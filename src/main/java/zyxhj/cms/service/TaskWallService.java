package zyxhj.cms.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;

import zyxhj.core.domain.User;
import zyxhj.core.service.UserService;
import zyxhj.kkqt.domain.TaskList;
import zyxhj.kkqt.domain.TaskWall;
import zyxhj.kkqt.repository.TaskListRepository;
import zyxhj.kkqt.repository.TaskWallRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.EXP;
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
	private UserService userService;

	public TaskWallService() {
		try {
			taskWallRepository = Singleton.ins(TaskWallRepository.class);
			taskListRepository = Singleton.ins(TaskListRepository.class);
			userService = Singleton.ins(UserService.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 创建任务
	 * 
	 * @throws Exception
	 */
	public TaskWall createTask(SyncClient client, String module, Byte type, Byte level, Byte status, Long upUserId,
			String pos, String title, String tags, Double money, String detail, Byte accessStatus) throws Exception {
		TaskWall tw = new TaskWall();
		Long id = IDUtils.getSimpleId();
		tw._id = TSUtils.get_id(id);
		tw.id = id;
		tw.module = module;
		tw.type = (long) type;
		tw.level = (long) level;
		if (status == TaskWall.STATUS.CREATED.v()) {
			tw.status = (long) TaskWall.STATUS.CREATED.v();
		} else if (status == TaskWall.STATUS.PUBLISHED.v()) {
			tw.status = (long) TaskWall.STATUS.PUBLISHED.v();
		}
		tw.upUserId = upUserId;
		if (pos == null) {
			tw.pos = "";
		} else {
			tw.pos = pos;
		}
		tw.createTime = new Date();
		tw.title = title;
		tw.tags = tags;
		tw.money = money;
		tw.taskStatus = (long) TaskWall.TASKSTATUS.PUBLISHED.v();
		tw.detail = detail;
		tw.accessStatus = (long) accessStatus; // 默认任务未被接取
		taskWallRepository.insert(client, tw, false);
		return tw;
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
	 * 根据标签获取任务列表
	 * 
	 * @throws Exception
	 */
	public JSONArray getTask(DruidPooledConnection conn, SyncClient client, String module, Byte type, Byte status,
			String tags, Integer count, Integer offset) throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "module", module);
		if (status != null) {
			ts.Term(OP.AND, "status", (long) status);
		}
		if (type != null) {
			ts.Term(OP.AND, "type", (long) type);
		}
		ts.setLimit(count);
		if (tags != null) {
			ts.Terms(OP.AND, "tags", tags);
		}
		ts.addSort(new FieldSort("createTime", SortOrder.DESC));
		ts.setLimit(count);
		ts.setOffset(offset);
		SearchQuery query = ts.build();
		JSONObject task = TSRepository.nativeSearch(client, taskWallRepository.getTableName(), "TaskWallIndex", query);
		JSONArray json = task.getJSONArray("list");
		for (int i = 0; i < json.size(); i++) {
			JSONObject j = json.getJSONObject(i);
			User user = userService.getUserById(conn, j.getLong("upUserId"));
			json.getJSONObject(i).put("user", user);
		}
		return json;
	}

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
	public TaskList acceptanceTask(DruidPooledConnection conn, SyncClient client, String module, Long accUserId,
			Byte type, String taskTitle, String task_id, Long taskId, Long upUserId, Byte accessStatus,
			String proviteData) throws Exception {
		TaskList taskList = taskListRepository.get(conn,
				EXP.INS().key("task_id", taskId).andKey("acc_user_id", accUserId));

		if (taskList == null) {
			TaskList tl = new TaskList();
			tl.id = IDUtils.getSimpleId();
			tl.module = module;
			tl.type = type;
			tl.accUserId = accUserId;
			tl.task_id = task_id;
			tl.taskId = taskId;
			tl.upUserId = upUserId;
			tl.status = TaskList.STATUS.EXAMINE.v();
			tl.createTime = new Date();
			tl.time = new Date();
			tl.updateTime = new Date();
			tl.taskTitle = taskTitle;
			tl.proviteData = proviteData;
			taskListRepository.insert(conn, tl);
			return tl;
		} else {
			throw new ServerException(BaseRC.ACC_TASK_LIST);
		}
	}

	// 修改任务状态
	public void editTaskListStatus(DruidPooledConnection conn, SyncClient client, String _id, Long userId,
			Long taskListId, Long taskId, Byte status) throws Exception {
		System.out.println("status=" + status);
		TaskList ta = new TaskList();
		ta.status = status;
		taskListRepository.update(conn, EXP.INS().key("id", taskListId).andKey("acc_user_id", userId), ta, true);
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("id", taskId).build();
		ColumnBuilder cb = new ColumnBuilder();
		if (status == TaskList.STATUS.EXAMINESUCCESS.v()) {
			ta.status = TaskList.STATUS.FAIL.v();
			ta.updateTime = new Date();
			taskListRepository.update(conn,
					EXP.INS().key("task_id", taskId).andKey("status", TaskList.STATUS.EXAMINE.v()), ta, true);
			cb.add("status", (long) TaskWall.STATUS.RECEIVE.v());
			cb.add("taskStatus", (long) TaskWall.TASKSTATUS.EXAMINEUSERSUCCESS.v());
			List<Column> columns = cb.build();
			TSRepository.nativeUpdate(client, taskWallRepository.getTableName(), pk, true, columns);
		} else if (status == TaskList.STATUS.SUCCESSEXAMINE.v()) {
			cb.add("taskStatus", (long) TaskWall.TASKSTATUS.EXAMINETASK.v());
			List<Column> columns = cb.build();
			TSRepository.nativeUpdate(client, taskWallRepository.getTableName(), pk, true, columns);
		} else if (status == TaskList.STATUS.REDO.v()) {
			cb.add("taskStatus", (long) TaskWall.TASKSTATUS.EXAMINEUSERSUCCESS.v());
			List<Column> columns = cb.build();
			TSRepository.nativeUpdate(client, taskWallRepository.getTableName(), pk, true, columns);
		} else if (status == TaskList.STATUS.SUCCESS.v()) {
			cb.add("taskStatus", (long) TaskWall.TASKSTATUS.SUCCESS.v());
			List<Column> columns = cb.build();
			TSRepository.nativeUpdate(client, taskWallRepository.getTableName(), pk, true, columns);
		}

	}
	

	// 根据任务类型或状态查询任务
	public JSONArray getTaskListByTypeORStatus(DruidPooledConnection conn, SyncClient client, String module,
			Long upUserId, Byte type, Byte status, Integer count, Integer offset) throws Exception {

		List<TaskList> taskList = taskListRepository.getList(conn, EXP.INS().andKey("module", module)
				.andKey("acc_user_id", upUserId).andKey("status", status).andKey("type", type), count, offset);

		String task = JSON.toJSON(taskList).toString();
		JSONArray json = JSONArray.parseArray(task);
		for (int i = 0; i < json.size(); i++) {
			JSONObject j = json.getJSONObject(i);
			User user = userService.getUserById(conn, j.getLong("upUserId"));
			json.getJSONObject(i).put("user", user);
		}
		return json;
	}

	// 获取当前任务接取人列表
	public JSONArray getTaskListByTaskId(DruidPooledConnection conn, Long taskId) throws Exception {
		List<TaskList> taskList = taskListRepository.getList(conn, EXP.INS().key("task_id", taskId), 512, 0);
		String task = JSON.toJSON(taskList).toString();
		JSONArray json = JSONArray.parseArray(task);
		for (int i = 0; i < json.size(); i++) {
			JSONObject j = json.getJSONObject(i);
			User user = userService.getUserById(conn, j.getLong("accUserId"));
			json.getJSONObject(i).put("user", user);
			Integer count = taskListRepository.countTaskListByAccUserId(conn, j.getLong("accUserId"));
			json.getJSONObject(i).put("worksNum", count);
		}
		return json;
	}

	public List<TaskList> getTaskListByAccUserId(DruidPooledConnection conn, String module, Long userId, Integer count,
			Integer offset) throws Exception {
		return taskListRepository.getList(conn, EXP.INS().key("module", module).andKey("acc_user_id", userId)
				.andKey("status", TaskList.STATUS.SUCCESS.v()), count, offset);
	}

	// 设置完成时间
	public void setTaskListTime(DruidPooledConnection conn, Long taskListId, Date time) throws Exception {
		TaskList tl = new TaskList();
		tl.time = time;
		tl.updateTime = new Date();
		taskListRepository.update(conn, EXP.INS().key("id", taskListId), tl, true);
	}

	public void setTaskListData(DruidPooledConnection conn, Long taskListId, String data) throws Exception {
		TaskList tl = new TaskList();
		tl.taskData = data;
		tl.updateTime = new Date();
		taskListRepository.update(conn, EXP.INS().key("id", taskListId), tl, true);
	}

	public JSONArray getTaskByGeo(DruidPooledConnection conn, SyncClient client, String module, String point, int meter,
			Byte type, Integer count, Integer offset) throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "module", module).GeoDistance(OP.AND, "pos", point, meter).Term(OP.AND, "status",
				(long) TaskWall.STATUS.CREATED.v());
		if (type != null) {
			ts.Term(OP.AND, "type", (long) type);
		}
		ts.setLimit(count);
		ts.setOffset(offset);
		SearchQuery query = ts.build();
		JSONObject tw = TSRepository.nativeSearch(client, taskWallRepository.getTableName(), "TaskWallIndex", query);
		JSONArray json = tw.getJSONArray("list");
		for (int i = 0; i < json.size(); i++) {
			JSONObject j = json.getJSONObject(i);
			User user = userService.getUserById(conn, j.getLong("upUserId"));
			json.getJSONObject(i).put("user", user);
		}
		return json;
	}

}
