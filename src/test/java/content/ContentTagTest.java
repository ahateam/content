//package content;
//
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.alibaba.druid.pool.DruidPooledConnection;
//import com.alicloud.openservices.tablestore.SyncClient;
//
//import zyxhj.cms.domian.ContentTag;
//import zyxhj.cms.domian.ContentTagGroup;
//import zyxhj.cms.repository.ContentTagGroupRepository;
//import zyxhj.cms.repository.ContentTagRepository;
//import zyxhj.cms.service.ContentTagService;
//import zyxhj.utils.IDUtils;
//import zyxhj.utils.Singleton;
//import zyxhj.utils.data.DataSource;
//
//public class ContentTagTest {
//
//	private static DruidPooledConnection conn;
//
//	private static ContentTagService contentTagService;
//	private static ContentTagRepository contentTagRepository;
//	private static ContentTagGroupRepository contentTagGroupRepository;
//	private static SyncClient client;
//
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		try {
//			client = DataSource.getTableStoreSyncClient("tsDefault.prop");
//			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
//			contentTagService = Singleton.ins(ContentTagService.class);
//			contentTagRepository = Singleton.ins(ContentTagRepository.class);
//			contentTagGroupRepository = Singleton.ins(ContentTagGroupRepository.class);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		conn.close();
//	}
//
//	private static final Long tagId = IDUtils.getSimpleId();
//	private static final Long tagGroupId = IDUtils.getSimpleId();
//	private static final String keyword = "首个分组";
//
//	@Test
//	public void CreateContentTag() {
//		String module = "kkqt";
//		String groupKeyword = "附近";
//		String name = "游玩";
//		
//		try {
//			contentTagService.createTag(client, module, groupKeyword, name);
////			contentTagGroupRepository.insert(conn, cot);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
////	@Test
//	public void CreateContentTagGroup() {
//		
//		String module  = "kkqt";
//		Byte tagGroupType = 2;
//		String type = "排行榜";
//		String keyword = "排行榜";
//		String remark = "";
//		
//		try {
//			contentTagService.createTagGroup(client, module, tagGroupType, type, keyword, remark);
////			contentTagRepository.insert(conn, co);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
////	@Test
//	public void testCreateContentTag() {
//
//		String keyword = "视频";
//		String name = "心灵鸡汤";
//		String module = "kkqt";
//		try {
//
//			contentTagService.createTag(client, module, keyword, name);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
////
////	@Test
//	public void testEditTagStatus() {
//		ContentTag co = new ContentTag();
//		Long tagId = 400216004170800L;
//		co.status = (long) ContentTag.STATUS.ENABLED.v();
//		try {
////			contentTagRepository.updateByKey(conn, "id", tagId, co, false);
////			contentTagService.editTagStatus(client, tagId, co.status);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
////	@Test
//	public void testGetTagsByStatus() {
//		String module = "kkqt";
//		Byte status = 1;
//		Integer count = 10;
//		Integer offset = 0;
//		try {
//			contentTagService.getTags(client, module, status, keyword, count, offset);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
////	@Test
//	public void testCreateTagGroup() {
//		String module = "kkqt";
//		Byte tagGroupType = 2;
//		String type = "图文";
//		String keyword = "任务";
//		String remark = "";
//		try {
//
//			contentTagService.createTagGroup(client, module, tagGroupType, type, keyword, remark);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
////	@Test
//	public void testGetTagGroupTypes() {
//		String module = "kkqt";
//		Byte tagGroupType = 1;
//		try {
//			contentTagService.getTagGroupTypes(client, module, tagGroupType);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//}
