package zyxhj.cms.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;

import zyxhj.cms.repository.ReplyRepository;
import zyxhj.core.domain.Reply;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSQL;
import zyxhj.utils.data.ts.TSQL.OP;
import zyxhj.utils.data.ts.TSUtils;

public class ReplyService {

	private static Logger log = LoggerFactory.getLogger(ReplyService.class);

	private ReplyRepository replyRepository;

	public ReplyService() {
		try {
			replyRepository = Singleton.ins(ReplyRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	// 创建回复
	public Reply createReply(SyncClient client, Long ownerId, Long upUserId, Long atUserId, String atUserName,
			String title, String text, String ext) throws ServerException {
		Reply reply = new Reply();
		reply._id = TSUtils.get_id(ownerId);
		reply.ownerId = ownerId;
		reply.createTime = new Date();
		reply.status = Reply.STATUS_UNEXAMINED;
		reply.upUserId = upUserId;
		reply.atUserId = atUserId;
		reply.atUserName = atUserName;
		reply.title = title;
		reply.text = text;
		reply.ext = ext;

		replyRepository.insert(client, reply, true);
		return reply;
	}

	// 编辑修改回复
	public void editReply(SyncClient client, Long ownerId, Long sequenceId, String title, String text, String ext)
			throws ServerException {
		Reply reply = new Reply();
		reply._id = TSUtils.get_id(ownerId);
		reply.ownerId = ownerId;
		reply.sequenceId = sequenceId;

		reply.title = title;
		reply.text = text;
		reply.ext = ext;

		replyRepository.update(client, reply, true);
	}

	public void examineReply(SyncClient client, Long ownerId, Long sequenceId, Byte status) throws ServerException {
		Reply reply = new Reply();
		reply._id = TSUtils.get_id(ownerId);
		reply.ownerId = ownerId;
		reply.sequenceId = sequenceId;

		if (status.equals(Reply.STATUS_ACCEPT) || status.equals(Reply.STATUS_REJECT)) {
			reply.status = status;

			replyRepository.update(client, reply, true);
		} else {
			throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, StringUtils.join("输入的状态异常>", status));
		}
	}

	// 删除回复
	public void delReply(SyncClient client, Long ownerId, Long sequenceId) throws ServerException {
		String _id = TSUtils.get_id(ownerId);
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("ownerId", ownerId).add("sequenceId", sequenceId)
				.build();
		replyRepository.delete(client, pk);
	}

	// 根据状态获取回复评论，没有状态则获取全部回复评论
	public JSONObject getReplyList(SyncClient client, Long ownerId, Long status, Integer count, Integer offset)
			throws Exception {
		String _id = TSUtils.get_id(ownerId);
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("ownerId", ownerId).build();
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "status", status);
		SearchQuery query = ts.build();
		return replyRepository.search(client, query);
	}
}
