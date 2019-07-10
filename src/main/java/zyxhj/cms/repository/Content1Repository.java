package zyxhj.cms.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;

import zyxhj.cms.domain.Content1;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;
import zyxhj.utils.data.rds.SQL;

public class Content1Repository extends RDSRepository<Content1> {

	public Content1Repository() {
		super(Content1.class);
	}

	/**
	 * RDS的FULLTEXT全文索引尝试失败，查不出东西</br>
	 * 将来准备用opensearch等工具替代，目前使用select like，只查询title字段
	 */
	public List<Content1> searchContentsByKeyword(DruidPooledConnection conn, Byte type, Byte status, Long upUserId,
			Long upChannelId, String keywords, Integer count, Integer offset) throws ServerException {

		StringBuffer sb = new StringBuffer("WHERE ");
		SQL sql = new SQL();
		sql.addExValid("type=?", type);
		sql.ANDValid("status=?", status);
		sql.ANDValid("up_user_id=?", upUserId);
		sql.ANDValid("up_channel_id=?", upChannelId);
		sql.AND(StringUtils.join("AND title LIKE '%", keywords, "%'"));

		sql.fillSQL(sb);

		return this.getList(conn, sb.toString(), sql.getParams(), count, offset);
	}

	public List<Content1> queryContentsByTags(DruidPooledConnection conn, Byte type, Byte status, Long upUserId,
			Long upChannelId, String groupKeyword, String[] tags, Integer count, Integer offset)
			throws ServerException {

		StringBuffer sb = new StringBuffer("WHERE ");
		SQL sql = new SQL();
		sql.addExValid("type=?", type);
		sql.ANDValid("status=?", status);
		sql.ANDValid("up_user_id=?", upUserId);
		sql.ANDValid("up_channel_id=?", upChannelId);

		sql.fillSQL(sb);
		return getListByTagsJSONArray(conn, "tags", groupKeyword, tags, sb.toString(), sql.getParams(), count, offset);
	}

	public JSONArray getContentTags(DruidPooledConnection conn, Long contentId, String groupKeyword)
			throws ServerException {
		return getTags(conn, "tags", groupKeyword, "WHERE id=?", new Object[] { contentId });
	}

	public int setContentTags(DruidPooledConnection conn, Long contentId, String groupKeyword, JSONArray tags)
			throws ServerException {
		return setTags(conn, "tags", groupKeyword, tags, "WHERE id=?", new Object[] { contentId });
	}

	public int addContentTag(DruidPooledConnection conn, Long contentId, String groupKeyword, String tag)
			throws ServerException {
		return addTag(conn, "tags", groupKeyword, tag, "WHERE id=?", new Object[] { contentId });
	}

	public int delContentTag(DruidPooledConnection conn, Long contentId, String groupKeyword, String tag)
			throws ServerException {
		return delTag(conn, "tags", groupKeyword, tag, "WHERE id=?", new Object[] { contentId });
	}

}
