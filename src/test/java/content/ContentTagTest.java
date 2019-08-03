package content;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alicloud.openservices.tablestore.SyncClient;

import io.vertx.ext.web.common.template.test;
import zyxhj.cms.domian.ContentTag;
import zyxhj.cms.domian.ContentTagGroup;
import zyxhj.cms.domian.Template;
import zyxhj.cms.repository.ContentTagGroupRepository;
import zyxhj.cms.repository.ContentTagRepository;
import zyxhj.cms.repository.TemplateRepository;
import zyxhj.cms.service.ContentTagService;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;

public class ContentTagTest {

	private static DruidPooledConnection conn;

	private static ContentTagService contentTagService;
	private static ContentTagRepository contentTagRepository;
	private static ContentTagGroupRepository contentTagGroupRepository;
	private static SyncClient client;
	private static TemplateRepository templateRepository;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			client = DataSource.getTableStoreSyncClient("tsDefault.prop");
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
			contentTagService = Singleton.ins(ContentTagService.class);
			contentTagRepository = Singleton.ins(ContentTagRepository.class);
			contentTagGroupRepository = Singleton.ins(ContentTagGroupRepository.class);
			templateRepository = Singleton.ins(TemplateRepository.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	private static final Long tagId = IDUtils.getSimpleId();
	private static final Long tagGroupId = IDUtils.getSimpleId();
//	private static final String keyword = "首个分组";

//	@Test
	public void CreateContentTag() {
		String module = "kkqt";
		Long groupId = 400548867662037L;
		String groupKeyword = "首页";
		String name = "真理名言";

		try {
			contentTagService.createTag(conn, module, groupId, groupKeyword, name);
//			contentTagGroupRepository.insert(conn, cot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void CreateContentTagGroup() {

		String module = "kkqt";
		Byte tagGroupType = 1;
		String type = "VIP";
		String keyword = "VIP";
		String remark = "";

		try {
			contentTagService.createTagGroup(conn, module, tagGroupType, type, keyword, remark);
//			contentTagRepository.insert(conn, co);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void testCreateContentTag() {

		String keyword = "首页";
		Long groupId = 123L;
		String name = "心灵鸡汤";
		String module = "kkqt";
		try {

			contentTagService.createTag(conn, module, groupId, keyword, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//
//	@Test
	public void testEditTagStatus() {
		ContentTag co = new ContentTag();
		Long tagId = 400216004170800L;
		co.status = ContentTag.STATUS.ENABLED.v();
		try {
//			contentTagRepository.updateByKey(conn, "id", tagId, co, false);
//			contentTagService.editTagStatus(client, tagId, co.status);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void testGetTagsByStatus() {
		String module = "kkqt";
		Byte status = 1;
		String keyword = "首页";
		Integer count = 10;
		Integer offset = 0;
		try {
			List<ContentTag> tags = contentTagService.getTags(conn, module, status, keyword, count, offset);
			for (ContentTag contentTag : tags) {
				System.out.println(contentTag.name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
//	public void testGetTags() {
//		String module = "kkqt";
//		Byte status = 1;
//		Integer count = 10;
//		Integer offset = 0;
//		try {
//			contentTagService.getTags(conn, module, status, keyword, count, offset);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	@Test
	public void testCreateTagGroup() {
		String module = "kkqt";
		Byte tagGroupType = 2;
		String type = "附近";
		String keyword = "附近";
		String remark = "";
		try {

			contentTagService.createTagGroup(conn, module, tagGroupType, type, keyword, remark);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void testGetTagGroupTypes() {
		String module = "kkqt";
		Byte tagGroupType = 2;
		try {
			System.out.println(contentTagService.getTagGroupTypes(conn, module, tagGroupType));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void createTemplate() {
		Template te = new Template();
		te.id = IDUtils.getSimpleId();
		te.name = "不屑";
		te.data = "{\"url\":\"https://weapp-xhj.oss-cn-hangzhou.aliyuncs.com/image/2019730/1564463488238.jpg\"}";

		try {
			templateRepository.insert(conn, te);
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}
	
	

}
