package content;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.cms.service.AppraiseService;
import zyxhj.cms.service.ReplyService;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;

public class AppraiseTest {

	private static DruidPooledConnection conn;
	private static AppraiseService appraiseService;
	private static SyncClient client;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			client = DataSource.getTableStoreSyncClient("tsDefault.prop");
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
			appraiseService = Singleton.ins(AppraiseService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	@Test
	public void createAppraise() {
		Long ownerId = 100400L;
		Long userId = 802L;
		Byte value = 0;

		try {
			appraiseService.createAppraise(ownerId, userId, value);
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void delAppraise() {
		Long ownerId = 100400L;
		Long userId = 800L;

		try {
			appraiseService.delAppraise(ownerId, userId);
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void editAppraise() {
		Long ownerId = 100400L;
		Long userId = 802L;
		Byte value = 1;

		try {
			appraiseService.editAppraise(ownerId, userId, value);
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getAppraise() {
		Long ownerId = 100400L;
		Long userId = 802L;
		Byte value = 1;
		try {
			JSONObject json = appraiseService.getAppraiseCount( ownerId, userId, value);
			System.out.println(json.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
