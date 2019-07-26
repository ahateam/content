package zyxhj.cms.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;

import zyxhj.kkqt.domain.TaskList;
import zyxhj.kkqt.domain.TaskWall;
import zyxhj.kkqt.repository.TaskListRepository;
import zyxhj.kkqt.repository.TaskWallRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.ts.ColumnBuilder;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSQL;
import zyxhj.utils.data.ts.TSQL.OP;
import zyxhj.utils.data.ts.TSRepository;
import zyxhj.utils.data.ts.TSUtils;

public class TaskWallService {

	private static Logger log = LoggerFactory.getLogger(TaskWallService.class);

	private TaskWallRepository taskWallRepository;
	private TaskListRepository taskListRepository;

	public TaskWallService() {
		try {
			taskWallRepository = Singleton.ins(TaskWallRepository.class);
			taskListRepository = Singleton.ins(TaskListRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 创建任务
	 * 
	 * @throws Exception
	 */
	public TaskWall createTask(SyncClient client, String module, Byte type, Byte level, String needs, Long status,
			Long upUserId, Date time, String pos, String title, String tags, Double money, String detail,
			Byte accessStatus) throws Exception {
		TaskWall tw = new TaskWall();
		Long id = IDUtils.getSimpleId();
		tw._id = TSUtils.get_id(id);
		tw.id = id;
		tw.module = module;
		tw.type = (long) type;
		tw.level = (long) level;
		tw.needs = needs;
		tw.status = status;
		tw.upUserId = upUserId;
		tw.time = time;
		if(pos == null) {
			tw.pos = "";
		}
		tw.title = title;
		tw.tags = tags;
		tw.money = money;
		tw.detail = detail;
		tw.accessStatus = (long) accessStatus; // 默认任务未被接取
		taskWallRepository.insert(client, tw, false);
		return tw;
	}

	// 创建任务 默认状态为已创建
	public TaskWall createTaskWallCreated(SyncClient client, String module, Byte type, Byte level, String needs,
			Long upUserId, Date time, String pos, String title, String tags, Double money, String detail,
			Byte accessStatus) throws Exception {

		return this.createTask(client, module, type, level, needs, (long) TaskWall.STATUS.CREATED.v(), upUserId, time,
				pos, title, tags, money, detail, accessStatus);
	}

	// 创建任务 默认状态为已发布
	public TaskWall createTaskWallPublished(SyncClient client, String module, Byte type, Byte level, String needs,
			Long upUserId, Date time, String pos, String title, String tags, Double money, String detail,
			Byte accessStatus) throws Exception {

		return this.createTask(client, module, type, level, needs, (long) TaskWall.STATUS.PUBLISHED.v(), upUserId, time,
				pos, title, tags, money, detail, accessStatus);
	}

	// 修改任务状态
	public void editTaskWallStatus(SyncClient client, String _id, Long taskWallId, Byte status) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("id", taskWallId).build();
		ColumnBuilder cb = new ColumnBuilder();
		cb.add("status", (long) status);
		List<Column> columns = cb.build();
		TSRepository.nativeUpdate(client, taskWallRepository.getTableName(), pk, false, columns);
	}

//	/**
//	 * 获取任务列表
//	 */
//	// TODO 删除
//	public JSONObject getTask(SyncClient client, String module, Byte status, Integer count, Integer offset)
//			throws Exception {
//
//		TSQL ts = new TSQL();
//		ts.Term(OP.AND, "module", module).Term(OP.AND, "status", (long) status).build();
//		ts.setLimit(count);
//		ts.setOffset(offset);
//		SearchQuery query = ts.build();
//
//		return TSRepository.nativeSearch(client, taskWallRepository.getTableName(), "TaskWallIndex", query);
//	}

	/**
	 * 根据标签获取任务列表
	 * 
	 * @throws Exception
	 */
	// TODO 时间倒序，如果不填tags，则全查
	public JSONObject getTaskByTag(SyncClient client, String module, Byte type, Byte status, String tags, Integer count,
			Integer offset) throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "module", module).Term(OP.AND, "status", (long) status);
		if (type != null) {
			ts.Term(OP.AND, "type", (long) type);
		}
		ts.setLimit(count);
		if (tags != null) {
			ts.Terms(OP.AND, "tags", tags);
		}
		ts.setOffset(offset);
		SearchQuery query = ts.build();
		return TSRepository.nativeSearch(client, taskWallRepository.getTableName(), "TaskWallIndex", query);
	}

//	/**
//	 * 根据类型获取任务列表
//	 * 
//	 * @throws Exception
//	 */
//	// TODO 合并接口
//	public JSONObject getTaskByType(SyncClient client, String module, Byte type, Integer count, Integer offset)
//			throws Exception {
//		TSQL ts = new TSQL();
//		ts.Term(OP.AND, "module", module);
//		ts.setLimit(count);
//		ts.setOffset(offset);
//		SearchQuery query = ts.build();
//		return TSRepository.nativeSearch(client, taskWallRepository.getTableName(), "TaskWallIndex", query);
//	}

