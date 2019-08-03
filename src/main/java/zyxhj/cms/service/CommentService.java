package zyxhj.cms.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;

import zyxhj.cms.domian.Comment;
import zyxhj.cms.repository.CommentRepository;
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

	public CommentService() {
		try {
			commentRepository = Singleton.ins(CommentRepository.class);
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

	// 获取内容评论或者是夸夸评论
	public JSONObject getCommentByContentId(SyncClient client, String module, Long contentId, Integer count,
			Integer offset) throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "contentId", contentId).Term(OP.AND, "module", module);
//		ts.addSort(new Sort(Arrays.asList()));
		ts.addSort(new FieldSort("upvote", SortOrder.ASC));
		ts.setLimit(count);
		ts.setLimit(offset);
		SearchQuery query = ts.build();
		return TSRepository.nativeSearch(client, commentRepository.getTableName(), "CommentIndex", query);

	}

	// 获取总评论数
	public JSONObject countCommentByContentId(SyncClient client, Long contentId) throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "contentId", contentId).MatchAll(OP.AND);
		ts.setLimit(0);
		ts.setGetTotalCount(true);
		SearchQuery query = ts.build();
		return TSRepository.nativeSearch(client, commentRepository.getTableName(), "CommentIndex", query);
	}

}
