package content;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.cms.service.CommentService;
import zyxhj.cms.service.TaskWallService;
import zyxhj.cms.service.UpvoteService;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;

public class UpvoteTest {

	private static DruidPooledConnection conn;
	private static SyncClient client;

	private static TaskWallService taskWallService;
	private static UpvoteService upvoteService;
	private static CommentService commentService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			client = DataSource.getTableStoreSyncClient("tsDefault.prop");
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
			taskWallService = Singleton.ins(TaskWallService.class);
			upvoteService = Singleton.ins(UpvoteService.class);
			commentService = Singleton.ins(CommentService.class);
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
	public void createUpvote() {

		Long contentId = 123123L;
		Long userId = 123123L;
		Byte type = 1;

		try {
			upvoteService.createUpvote(client, contentId, userId, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void countUpvote() {
		Long contentId = 400598556506972L;
		try {
			System.out.println(upvoteService.countUpvote(client, contentId));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void checkUpvote() {
		Long contentId = 123123L;
		Long userId = 123123L;
		try {
			System.out.println(upvoteService.checkUpvote(client, contentId, userId));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void createComment() {
		String module = "kkqt";
		Long contentId = 123123L;
		Long userId = 123123L;
		Byte type = 1;
		String commentContent = "哈喽哈喽";
		String data = "[]";

		try {
			commentService.createComment(client, module, contentId, userId, commentContent, data);
//			upvoteService.createUpvote(client, contentId, userId, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//
////	@Test
//	public void getComment() {
//		String module = "kkqt";
//		Long contentId = 123123L;
//		Integer count = 10;
//		Integer offset = 0;
//		try {
//			System.out.println(commentService.getCommentByContentId(client, module, contentId, count, offset));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	@Test
	public void countCommentByContentId() {
		String module = "kkqt";
		Long contentId = 123123L;
		Integer count = 10;
		Integer offset = 0;
		try {
			System.out.println(commentService.getCommentByContentId(client,module,contentId,count,offset));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
