package zyxhj.cms.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;

import zyxhj.cms.domian.Bookmark;
import zyxhj.cms.domian.Content;
import zyxhj.cms.domian.Template;
import zyxhj.cms.repository.BookmarkRepository;
import zyxhj.cms.repository.ContentRepository;
import zyxhj.cms.repository.TemplateRepository;
import zyxhj.core.domain.User;
import zyxhj.core.service.UserService;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.EXP;
import zyxhj.utils.data.ts.ColumnBuilder;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSQL;
import zyxhj.utils.data.ts.TSQL.OP;
import zyxhj.utils.data.ts.TSRepository;
import zyxhj.utils.data.ts.TSUtils;

public class ContentService {

	private static Logger log = LoggerFactory.getLogger(ContentService.class);

	private ContentRepository contentRepository;
	private UserService userService;
	private TemplateRepository templateRepository;
	private BookmarkRepository bookmarkRepository;

	public ContentService() {
		try {
			contentRepository = Singleton.ins(ContentRepository.class);
			userService = Singleton.ins(UserService.class);
			templateRepository = Singleton.ins(TemplateRepository.class);
			bookmarkRepository = Singleton.ins(BookmarkRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 验证内容是否合法：判断是否下线，等等。TODO 目前待完善
	 */
	public JSONObject auth(DruidPooledConnection conn, SyncClient client, Long contentId) throws Exception {
		// 先判断user是否存在
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "id", contentId);
		ts.setLimit(1);
		ts.setOffset(0);
		SearchQuery query = ts.build();
		JSONObject content = TSRepository.nativeSearch(client, contentRepository.getTableName(), "ContentIndex", query);

		if (null == content) {
			// content不存在
			throw new ServerException(BaseRC.CMS_CONTENT_NOT_EXISET);
		} else {
			// 再判断content状态是否有效，TODO 目前status没有启用
			return content;
		}
	}

	public Content addContent(SyncClient client, String module, Byte type, Byte status, Long upUserId, Long upChannelId,
			String title, String data, Byte paid) throws Exception {

		Long id = IDUtils.getSimpleId();
		Content c = new Content();

		c._id = TSUtils.get_id(id);
		c.id = id;

		c.module = module;
		c.type = (long) type;
		c.status = (long) status;

		c.createTime = new Date();
		c.updateTime = c.createTime;
		c.upUserId = upUserId;
		if (upChannelId == null) {
			c.upChannelId = 0L;
		} else {
			c.upChannelId = upChannelId;
		}

		c.title = title;
		if (StringUtils.isBlank(data)) {
		} else {
			c.data = data;
		}
		c.proviteData = "{}";
		c.tags = "[]";// 设置为JSON数组的空格式
		c.paid = 0L;
		c.ext = "";
		contentRepository.insert(client, c, false);

		return c;
	}

	public void editContent(SyncClient client, String _id, Long id, Byte status, Long upChannelId, String title,
			String tags, String data) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("id", id).build();
		ColumnBuilder cb = new ColumnBuilder();
		if (status == Content.STATUS.PUBLISHED.v()) {
			cb.add("status", (long) status);
			if (upChannelId != null) {
				cb.add("upChannelid", upChannelId);
			}
			if (tags != null) {
				cb.add("tags", tags);
			}
			cb.add("title", title);
			cb.add("data", data);
			cb.add("updateTime", new Date().getTime());
		} else if (status == Content.STATUS.PUBLISHEDFAIL.v()) {
			cb.add("status", (long) Content.STATUS.PUBLISHEDFAIL.v());
			cb.add("updateTime", new Date().getTime());
		}
		List<Column> columns = cb.build();
		TSRepository.nativeUpdate(client, contentRepository.getTableName(), pk, true, columns);

	}

	/**
	 * 根据编号删除内容
	 */
	public void delContentById(SyncClient client, String id, Long contentId) throws Exception {
		Content renew = new Content();
		renew.status = (long) Content.STATUS.DELETED.v();
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", id).add("id", contentId).build();
		TSRepository.nativeDel(client, contentRepository.getTableName(), pk);
	}

	/**
	 * 根据内容编号查询内容
	 */
	public JSONObject getContentById(SyncClient client, String id, Long contentId) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", id).add("id", contentId).build();
		return TSRepository.nativeGet(client, contentRepository.getTableName(), pk);
	}

