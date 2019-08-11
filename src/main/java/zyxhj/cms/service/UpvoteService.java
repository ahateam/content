package zyxhj.cms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;

import zyxhj.cms.domian.Upvote;
import zyxhj.cms.repository.UpvoteRepository;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSQL;
import zyxhj.utils.data.ts.TSQL.OP;
import zyxhj.utils.data.ts.TSRepository;

public class UpvoteService {

	private static Logger log = LoggerFactory.getLogger(UpvoteService.class);

	private UpvoteRepository upvoteRepository;

	public UpvoteService() {
		try {
			upvoteRepository = Singleton.ins(UpvoteRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	// 创建点赞
	public void createUpvote(SyncClient client, Long contentId, Long userId, Byte type) throws Exception {
		Boolean checkUpvote = checkUpvote(client, contentId, userId);
		if (!checkUpvote) {
			Upvote uv = new Upvote();
			uv.contentId = contentId;
			uv.userId = userId;
			uv.Type = (long) type;
			upvoteRepository.insert(client, uv, false);
		} else {
			throw new ServerException(BaseRC.UP_VOTE_ERROR);
		}
	}

	// 获取点赞总数
	public Integer countUpvote(SyncClient client, Long contentId) throws Exception {
		TSQL ts = new TSQL();
		ts.Match(OP.AND, "contentId", contentId.toString());
		ts.setLimit(0);
		ts.setGetTotalCount(true);
		SearchQuery query = ts.build();
		JSONObject upvote = TSRepository.nativeSearch(client, upvoteRepository.getTableName(), "UpvoteIndex", query);

		return upvote.getInteger("totalCount");
	}

	// 查看用户是否投票
	public boolean checkUpvote(SyncClient client, Long contentId, Long userId) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("contentId", contentId).add("userId", userId).build();

		JSONObject upVote = TSRepository.nativeGet(client, upvoteRepository.getTableName(), pk);
		if (upVote != null) {
			return true;
		} else {
			return false;
		}

	}

}
