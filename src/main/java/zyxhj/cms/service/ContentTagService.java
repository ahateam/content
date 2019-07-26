package zyxhj.cms.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import zyxhj.cms.domian.ContentTag;
import zyxhj.cms.domian.ContentTagGroup;
import zyxhj.cms.repository.ContentTagGroupRepository;
import zyxhj.cms.repository.ContentTagRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.ts.ColumnBuilder;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSQL;
import zyxhj.utils.data.ts.TSQL.OP;
import zyxhj.utils.data.ts.TSRepository;
import zyxhj.utils.data.ts.TSUtils;

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
	 * 创建自定义标签
	 */
	public ContentTag createTag(SyncClient client, String module, String groupKeyword, String name) throws Exception {
		ContentTag tag = new ContentTag();

		Long id = IDUtils.getSimpleId();
		tag._id = TSUtils.get_id(id);
		tag.id = id;
		tag.status = (long) ContentTag.STATUS.ENABLED.v();
		tag.groupKeyword = groupKeyword;
		tag.name = name;
		tag.module = module;
		tagRepository.insert(client, tag, false);
		return tag;
	}

	/**
	 * 启用/禁用标签
	 */
	public void editTagStatus(SyncClient client, String _id, Long tagId, Byte status) throws Exception {

		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("id", tagId).build();

		ColumnBuilder cb = new ColumnBuilder();
		if (status == ContentTag.STATUS.DISABLED.v()) {
			cb.add("status", status);
		} else {
			cb.add("status", ContentTag.STATUS.ENABLED.v());
		}

		List<Column> columns = cb.build();

		TSRepository.nativeUpdate(client, tagRepository.getTableName(), pk, true, columns);

	}

	/**
	 * 根据状态获取标签列表
	 */
	public JSONObject getTags(SyncClient client, String module, Byte status, String keyword, Integer count,
			Integer offset) throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "module", module).Term(OP.AND, "groupKeyword", keyword).Term(OP.AND, "status", (long) status);
		ts.setLimit(count);
		ts.setOffset(offset);
		SearchQuery query = ts.build();

		return TSRepository.nativeSearch(client, tagRepository.getTableName(), "ContentTagIndex", query);

	}

	/**
	 * 创建内容标签分组(不允许编辑和删除,纯粹用于管理标签)
	 */
	public ContentTagGroup createTagGroup(SyncClient client, String module, Byte tagGroupType, String type,
			String keyword, String remark) throws Exception {

		ContentTagGroup group = new ContentTagGroup();
		Long id = IDUtils.getSimpleId();
		group._id = TSUtils.get_id(id);
		group.id = id;
		group.module = module;
		group.type = type;
		group.keyword = keyword;
		group.remark = remark;
		if (tagGroupType == ContentTagGroup.TAGGROUPTYPE.HOME.v()) {
			group.tagGroupType = (long) ContentTagGroup.TAGGROUPTYPE.HOME.v();
		} else if (tagGroupType == ContentTagGroup.TAGGROUPTYPE.VIP.v()) {
			group.tagGroupType = (long) ContentTagGroup.TAGGROUPTYPE.VIP.v();
		} else if (tagGroupType == ContentTagGroup.TAGGROUPTYPE.TASK.v()) {
			group.tagGroupType = (long) ContentTagGroup.TAGGROUPTYPE.TASK.v();
		}

		groupRepository.insert(client, group, false);

		return group;
	}

	/**
	 * 获取标签分组类型列表
	 */
	public JSONObject getTagGroupTypes(SyncClient client, String module, Byte type) throws Exception {
//		return groupRepository.getContentTagGroupTypes(conn);
//		return groupRepository.getList(conn, 512, 0);
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "module", module).Term(OP.AND, "tagGroupType", (long) type);
		SearchQuery query = ts.build();

		return TSRepository.nativeSearch(client, groupRepository.getTableName(), "ContentTagGroupIndex", query);
	}

//	/**
//	 * 根据编号获取自定义标签
//	 */
//	public ContentTag getTagById(SyncClient client, Long tagId) throws Exception {
//		// 先从系统缓存里取，再从缓存去，最后再查
//		ContentTag tag = CONTENT_TAG_CACHE.getIfPresent(tagId);
//		if (tag == null) {
//			// 从数据库中获取
//			tag = tagRepository.getByKey(conn, "id", tagId);
//			if (tag != null) {
//				// 放入缓存
//				CONTENT_TAG_CACHE.put(tagId, tag);
//			}
//		}
//		return tag;
//	}

//	/**
//	 * 创建自定义角色
//	 */
//	public ContentTag createTag(DruidPooledConnection conn, Long groupId, String groupKeyword, String name)
//			throws Exception {
//		ContentTag tag = new ContentTag();
//		tag.id = IDUtils.getSimpleId();
//		tag.groupId = groupId;
//		tag.status = ContentTag.STATUS.ENABLED.v();
//		tag.groupKeyword = groupKeyword;
//		tag.name = name;
//
//		tagRepository.insert(conn, tag);
//
//		return tag;
//	}

//	/**
//	 * 启用/禁用标签
//	 */
//	public int editTagStatus(DruidPooledConnection conn, Long tagId, Byte status) throws Exception {
//		ContentTag renew = new ContentTag();
//		if (status == ContentTag.STATUS.DISABLED.v()) {
//			renew.status = status;
//		} else {
//			renew.status = ContentTag.STATUS.ENABLED.v();
//		}
//
//		return tagRepository.updateByKey(conn, "id", tagId, renew, true);
//	}

//	/**
//	 * 根据状态获取标签列表
//	 */
//	public List<ContentTag> getTags(DruidPooledConnection conn, Byte status, String groupKeyword, Integer count,
//			Integer offset) throws Exception {
//		return tagRepository.getListByANDKeys(conn, new String[] { "status", "group_keyword" },
//				new Object[] { status, groupKeyword }, count, offset);
//	}

//	/**
//	 * 根据编号获取自定义标签
//	 */
//	public ContentTag getTagById(DruidPooledConnection conn, Long tagId) throws Exception {
//		// 先从系统缓存里取，再从缓存去，最后再查
//		ContentTag tag = CONTENT_TAG_CACHE.getIfPresent(tagId);
//		if (tag == null) {
//			// 从数据库中获取
//			tag = tagRepository.getByKey(conn, "id", tagId);
//			if (tag != null) {
//				// 放入缓存
//				CONTENT_TAG_CACHE.put(tagId, tag);
//			}
//		}
//		return tag;
//	}

//	/**
//	 * 创建内容标签分组(不允许编辑和删除,纯粹用于管理标签)
//	 */
//	public ContentTagGroup createTagGroup(DruidPooledConnection conn, String type, String keyword, String remark)
//			throws Exception {
//
//		ContentTagGroup group = new ContentTagGroup();
//		group.id = IDUtils.getSimpleId();
//		group.type = type;
//		group.keyword = keyword;
//		group.remark = remark;
//
//		groupRepository.insert(conn, group);
//
//		return group;
//	}

//	/**
//	 * 获取标签分组类型列表
//	 */
//	public List<ContentTagGroup> getTagGroupTypes(DruidPooledConnection conn) throws Exception {
////		return groupRepository.getContentTagGroupTypes(conn);
//		return groupRepository.getList(conn, 512, 0);
//	}

}
