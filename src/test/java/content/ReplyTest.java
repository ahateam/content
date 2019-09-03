package content;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.cms.service.ReplyService;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;



public class ReplyTest {

	private static DruidPooledConnection conn;
	private static ReplyService replyService;
	private static SyncClient client;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			client = DataSource.getTableStoreSyncClient("tsDefault.prop");
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
			replyService = Singleton.ins(ReplyService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}
	
	@Test
	public void createReply() {
		Long ownerId = 123584L;
		Long upUserId = 900L;
		Long atUserId = 200L;
		String title = "回复标题(a)";
		String text = "回复内容(a)";
		String ext = "";
		
		try {
			replyService.createReply(client, ownerId, upUserId, atUserId, title, text, ext);
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void editReply() {
		Long ownerId = 123588L;
		Long upUserId = 800L;
		Long atUserId = 200L;
		String title = "回复标题(b)";
		String text = "回复内容(b)";
		String ext = "测试内容b";
		
		try {
			replyService.editReply(client, ownerId, 1567475489126000L, 0L, title, text, ext);
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void delReply() {
		Long ownerId = 123588L;
		Long upUserId = 800L;
		Long atUserId = 200L;
		String title = "回复标题(b)";
		String text = "回复内容(b)";
		String ext = "测试内容b";
		
		try {
			replyService.delReply(client,123588L, 1567475489126000L);
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getReply() {
		Long ownerId = 123588L;
		Long upUserId = 800L;
		Long atUserId = 200L;
		String title = "回复标题(b)";
		String text = "回复内容(b)";
		String ext = "测试内容b";
		
		try {
			JSONObject json = replyService.getReplyList(client, 123584L, 1L, 10, 1);
			System.out.println(json.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
