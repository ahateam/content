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
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;

import zyxhj.cms.domian.Content;
import zyxhj.cms.repository.ContentRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.ts.ColumnBuilder;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSQL;
import zyxhj.utils.data.ts.TSQL.OP;
import zyxhj.utils.data.ts.TSRepository;
import zyxhj.utils.data.ts.TSUtils;

public class ContentService {

	private static Logger log = LoggerFactory.getLogger(ContentService.class);

	private ContentRepository contentRepository;

	public ContentService() {
		try {
			contentRepository = Singleton.ins(ContentRepository.class);
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

	private Content addContent(SyncClient client, String module, Byte type, Byte status, Long upUserId,
			Long upChannelId, String title, String data, String text) throws Exception {

		Long id = IDUtils.getSimpleId();
		Content c = new Content();

		c._id = TSUtils.get_id(id);
		c.id = id;

		c.module = module;
		c.type = (long) type;
		if (status == Content.STATUS.DRAFT.v() || status == Content.STATUS.NORMAL.v()) {
			c.status = (long) status;
		} else {
			c.status = (long) Content.STATUS.NORMAL.v();// 首次创建时是正常
		}

		c.createTime = new Date();
		c.updateTime = c.createTime;
		c.upUserId = upUserId;
		c.upChannelId = upChannelId;

		c.title = title;
		if (StringUtils.isBlank(data)) {
		} else {
			c.data = data;
		}
		c.text = text;
		c.tags = "{}";// 设置为JSON数组的空格式，否则后续的编辑操作会没效果（可能是MYSQL的bug）
		c.paymentOrNot = 0L;
		c.ext = "";
		contentRepository.insert(client, c, false);

		return c;
	}

	/**
	 * 创建内容（默认为草稿）
	 */
	public Content createContentDraft(SyncClient client, String module, Byte type, Long upUserId, Long upChannelId,
			String title, String data, String text) throws Exception {
		return addContent(client, module, type, Content.STATUS.DRAFT.v(), upUserId, upChannelId, title, data, text);
	}

	/**
	 * 创建内容（默认为正常状态，已发布）
	 */
	public Content createContentPublished(SyncClient client, String module, Byte type, Long upUserId, Long upChannelId,
			String title, String data, String text) throws Exception {
		return addContent(client, module, type, Content.STATUS.NORMAL.v(), upUserId, upChannelId, title, data, text);
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
	public JSONArray getContents(SyncClient client, Integer count, Integer offset) throws Exception {

		PrimaryKey pkStart = new PrimaryKeyBuilder().add("_id", PrimaryKeyValue.INF_MIN)
				.add("id", PrimaryKeyValue.INF_MIN).build();

		PrimaryKey pkEnd = new PrimaryKeyBuilder().add("_id", PrimaryKeyValue.INF_MAX)
				.add("id", PrimaryKeyValue.INF_MAX).build();

		return TSRepository.nativeGetRange(client, contentRepository.getTableName(), pkStart, pkEnd, count, offset);
	}

	/**
	 * 根据关键字搜索内容
	 */
	public JSONObject searchContentsByKeyword(SyncClient client, Byte type, Byte status, Long upUserId,
			Long upChannelId, String keywords, Integer count, Integer offset) throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "type", (long) type).Term(OP.AND, "status", (long) status).Term(OP.AND, "upUserId", upUserId)
				.Term(OP.AND, "upChannelId", upChannelId).Match(OP.AND, "title", keywords);
		ts.setLimit(count);
		ts.setOffset(offset);
		SearchQuery query = ts.build();
		return TSRepository.nativeSearch(client, contentRepository.getTableName(), "ContentIndex", query);
	}

	/**
	 * 根据标签查询内容
	 */
	public JSONObject queryContentsByTags(SyncClient client, Byte type, Byte status, Long upUserId, Long upChannelId,
			String groupKeyword, Integer count, Integer offset) throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "type", (long) type).Term(OP.AND, "status", (long) status).Term(OP.AND, "upUserId", upUserId)
				.Term(OP.AND, "upChannelId", upChannelId).MatchPhrase(OP.AND, "tags", groupKeyword);
		ts.setLimit(count);
		ts.setOffset(offset);
		SearchQuery query = ts.build();
		return TSRepository.nativeSearch(client, contentRepository.getTableName(), "ContentIndex", query);

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
		TSRepository.nativeUpdate(client, contentRepository.getTableName(), pk, columns);
	}

	/**
	 * 为内容添加标签
	 */
	public void addContentTag(DruidPooledConnection conn, Long contentId, String groupKeyword, String tag)
			throws Exception {

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
		TSRepository.nativeUpdate(client, contentRepository.getTableName(), pk, columns);
	}

	/**
	 * 
	 */

}
