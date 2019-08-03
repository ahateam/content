package content;

import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.cms.service.TaskWallService;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;

public class TaskTest {

	private static DruidPooledConnection conn;
	private static SyncClient client;

	private static TaskWallService taskWallService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			client = DataSource.getTableStoreSyncClient("tsDefault.prop");
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
			taskWallService = Singleton.ins(TaskWallService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

//	@Test
//	public void CreateContentTag() {
//		ContentTagGroup cot = new ContentTagGroup();
//		cot.id = tagGroupId;
//		cot.keyword = keyword;
//		cot.type = "首个";
//		cot.remark = "这是第一个分组";
//		try {
////			contentTagService.createTag(client, groupId, groupKeyword, name);
////			contentTagGroupRepository.insert(conn, cot);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	@Test
	public void testAddTask() throws Exception {
		String module = "kkqt";
		Byte type = 0;
		Byte level = 2;
		String needs = "";
		Long upUserId = 123456L;
		Date time = new Date();
		String pos = "";
		String title = "小视频制作";
		String tags = "[\"搞笑\",\"走心\"]";
		Double money = 112.0;
		String detail = "";
		Byte accessStatus = 0;

		taskWallService.createTaskWallPublished(client, module, type, level, needs, upUserId, time, pos, title, tags,
				money, detail, accessStatus);

	}

////	@Test
//	public void testGetTaskByType() throws Exception {
//		String module = "kkqt";
//		Byte type = 0;
//
//		JSONObject taskByType = taskWallService.getTaskByType(client, module, type, 10, 0);
//		System.out.println("ByType");
//		System.out.println(taskByType);
//	}

//	@Test
	public void testGetTaskByTag() throws Exception {
		String module = "kkqt";
		Byte type = 0;
		String tags = "走心";
		Byte status = 0;

//		JSONObject taskByTag = taskWallService.getTaskByTag(client, module, type, status, tags, 10, 0);
//		System.out.println("ByTag");
//		System.out.println(taskByTag);
	}

	// 接取任务
//	@Test
	public void testAcceptanceTask() throws Exception {

		Long accUserId = 1234L;
		Byte type = 2;
		String task_id = "16c2";
		Long taskId = 400372511043498L;
		Long upUserId = 123456L;
		Byte accessStatus = 1;
		String taskTitle = "小视频制作";
		String module = "kkqt";
		String proviteData = "{}";

		taskWallService.acceptanceTask(client, module, accUserId, type, taskTitle, task_id, taskId, upUserId,
				accessStatus, proviteData);

	}

//	// 查询已接任务
////	@Test
//	public void testGetTaskList() throws Exception {
//
//		String wxOpenId = "1234";
//		String module = "kkqt";
//
//		JSONObject taskListByWxOpenId = taskWallService.getTaskListByWxOpenId(client, module, wxOpenId, 10, 0);
//		System.out.println("查询已接任务（所有任务  包括已完成任务）");
//		System.out.println(taskListByWxOpenId);
//	}
//
////	@Test
//	public void testGetTaskListByType() throws Exception {
//
//		Long upUserId = 123456L;
//		String module = "kkqt";
//		Byte type = 0;
//		JSONObject taskListByWxOpenId = taskWallService.getTaskListByType(client, module, upUserId, type, 10, 0);
//		System.out.println("根据类型查询已接任务（所有任务  包括已完成任务）");
//		System.out.println(taskListByWxOpenId);
//	}

////	@Test
//	public void testGetTaskListByStatus() throws Exception {
//
//		Long upUserId = 123456L;
//		String module = "kkqt";
//		Byte status = 0;
//		JSONObject taskListByWxOpenId = taskWallService.getTaskListByStatus(client, module, upUserId, status, 10, 0);
//		System.out.println("根据状态查询已接任务（所有任务  包括已完成任务）");
//		System.out.println(taskListByWxOpenId);
//	}

//	@Test
	public void testGetTaskListByTypeAndStatus() throws Exception {

		Long upUserId = 123456L;
		String module = "kkqt";
		Byte type = 2;
		Byte status = null;
		JSONArray taskListByWxOpenId = taskWallService.getTaskListByTypeORStatus(conn, client, module, upUserId, type,
				status, 10, 0);
		System.out.println("根据类型+状态查询已接任务（所有任务  包括已完成任务）");
		System.out.println(taskListByWxOpenId);
	}

	@Test
	public void testGetTask() throws Exception {
		String module = "kkqt";
		Byte type = 0;
		Byte status = 1;
		String tags = null;
//		JSONObject taskByTag = taskWallService.getTaskByTag(client, module, type, status, tags, 10, 0);
//		System.out.println(taskByTag);
	}

}
