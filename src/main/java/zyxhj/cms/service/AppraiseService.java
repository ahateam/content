package zyxhj.cms.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;

import zyxhj.cms.repository.AppraiseRepository;
import zyxhj.core.domain.Appraise;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.ts.ColumnBuilder;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSQL;
import zyxhj.utils.data.ts.TSQL.OP;
import zyxhj.utils.data.ts.TSUtils;


//赞
public class AppraiseService {
	private static Logger log = LoggerFactory.getLogger(AppraiseService.class);
	
	public AppraiseRepository appraiseRepository;
	
	public AppraiseService() {
		try {
			appraiseRepository = Singleton.ins(AppraiseRepository.class);
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	
	//创建点赞或菜
	public Appraise createAppraise(SyncClient client,Long ownerId,Long userId, Long value) throws ServerException {
		Appraise appraise = new Appraise();
		Long id = IDUtils.getSimpleId();
		appraise._id = TSUtils.get_id(id);
		appraise.ownerId = ownerId;
		appraise.userId = userId;
		appraise.value = value;
		appraiseRepository.insert(client, appraise, false);
		return appraise;
		
	}
	//删除点赞或踩
	public void delAppraise(SyncClient client,Long ownerId,Long userId) throws ServerException {
		
		PrimaryKey pk = null;
		if(userId != null) {//如果用户id为空，则删除此内容下的所有赞和踩，否则就只删除此用户在此内容下的赞和踩
			 pk = new PrimaryKeyBuilder().add("ownerId", ownerId).add("userId", userId).build();
		}else {
			 pk = new PrimaryKeyBuilder().add("ownerId", ownerId).build();
		}
		AppraiseRepository.nativeDel(client, appraiseRepository.getTableName(), pk);
	}
	//修改状态
	public void editAppraise(SyncClient client,Long ownerId,Long userId,Long value) throws ServerException {
		PrimaryKey pk = null;
		if(userId != null) {//如果用户id为空，则修改此内容下的所有赞和踩，否则就只修改此用户在此内容下的赞和踩
			 pk = new PrimaryKeyBuilder().add("ownerId", ownerId).add("userId", userId).build();
		}else {
			 pk = new PrimaryKeyBuilder().add("ownerId", ownerId).build();
		}
		ColumnBuilder cb = new ColumnBuilder();
		cb.add("value", value);
		List<Column> columns = cb.build();
		AppraiseRepository.nativeUpdate(client, appraiseRepository.getTableName(), pk, true, columns);
	}
	//获取点赞数，踩数
	public void getAppraiseCount(SyncClient client,Long ownerId,Long userId,Long value) throws ServerException {
		TSQL ts = new TSQL();
		if(ownerId != null) {
			ts.Term(OP.AND, "value", value).Term(OP.AND, "value", value);
		}
		ts.setGetTotalCount(true);
		SearchQuery query = ts.build();
		JSONObject comment = appraiseRepository.nativeSearch(client, appraiseRepository.getTableName(), "core_appraise_index", query);
		System.out.println(comment);
		
	}
	
	
}
