package zyxhj.cms.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;

import zyxhj.cms.domian.Comment;
import zyxhj.cms.repository.CommentRepository;
import zyxhj.core.domain.User;
import zyxhj.core.service.UserService;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSQL;
import zyxhj.utils.data.ts.TSQL.OP;
import zyxhj.utils.data.ts.TSRepository;
import zyxhj.utils.data.ts.TSUtils;

public class CommentService {

	private static Logger log = LoggerFactory.getLogger(CommentService.class);

	private CommentRepository commentRepository;
	private UpvoteService upvoteService;
	private UserService userService;

	public CommentService() {
		try {
			commentRepository = Singleton.ins(CommentRepository.class);
			upvoteService = Singleton.ins(UpvoteService.class);
			userService = Singleton.ins(UserService.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	// 创建评论
	public Comment createComment(SyncClient client, String module, Long contentId, Long userId, String commentContent,
			String data) throws Exception {
		Comment co = new Comment();
		Long id = IDUtils.getSimpleId();
		co._id = TSUtils.get_id(id);
		co.id = id;
		co.module = module;
		co.contentId = contentId;
		co.userId = userId;
		co.createTime = new Date();
		co.commentContent = commentContent;
		co.data = data;
		commentRepository.insert(client, co, true);
		return co;
	}

	// 删除评论
	public void delComment(SyncClient client, String _id, Long id) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("id", id).build();
		commentRepository.delete(client, pk);

	}

//	// 获取内容评论或者是夸夸评论
//	public JSONObject getCommentByContentId(SyncClient client, String module, Long contentId, Integer count,
//			Integer offset) throws Exception {
//		TSQL ts = new TSQL();
//		ts.Term(OP.AND, "contentId", contentId).Term(OP.AND, "module", module);
//		ts.addSort(new FieldSort("createTime", SortOrder.ASC));
//		ts.setLimit(count);
//		ts.setOffset(offset);
//		SearchQuery query = ts.build();
//		return TSRepository.nativeSearch(client, commentRepository.getTableName(), "CommentIndex", query);
//
//	}

	// 获取总评论数

	// TODO  使用索引
	public JSONObject getCommentByContentId(DruidPooledConnection conn, SyncClient client, String module,
			Long contentId, Integer count, Integer offset) throws Exception {
		TSQL ts = new TSQL();
		ts.Match(OP.AND, "contentId", contentId.toString()).Term(OP.AND, "module", module);
		ts.addSort(new FieldSort("createTime", SortOrder.ASC));
		ts.setOffset(0);
		ts.setLimit(10);
		ts.setGetTotalCount(true);
		SearchQuery query = ts.build();
		JSONObject comment = TSRepository.nativeSearch(client, commentRepository.getTableName(), "CommentIndex", query);
		JSONArray json = comment.getJSONArray("list");
		for (int i = 0; i < json.size(); i++) {
			JSONObject j = json.getJSONObject(i);
			Integer c = upvoteService.countUpvote(client, j.getLong("id"));
			json.getJSONObject(i).put("commentTotalCount", c);
		}
		Integer contentUpvote = upvoteService.countUpvote(client, contentId);
		comment.put("contentUpvote", contentUpvote);
		comment.put("list", json);
		for (int k = 0; k < json.size(); k++) {
			JSONObject j = json.getJSONObject(k);
			User user = userService.getUserById(conn, j.getLong("userId"));
			json.getJSONObject(k).put("user", user);
		}
		return comment;
	}

//	private Integer countCommentByContentId(SyncClient client, Long contentId) throws Exception {
//		TSQL ts = new TSQL();
//		ts.Match(OP.AND, "contentId", contentId.toString());
//		ts.setLimit(10);
//		ts.setGetTotalCount(true);
//		SearchQuery query = ts.build();
//		JSONObject comment = TSRepository.nativeSearch(client, commentRepository.getTableName(), "CommentIndex", query);
//		Integer count = comment.getInteger("totalCount");
//		return count;
//	}

}
