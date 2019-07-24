package content;

import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.cms.service.TaskWallService;
import zyxhj.utils.IDUtils;
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
		String wxOpenId = "123456";
		Date time = new Date();
		String pos = "";
		String title = "小视频制作";
		String tags = "[\"搞笑\",\"走心\"]";
		Double money = 112.0;
		String detail = "";
		Byte accessStatus = 0;

		taskWallService.createTaskWallPublished(client, module, type, level, needs, wxOpenId, time, pos, title, tags,
				money, detail, accessStatus);

	}

//	@Test
	public void testGetTaskByType() throws Exception {
		String module = "kkqt";
		Byte type = 0;

		JSONObject taskByType = taskWallService.getTaskByType(client, module, type, 10, 0);
		System.out.println("ByType");
		System.out.println(taskByType);
	}

//	@Test
	public void testGetTaskByTag() throws Exception {
		String module = "kkqt";
		Byte type = 0;
		String tags = "走心";

		JSONObject taskByTag = taskWallService.getTaskByTag(client, module, type, tags, 10, 0);
		System.out.println("ByTag");
		System.out.println(taskByTag);
	}

	// 接取任务
//	@Test
	public void testAcceptanceTask() throws Exception {

		String wxOpenId = "1234";
		Byte type = 2;
		String task_id = "16c2";
		Long taskId = 400372511043498L;
		String upUserId = "123456";
		Byte accessStatus = 1;
		String taskTitle = "小视频制作";
		String module = "kkqt";

		taskWallService.acceptanceTask(client, module, wxOpenId, type, taskTitle, task_id, taskId, upUserId,
				accessStatus);

	}

	// 查询已接任务
//	@Test
	public void testGetTaskList() throws Exception {

		String wxOpenId = "1234";
		String module = "kkqt";

		JSONObject taskListByWxOpenId = taskWallService.getTaskListByWxOpenId(client, module, wxOpenId);
		System.out.println("查询已接任务（所有任务  包括已完成任务）");
		System.out.println(taskListByWxOpenId);
	}

//	@Test
	public void testGetTaskListByType() throws Exception {

		String wxOpenId = "1234";
		String module = "kkqt";
		Byte type = 0;
		JSONObject taskListByWxOpenId = taskWallService.getTaskListByType(client, module, wxOpenId, type);
		System.out.println("根据类型查询已接任务（所有任务  包括已完成任务）");
		System.out.println(taskListByWxOpenId);
	}

//	@Test
	public void testGetTaskListByStatus() throws Exception {

		String wxOpenId = "1234";
		String module = "kkqt";
		Byte status = 0;
		JSONObject taskListByWxOpenId = taskWallService.getTaskListByStatus(client, module, wxOpenId, status);
		System.out.println("根据状态查询已接任务（所有任务  包括已完成任务）");
		System.out.println(taskListByWxOpenId);
	}

//	@Test
	public void testGetTaskListByTypeAndStatus() throws Exception {

		String wxOpenId = "1234";
		String module = "kkqt";
		Byte type = 2;
		Byte status = 0;
		JSONObject taskListByWxOpenId = taskWallService.getTaskListByTypeAndStatus(client, module, wxOpenId, type,
				status);
		System.out.println("根据类型+状态查询已接任务（所有任务  包括已完成任务）");
		System.out.println(taskListByWxOpenId);
	}

}