	/**
	 * 类容列表
	 */
	public JSONArray getContents(DruidPooledConnection conn, SyncClient client, String module, Byte status, Byte paid,
			Byte type, String tags, Integer count, Integer offset) throws Exception {

		TSQL ts = new TSQL();
		ts.Terms(OP.AND, "module", module);
		if (status != null) {
			ts.Term(OP.AND, "status", (long) status);
		}
		if (paid != null) {
			ts.Term(OP.AND, "paid", (long) paid);
		}
		if (type != null) {
			ts.Term(OP.AND, "type", (long) type);
		}
		if (tags != null) {
			System.out.println("11111");
			ts.Terms(OP.AND, "tags", tags);
		}
		ts.addSort(new FieldSort("updateTime", SortOrder.DESC));
		ts.setLimit(count);
		ts.setOffset(offset);
		SearchQuery query = ts.build();
		JSONObject con = TSRepository.nativeSearch(client, contentRepository.getTableName(), "ContentIndex", query);
		JSONArray json = con.getJSONArray("list");
		for (int i = 0; i < json.size(); i++) {
			JSONObject j = json.getJSONObject(i);
			User user = userService.getUserById(conn, j.getLong("upUserId"));
			json.getJSONObject(i).put("user", user);
		}
		return json;
	}

//	public JSONObject getContentByType(SyncClient client, Byte type, Integer count, Integer offset) throws Exception {
//		TSQL ts = new TSQL();
//		ts.Term(OP.AND, "type", (long) type).Term(OP.AND, "status", (long) Content.STATUS.NORMAL.v()).Term(OP.AND,
//				"paid", (long) 0);
//		ts.setLimit(count);
//		ts.setOffset(offset);
//		SearchQuery query = ts.build();
//		return TSRepository.nativeSearch(client, contentRepository.getTableName(), "ContentIndex", query);
//	}

	/**
	 * 根据关键字搜索内容
	 */
	public JSONObject searchContentsByKeyword(SyncClient client, Byte type, Byte status, Long upUserId,
			Long upChannelId, String keywords, Integer count, Integer offset) throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "type", (long) type).Term(OP.AND, "status", (long) status).Term(OP.AND, "upUserId", upUserId)
				.Term(OP.AND, "upChannelId", upChannelId).Match(OP.AND, "title", keywords)
				.Term(OP.AND, "paid", (long) 0);
		ts.setLimit(count);
		ts.setOffset(offset);
		SearchQuery query = ts.build();
		return TSRepository.nativeSearch(client, contentRepository.getTableName(), "ContentIndex", query);
	}

	/**
	 * 根据标签查询内容
	 */
	public JSONArray queryContentsByTags(DruidPooledConnection conn, SyncClient client, String module, Byte status,
			String groupKeyword, Integer count, Integer offset) throws Exception {
		TSQL ts = new TSQL();
		ts.Terms(OP.AND, "module", module).Term(OP.AND, "status", (long) status).Terms(OP.AND, "tags", groupKeyword)
				.Term(OP.AND, "paid", (long) 0);
		ts.setLimit(count);
		ts.setOffset(offset);
		SearchQuery query = ts.build();
		JSONObject con = TSRepository.nativeSearch(client, contentRepository.getTableName(), "ContentIndex", query);
		JSONArray json = con.getJSONArray("list");
		for (int i = 0; i < json.size(); i++) {
			JSONObject j = json.getJSONObject(i);
			User user = userService.getUserById(conn, j.getLong("upUserId"));
			json.getJSONObject(i).put("user", user);
		}
		return json;
	}

	/**
	 * 读取内容对应的标签
	 * 
	 */
	public JSONObject getContentTagsById(SyncClient client, Long contentId, String groupKeyword) throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "id", contentId).Match(OP.AND, "tags", groupKeyword);
		SearchQuery query = ts.build();
		return TSRepository.nativeSearch(client, contentRepository.getTableName(), "ContentIndex", query);
