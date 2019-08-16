package zyxhj.cms.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import zyxhj.cms.domian.ContentTag;
import zyxhj.cms.domian.ContentTagGroup;
import zyxhj.cms.repository.ContentTagGroupRepository;
import zyxhj.cms.repository.ContentTagRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.EXP;

/**
 * 内容标签service
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
	 * 创建自定义标签
	 */
	public ContentTag createTag(DruidPooledConnection conn, String module, Long groupId, String groupKeyword,
			String name) throws Exception {
		ContentTag tag = new ContentTag();

		tag.id = IDUtils.getSimpleId();
		tag.status = ContentTag.STATUS.ENABLED.v();
		tag.groupId = groupId;
		tag.groupKeyword = groupKeyword;
		tag.name = name;
		tag.module = module;
		tagRepository.insert(conn, tag);
		return tag;
	}

	/**
	 * 启用/禁用标签
	 */
	public void editTagStatus(DruidPooledConnection conn, Long tagId, Byte status) throws Exception {

		ContentTag tag = new ContentTag();
		tag.status = status;

		tagRepository.update(conn, EXP.INS().and("id", "=", tagId), tag, true);

//		tagRepository.updateByKey(conn, "id", tagId, tag, true);

	}

	/**
	 * 根据状态获取标签列表
	 */
	public List<ContentTag> getTags(DruidPooledConnection conn, String module, Byte status, String keyword,
			Integer count, Integer offset) throws Exception {

		return tagRepository.getList(conn,
				EXP.INS().and("module", "=", module).and("status", "=", status).and("group_keyword", "=", keyword),
				count, offset);
	}

	/**
	 * 创建内容标签分组(不允许编辑和删除,纯粹用于管理标签)
	 */
	public ContentTagGroup createTagGroup(DruidPooledConnection conn, String module, Byte tagGroupType, String type,
			String keyword, String remark) throws Exception {

		ContentTagGroup group = new ContentTagGroup();
		group.id = IDUtils.getSimpleId();
		group.module = module;
		group.type = type;
		group.keyword = keyword;
		group.remark = remark;
		group.status = ContentTagGroup.STATUS.OPEN.v();
		if (tagGroupType == ContentTagGroup.TAGGROUPTYPE.HOME.v()) {
			group.tagGroupType = ContentTagGroup.TAGGROUPTYPE.HOME.v();
		} else if (tagGroupType == ContentTagGroup.TAGGROUPTYPE.VIP.v()) {
			group.tagGroupType = ContentTagGroup.TAGGROUPTYPE.VIP.v();
		} else if (tagGroupType == ContentTagGroup.TAGGROUPTYPE.TASK.v()) {
			group.tagGroupType = ContentTagGroup.TAGGROUPTYPE.TASK.v();
		} else if (tagGroupType == ContentTagGroup.TAGGROUPTYPE.TEMPLATE.v()) {
			group.tagGroupType = ContentTagGroup.TAGGROUPTYPE.TEMPLATE.v();
		}

		groupRepository.insert(conn, group);

		return group;
	}

	/**
	 * 获取标签分组类型列表
	 */
	public List<ContentTagGroup> getTagGroupTypes(DruidPooledConnection conn, String module, Byte type)
			throws Exception {
		return groupRepository.getList(conn, EXP.INS().and("module", "=", module).and("tag_group_type", "=", type)
				.and("status", "=", ContentTagGroup.STATUS.OPEN.v()), 512, 0);
	}

	/**
	 * 根据编号获取自定义标签
	 */
	public ContentTag getTagById(DruidPooledConnection conn, Long tagId) throws Exception {
		// 先从系统缓存里取，再从缓存去，最后再查
		ContentTag tag = CONTENT_TAG_CACHE.getIfPresent(tagId);
		if (tag == null) {
			// 从数据库中获取
			tag = tagRepository.get(conn, EXP.INS().and("id", "=", tagId));
			if (tag != null) {
				// 放入缓存
				CONTENT_TAG_CACHE.put(tagId, tag);
			}
		}
		return tag;
	}

}
