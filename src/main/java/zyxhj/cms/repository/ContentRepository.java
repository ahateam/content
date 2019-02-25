package zyxhj.cms.repository;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;

import zyxhj.cms.domain.Content;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

public class ContentRepository extends RDSRepository<Content> {

	public ContentRepository() {
		super(Content.class);
	}

	/**
	 * RDS的FULLTEXT全文索引尝试失败，查不出东西</br>
	 * 将来准备用opensearch等工具替代，目前使用select like，只查询title字段
	 */
	public List<Content> searchContentsByKeyword(DruidPooledConnection conn, Byte type, Byte status, Long upUserId,
			Long upChannelId, String keywords, Integer count, Integer offset) throws ServerException {

		ArrayList<Object> objs = new ArrayList<>();

		StringBuffer sb = new StringBuffer();
		sb.append("WHERE TRUE ");
		if (null != type) {
			sb.append("AND type=? ");
			objs.add(type);
		}
		if (null != status) {
			sb.append("AND status=? ");
			objs.add(status);
		}
		if (null != upUserId) {
			sb.append("AND up_user_id=? ");
			objs.add(upUserId);
		}
		if (null != upChannelId) {
			sb.append("AND up_channel_id=? ");
			objs.add(upChannelId);
		}
		sb.append("AND title LIKE '%").append(keywords).append("%'");

		return this.getList(conn, sb.toString(), objs.toArray(), count, offset);
	}

	public List<Content> queryContentsByTags(DruidPooledConnection conn, Byte type, Byte status, Long upUserId,
			Long upChannelId, String groupKeyword, JSONArray tags, Integer count, Integer offset)
			throws ServerException {
		// SELECT * FROM `tb_content` WHERE JSON_CONTAINS(tags->'$.k1', '"t1"');

		ArrayList<Object> objs = new ArrayList<>();

		StringBuffer sb = new StringBuffer();
		sb.append("WHERE TRUE ");
		if (null != type) {
			sb.append("AND type=? ");
			objs.add(type);
		}
		if (null != status) {
			sb.append("AND status=? ");
			objs.add(status);
		}
		if (null != upUserId) {
			sb.append("AND up_user_id=? ");
			objs.add(upUserId);
		}
		if (null != upChannelId) {
			sb.append("AND up_channel_id=? ");
			objs.add(upChannelId);
		}

		// JSON_CONTAINS(tags, JSON_ARRAY("tag2","tag3"), '$.kind_type')
		sb.append("AND JSON_CONTAINS(tags, JSON_ARRAY(");
		for (Object tag : tags) {
			sb.append("\"").append(tag).append("\",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("), '$.").append(groupKeyword).append("')");

		return this.getList(conn, sb.toString(), objs.toArray(), count, offset);
	}

	public JSONArray getContentTags(DruidPooledConnection conn, Long contentId, String groupKeyword)
			throws ServerException {
		return this.getTags(conn, "tags", groupKeyword, "WHERE id=?", new Object[] { contentId });
	}

	public int setContentTags(DruidPooledConnection conn, Long contentId, String groupKeyword, JSONArray tags)
			throws ServerException {
		return this.setTags(conn, "tags", groupKeyword, tags, "WHERE id=?", new Object[] { contentId });
	}

	public int addContentTag(DruidPooledConnection conn, Long contentId, String groupKeyword, String tag)
			throws ServerException {
		return this.addTag(conn, "tags", groupKeyword, tag, "WHERE id=?", new Object[] { contentId });
	}

	public int delContentTag(DruidPooledConnection conn, Long contentId, String groupKeyword, String tag)
			throws ServerException {
		return this.delTag(conn, "tags", groupKeyword, tag, "WHERE id=?", new Object[] { contentId });
	}

}
