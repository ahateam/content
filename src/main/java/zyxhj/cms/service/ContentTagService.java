package zyxhj.cms.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import zyxhj.cms.domain.ContentTag;
import zyxhj.cms.domain.ContentTagGroup;
import zyxhj.cms.repository.ContentTagGroupRepository;
import zyxhj.cms.repository.ContentTagRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;

/**
 * 第三方用户自定义角色service
 *
 */
public class ContentTagService {

	private static Logger log = LoggerFactory.getLogger(ContentTagService.class);

	private static Cache<Long, ContentTag> CONTENT_TAG_CACHE = CacheBuilder.newBuilder()//
			.expireAfterAccess(5, TimeUnit.MINUTES)//
			.maximumSize(1000)//
			.build();

	private ContentTagRepository tagRepository;
	private ContentTagGroupRepository groupRepository;

	public ContentTagService() {
		try {
			tagRepository = Singleton.ins(ContentTagRepository.class);
			groupRepository = Singleton.ins(ContentTagGroupRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 创建自定义角色
	 */
	public ContentTag createTag(DruidPooledConnection conn, String groupKeyword, String name) throws Exception {
		ContentTag tag = new ContentTag();
		tag.id = IDUtils.getSimpleId();
		tag.status = ContentTag.STATUS.ENABLED.v();
		tag.groupKeyword = groupKeyword;
		tag.name = name;

		tagRepository.insert(conn, tag);

		return tag;
	}

	/**
	 * 启用/禁用标签
	 */
	public int editTagStatus(DruidPooledConnection conn, Long tagId, Byte status) throws Exception {
		ContentTag renew = new ContentTag();
		if (status == ContentTag.STATUS.DISABLED.v()) {
			renew.status = status;
		} else {
			renew.status = ContentTag.STATUS.ENABLED.v();
		}

		return tagRepository.updateByKey(conn, "id", tagId, renew, true);
	}

	/**
	 * 根据状态获取标签列表
	 */
	public List<ContentTag> getTags(DruidPooledConnection conn, Byte status, String groupKeyword, Integer count,
			Integer offset) throws Exception {
		return tagRepository.getListByANDKeys(conn, new String[] { "status", "group_keyword" },
				new Object[] { status, groupKeyword }, count, offset);
	}

	/**
	 * 根据编号获取自定义标签
	 */
	public ContentTag getTagById(DruidPooledConnection conn, Long orgId, Long tagId) throws Exception {
		// 先从系统缓存里取，再从缓存去，最后再查
		ContentTag tag = CONTENT_TAG_CACHE.getIfPresent(tagId);
		if (tag == null) {
			// 从数据库中获取
			tag = tagRepository.getByKey(conn, "id", tagId);
			if (tag != null) {
				// 放入缓存
				CONTENT_TAG_CACHE.put(tagId, tag);
			}
		}
		return tag;
	}

	/**
	 * 创建内容标签分组(不允许编辑和删除,纯粹用于管理标签)
	 */
	public ContentTagGroup createTagGroup(DruidPooledConnection conn, String type, String keyword, String remark)
			throws Exception {

		ContentTagGroup group = new ContentTagGroup();
		group.type = type;
		group.keyword = keyword;
		group.remark = remark;

		groupRepository.insert(conn, group);

		return group;
	}

	/**
	 * 获取标签分组类型列表
	 */
	public List<String> getTagGroupTypes(DruidPooledConnection conn) throws Exception {
		return groupRepository.getContentTagGroupTypes(conn);
	}

}
