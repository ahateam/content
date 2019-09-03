package content;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.core.service.MailService;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;

public class MailTest {
	private static DruidPooledConnection conn;
	private static MailService mailService;
	private static SyncClient client;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			client = DataSource.getTableStoreSyncClient("tsDefault.prop");
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
			mailService = Singleton.ins(MailService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}
	
	@Test
	public void createMailTag() {
		Long modele = 100L;
		String name = "测试标签(a)";
		
		try {
			mailService.createMailTag(modele, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
