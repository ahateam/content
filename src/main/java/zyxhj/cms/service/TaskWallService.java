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
	private TaskWall createTask(SyncClient client, String module, Byte type, Byte level, String needs, Long status,
			String wxOpenId, Date time, String pos, String title, String tags, Double money, String detail,
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
		tw.upUserId = wxOpenId;
		tw.time = time;
		tw.pos = pos;
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
			String wxOpenId, Date time, String pos, String title, String tags, Double money, String detail,
			Byte accessStatus) throws Exception {

		return this.createTask(client, module, type, level, needs, (long) TaskWall.STATUS.CREATED.v(), wxOpenId, time,
				pos, title, tags, money, detail, accessStatus);
	}

	// 创建任务 默认状态为已发布
	public TaskWall createTaskWallPublished(SyncClient client, String module, Byte type, Byte level, String needs,
			String wxOpenId, Date time, String pos, String title, String tags, Double money, String detail,
			Byte accessStatus) throws Exception {

		return this.createTask(client, module, type, level, needs, (long) TaskWall.STATUS.PUBLISHED.v(), wxOpenId, time,
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

	/**
	 * 获取任务列表
	 */
	public JSONObject getTask(SyncClient client, String module, Byte status, Integer count, Integer offset)
			throws Exception {

		TSQL ts = new TSQL();
		ts.Term(OP.AND, "module", module).Term(OP.AND, "status", (long) status).build();
		ts.setLimit(count);
		ts.setOffset(offset);
		SearchQuery query = ts.build();

		return TSRepository.nativeSearch(client, taskWallRepository.getTableName(), "TaskWallIndex", query);
	}

	/**
	 * 根据标签获取任务列表
	 * 
	 * @throws Exception
	 */
	public JSONObject getTaskByTag(SyncClient client, String module, Byte type, String tags, Integer count,
			Integer offset) throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "module", module).Term(OP.AND, "type", (long) type).Terms(OP.AND, "tags", tags);
		ts.setLimit(count);
		ts.setOffset(offset);
		SearchQuery query = ts.build();
		return TSRepository.nativeSearch(client, taskWallRepository.getTableName(), "TaskWallIndex", query);
	}

	/**
	 * 根据类型获取任务列表
	 * 
	 * @throws Exception
	 */
	public JSONObject getTaskByType(SyncClient client, String module, Byte type, Integer count, Integer offset)
			throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "module", module);
		ts.setLimit(count);
		ts.setOffset(offset);
		SearchQuery query = ts.build();
		return TSRepository.nativeSearch(client, taskWallRepository.getTableName(), "TaskWallIndex", query);
	}

	/**
	 * 根据id任务
	 * 
	 * @throws Exception
	 */
	public void getTaskById(SyncClient client, String _id, Long taskWallId) throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "_id", _id).Term(OP.AND, "id", taskWallId).build();
		ts.setLimit(1);
		ts.setOffset(0);
		SearchQuery query = ts.build();

		TSRepository.nativeSearch(client, taskWallRepository.getTableName(), "TaskWallIndex", query);
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
	public TaskList acceptanceTask(SyncClient client, String module, String wxOpenId, Byte type, String taskTitle,
			String task_id, Long taksId, String upUserId, Byte accessStatus) throws Exception {
		TaskList tl = new TaskList();
		Long id = IDUtils.getSimpleId();
		tl._id = TSUtils.get_id(id);
		tl.id = id;
		tl.module = module;
		tl.type = (long) type;
		tl.wxOpenId = wxOpenId;
		tl.task_id = task_id;
		tl.taskId = taksId;
		tl.upUserId = upUserId;
		tl.status = (long) TaskList.STATUS.NOTCOMPLETED.v();
		tl.createTime = new Date();
		tl.updateTime = new Date();
		tl.taskTitle = taskTitle;
		taskListRepository.insert(client, tl, false);

		if (accessStatus == TaskWall.ACCESSSTATUS.ONE.v()) {
			PrimaryKey pk = new PrimaryKeyBuilder().add("_id", task_id).add("id", taksId).build();
			ColumnBuilder cb = new ColumnBuilder();
			cb.add("status", (long) TaskWall.STATUS.CLOSED.v());
			List<Column> columns = cb.build();
			TSRepository.nativeUpdate(client, taskWallRepository.getTableName(), pk, true, columns);
		}

		return tl;
	}

	// 修改任务状态
	public void editTaskListStatus(SyncClient client, String _id, Long taskListId, Byte status) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("id", taskListId).build();

		ColumnBuilder cb = new ColumnBuilder();
		cb.add("status", (long) status);

		List<Column> columns = cb.build();

		TSRepository.nativeUpdate(client, taskListRepository.getTableName(), pk, true, columns);

	}

	// 查询已接任务
	public JSONObject getTaskListByWxOpenId(SyncClient client, String module, String wxOpenId) throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "module", module).Term(OP.AND, "wxOpenId", wxOpenId).build();
		ts.setLimit(1);
		ts.setOffset(0);
		SearchQuery query = ts.build();

		return TSRepository.nativeSearch(client, taskListRepository.getTableName(), "TaskListIndex", query);
	}

	// 根据类型查询已接任务
	public JSONObject getTaskListByType(SyncClient client, String module, String wxOpenId, Byte type) throws Exception {

		TSQL ts = new TSQL();
		ts.Term(OP.AND, "module", module).Term(OP.AND, "wxOpenId", wxOpenId).Term(OP.AND, "type", (long) type).build();
		ts.setLimit(1);
		ts.setOffset(0);
		SearchQuery query = ts.build();

		return TSRepository.nativeSearch(client, taskListRepository.getTableName(), "TaskListIndex", query);
	}

	// 根据任务状态查询任务
	public JSONObject getTaskListByStatus(SyncClient client, String module, String wxOpenId, Byte status)
			throws Exception {

		TSQL ts = new TSQL();
		ts.Term(OP.AND, "module", module).Term(OP.AND, "wxOpenId", wxOpenId).Term(OP.AND, "status", (long) status)
				.build();
		ts.setLimit(1);
		ts.setOffset(0);
		SearchQuery query = ts.build();

		return TSRepository.nativeSearch(client, taskListRepository.getTableName(), "TaskListIndex", query);
	}

	// 根据任务类型+状态查询任务
	public JSONObject getTaskListByTypeAndStatus(SyncClient client, String module, String wxOpenId, Byte type,
			Byte status) throws Exception {

		TSQL ts = new TSQL();
		ts.Term(OP.AND, "module", module).Term(OP.AND, "wxOpenId", wxOpenId).Term(OP.AND, "status", (long) status)
				.Term(OP.AND, "type", (long) type).build();
		ts.setLimit(1);
		ts.setOffset(0);
		SearchQuery query = ts.build();

		return TSRepository.nativeSearch(client, taskListRepository.getTableName(), "TaskListIndex", query);
	}

}