	/**
	 * 根据id任务
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public JSONObject getTaskById(SyncClient client, String _id, Long taskWallId) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("id", taskWallId).build();
		return TSRepository.nativeGet(client, taskWallRepository.getTableName(), pk);
	}

//	// 用户获取自己的发布列表
//	public JSONObject getTaskById(SyncClient client, String module, Long upUserId, Integer count, Integer offset)
//			throws Exception {
//		TSQL ts = new TSQL();
//		ts.Term(OP.AND, "module", module).Term(OP.AND, "upUserId", upUserId);
//		ts.setLimit(count);
//		ts.setOffset(offset);
//		SearchQuery query = ts.build();
//		return TSRepository.nativeSearch(client, taskWallRepository.getTableName(), "TaskWallIndex", query);
//	}

	// 用户根据状态获取自己的发布列表
	public JSONObject getTaskByUpUserId(SyncClient client, String module, Long upUserId, Byte type, Byte status,
			Integer count, Integer offset) throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "module", module).Term(OP.AND, "upUserId", upUserId);
		if (type != null) {
			ts.Term(OP.AND, "type", (long) type);
		}
		if (status != null) {
			ts.Term(OP.AND, "status", (long) status);
		}
		ts.setLimit(count);
		ts.setOffset(offset);
		SearchQuery query = ts.build();
		return TSRepository.nativeSearch(client, taskWallRepository.getTableName(), "TaskWallIndex", query);
	}

	/**
	 * 移除任务
	 * 
	 * @throws Exception
	 */
	public void delTaskById(SyncClient client, String _id, Long taskWallId) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("id", taskWallId).build();

		TSRepository.nativeDel(client, taskWallRepository.getTableName(), pk);
	}

	/**
	 * 接取任务
	 */
	// TODO 用userId
	public TaskList acceptanceTask(SyncClient client, String module, Long accUserId, Byte type, String taskTitle,
			String task_id, Long taksId, Long upUserId, Byte accessStatus, String proviteData) throws Exception {
		TaskList tl = new TaskList();
		Long id = IDUtils.getSimpleId();
		tl._id = TSUtils.get_id(id);
		tl.id = id;
		tl.module = module;
		tl.type = (long) type;
		tl.accUserId = accUserId;
		tl.task_id = task_id;
		tl.taskId = taksId;
		tl.upUserId = upUserId;
		tl.status = (long) TaskList.STATUS.NOTCOMPLETED.v();
		tl.createTime = new Date();
		tl.updateTime = new Date();
		tl.taskTitle = taskTitle;
		tl.proviteData = proviteData;
		taskListRepository.insert(client, tl, false);

		if (accessStatus == TaskWall.ACCESSSTATUS.ONE.v()) {
			PrimaryKey pk = new PrimaryKeyBuilder().add("_id", task_id).add("id", taksId).build();
			ColumnBuilder cb = new ColumnBuilder();
			cb.add("status", (long) TaskWall.STATUS.RECEIVE.v());
			List<Column> columns = cb.build();
			TSRepository.nativeUpdate(client, taskWallRepository.getTableName(), pk, true, columns);
		}

		return tl;
	}

	// 修改任务状态
	public void editTaskListStatus(SyncClient client, String _id, Long taskWallId, Byte status) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("id", taskWallId).build();

		ColumnBuilder cb = new ColumnBuilder();
		cb.add("status", status);

		List<Column> columns = cb.build();

		TSRepository.nativeUpdate(client, taskListRepository.getTableName(), pk, true, columns);

	}

//	// 查询已接任务
//	public JSONObject getTaskListByWxOpenId(SyncClient client, String module, String wxOpenId, Integer count,
//			Integer offset) throws Exception {
//		TSQL ts = new TSQL();
//		ts.Term(OP.AND, "module", module).Term(OP.AND, "wxOpenId", wxOpenId).build();
//		ts.setLimit(count);
//		ts.setOffset(offset);
//		SearchQuery query = ts.build();
//
//		return TSRepository.nativeSearch(client, taskListRepository.getTableName(), "TaskListIndex", query);
//	}

//	// 根据类型查询已接任务
//	public JSONObject getTaskListByType(SyncClient client, String module, Long upUserId, Byte type, Integer count,
//			Integer offset) throws Exception {
//
//		TSQL ts = new TSQL();
//		ts.Term(OP.AND, "module", module).Term(OP.AND, "upUserId", upUserId).Term(OP.AND, "type", (long) type).build();
//		ts.setLimit(count);
//		ts.setOffset(offset);
//		SearchQuery query = ts.build();
//
//		return TSRepository.nativeSearch(client, taskListRepository.getTableName(), "TaskListIndex", query);
//	}
//
//	// 根据任务状态查询任务
//	public JSONObject getTaskListByStatus(SyncClient client, String module, Long upUserId, Byte status, Integer count,
//			Integer offset) throws Exception {
//
//		TSQL ts = new TSQL();
//		ts.Term(OP.AND, "module", module).Term(OP.AND, "upUserId", upUserId).Term(OP.AND, "status", (long) status)
//				.build();
//		ts.setLimit(count);
//		ts.setOffset(offset);
//		SearchQuery query = ts.build();
//
//		return TSRepository.nativeSearch(client, taskListRepository.getTableName(), "TaskListIndex", query);
//	}

	// 根据任务类型或状态查询任务
	public JSONObject getTaskListByTypeORStatus(SyncClient client, String module, Long upUserId, Byte type, Byte status,
			Integer count, Integer offset) throws Exception {

		TSQL ts = new TSQL();
		ts.Term(OP.AND, "module", module).Term(OP.AND, "accUserId", upUserId);
		if (type != null) {
			ts.Term(OP.AND, "type", type);
		}
		if (status != null) {
			ts.Term(OP.AND, "status", status);
		}
		ts.setLimit(count);
		ts.setOffset(offset);
		SearchQuery query = ts.build();

		return TSRepository.nativeSearch(client, taskListRepository.getTableName(), "TaskListIndex", query);
	}

}
