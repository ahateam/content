package content;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.cms.repository.ContentTagGroupRepository;
import zyxhj.cms.service.ChannelService;
import zyxhj.cms.service.ContentService;
import zyxhj.cms.service.ContentTagService;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;

public class ChannelTest {

	private static DruidPooledConnection conn;

	private static ContentTagService contentTagService;
	private static ContentTagGroupRepository contentTagGroupRepository;
	private static ChannelService channelService;
	private static ContentService contentService;

	private static SyncClient client;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			client = DataSource.getTableStoreSyncClient("tsDefault.prop");
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
			channelService = Singleton.ins(ChannelService.class);
			contentTagService = Singleton.ins(ContentTagService.class);
			contentTagGroupRepository = Singleton.ins(ContentTagGroupRepository.class);
			contentService = Singleton.ins(ContentService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}
//
//	private static final Long tagId = IDUtils.getSimpleId();
//	private static final Long tagGroupId = IDUtils.getSimpleId();
//	private static final String keyword = "首个分组";

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

	// 创建专栏
	@Test
	public void CreateChannel() {
		String module = "kkqt";
		String title = "魅力男人的聊天技巧";
		String tags = "[\"聊天技巧\"]";
		String data = "{}";

		try {
			channelService.createChannel(client, module, title, tags, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取专栏
//	@Test
	public void getChannel() {
		String module = "kkqt";

		try {

			System.out.println(channelService.getChannel(client, module, 10, 0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取专栏
//	@Test
	public void getChannelByStatus() {
		String module = "kkqt";
		Byte status = 0;
		Integer count = 10;
		Integer offset = 0;
		try {
			System.out.println(channelService.getChannelByStatus(client, module, status, count, offset));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 根据专栏id查询内容
//	@Test
	public void getConentByChannelId() throws Exception {
		String module = "kkqt";
		Long channelId = 400350832326030L;
		Byte status = 1;
//		JSONObject contentByChannelId = channelService.getContentByChannelId(client, module, channelId, status, 10, 0);
//		System.out.println(contentByChannelId);
	}

}