//		return contentRepository.getContentTags(conn, contentId, groupKeyword);
	}

	/**
	 * 为内容设置标签（覆盖）
	 */
	public void setContentTags(SyncClient client, String id, Long contentId, String groupKeyword) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", id).add("id", contentId).build();
		ColumnBuilder cb = new ColumnBuilder();
		cb.add("tags", groupKeyword);
		List<Column> columns = cb.build();
		TSRepository.nativeUpdate(client, contentRepository.getTableName(), pk, true, columns);
	}

	/**
	 * 为内容添加标签
	 */
	public void addContentTag(DruidPooledConnection conn, String _id, Long contentId, String groupKeyword, String tag)
			throws Exception {
//		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("id", contentId).build();
//		ColumnBuilder cb = new ColumnBuilder();
//		cb.add("tags", groupKeyword);
//		return contentRepository.addContentTag(conn, contentId, groupKeyword, tag);
	}

	/**
	 * 移除内容的标签
	 */
	public void delContentTag(SyncClient client, String id, Long contentId, String groupKeyword) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", id).add("id", contentId).build();
		ColumnBuilder cb = new ColumnBuilder();
		cb.add("tags", "{}");
		List<Column> columns = cb.build();
		TSRepository.nativeUpdate(client, contentRepository.getTableName(), pk, true, columns);
	}

	/**
	 * 获取用户发布内容
	 */
	public JSONObject getContentByUpUserId(SyncClient client, String module, Long upUserId, Integer count,
			Integer offset) throws Exception {
		TSQL ts = new TSQL();
		ts.Terms(OP.AND, "module", module).Term(OP.AND, "upUserId", upUserId);
		ts.addSort(new FieldSort("createTime", SortOrder.DESC));
		ts.setLimit(count);
		ts.setOffset(offset);
		SearchQuery query = ts.build();
		return TSRepository.nativeSearch(client, contentRepository.getTableName(), "ContentIndex", query);
	}

	public JSONArray returnTabBar() {
		JSONArray json = new JSONArray();
		JSONObject jo1 = new JSONObject();
		jo1.put("iconPath", "/static/image/release.png");
		jo1.put("selectedIconPath", "/static/image/release.png");
		jo1.put("text", "发图文");
		jo1.put("active", true);
		jo1.put("url", "/pages/index/addContent/addContent?type=1");
		json.add(jo1);
		JSONObject jo2 = new JSONObject();
		jo2.put("iconPath", "/static/image/video.png");
		jo2.put("selectedIconPath", "/static/image/video.png");
		jo2.put("text", "发视频");
		jo2.put("active", false);
		jo2.put("url", "/pages/index/addContent/addContent?type=0");
		json.add(jo2);
		return json;
	}

	// 创建模板
	public void addTemplate(DruidPooledConnection conn, String module, String name, String tags, String data,
			Double money, Byte status, Byte type) throws Exception {
		Template te = new Template();
		te.id = IDUtils.getSimpleId();
		te.name = name;
		te.module = module;
		te.tags = tags;
		te.data = data;
		te.money = money;
		te.status = status;
		te.type = type;
		templateRepository.insert(conn, te);

	}

	public List<Template> getTemplate(DruidPooledConnection conn, String module, Byte type, Byte status, String tags,
			Integer count, Integer offset) throws Exception {
		EXP ex = EXP.INS();
		ex.key("module", module);
		if(type != null) {
			ex.andKey("type", type);
		}
		if(status != null) {
			ex.andKey("status", status);
		}
		if(tags != null) {
			ex.and(EXP.JSON_CONTAINS("tags", "$", tags));
		}
		return templateRepository.getList(conn, ex, count, offset);
	}

	public List<Template> getTemplateByTag(DruidPooledConnection conn, String module, String tags, Byte type,
			Integer count, Integer offset) throws Exception {
		return templateRepository.getList(conn,
				EXP.INS().key("module", module).andKey("type", type).and(EXP.JSON_CONTAINS("tags", "$", tags)), count,
				offset);
	}

	// 购买内容
	public void addBookmark(DruidPooledConnection conn, String module, Long userId, Long contentId) throws Exception {
		Bookmark bo = new Bookmark();
		bo.userId = userId;
		bo.contentId = contentId;
		bo.module = module;
		bo.createTime = new Date();

		try {
			bookmarkRepository.insert(conn, bo);
		} catch (ServerException e) {
			throw new ServerException(BaseRC.CONTENT_BY_ERROR);
		}
	}

	// 查看是否购买当前内容 如果没购买 则返回空值 如已经购买 则返回相应的内容
	public JSONObject checkBookmark(SyncClient client, DruidPooledConnection conn, Long userId, Long contentId)
			throws Exception {
		Bookmark bookmark = bookmarkRepository.get(conn,
				EXP.INS().key("user_id", userId).andKey("content_id", contentId));
		if (bookmark != null) {
			JSONArray json = new JSONArray();
			json.add(contentId);
			return getContentsByIds(client, json);
		} else {
			return null;
		}
	}

	// 根据内容id获取内容列表
	private JSONObject getContentsByIds(SyncClient client, JSONArray contentIds) throws Exception {
		TSQL ts = new TSQL();
		if (contentIds != null && contentIds.size() > 0) {
			for (int i = 0; i < contentIds.size(); i++) {
				ts.Term(OP.OR, "id", contentIds.get(i));
			}
			SearchQuery query = ts.build();
			return contentRepository.search(client, query);
		} else {
			return null;
		}
	}

	// 获取用户购买的内容
	public JSONObject getUserBuyContent(SyncClient client, DruidPooledConnection conn, Long userId) throws Exception {
		List<Bookmark> list = bookmarkRepository.getList(conn, EXP.INS().key("user_id", userId), 512, 0);
		if (list != null && list.size() > 0) {
			JSONArray json = new JSONArray();
			for (Bookmark bookmark : list) {
				json.add(bookmark.contentId);
			}
			return getContentsByIds(client, json);
		} else {
			return null;
		}

	}

}
