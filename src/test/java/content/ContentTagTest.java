package content;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.cms.domian.ContentTag;
import zyxhj.cms.domian.ContentTagGroup;
import zyxhj.cms.repository.ContentTagGroupRepository;
import zyxhj.cms.repository.ContentTagRepository;
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

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {

			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
			contentTagService = Singleton.ins(ContentTagService.class);
			contentTagRepository = Singleton.ins(ContentTagRepository.class);
			contentTagGroupRepository = Singleton.ins(ContentTagGroupRepository.class);
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
	private static final String keyword = "首个分组";

	@Test
	public void CreateContentTag() {
		ContentTagGroup cot = new ContentTagGroup();
		cot.id = tagGroupId;
		cot.keyword = keyword;
		cot.type = "首个";
		cot.remark = "这是第一个分组";
		try {
			contentTagGroupRepository.insert(conn, cot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void CreateContentTagGroup() {
		ContentTag co = new ContentTag();
		co.id = tagId;
		co.groupId = tagGroupId;
		co.groupKeyword = keyword;
		co.name = "首个分组下的首个标签";
		co.status = ContentTag.STATUS.DISABLED.v();
		try {
			contentTagRepository.insert(conn, co);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateContentTag() {

		String name = "首个分组下首个标签";
		try {

			contentTagService.createTag(conn, tagGroupId, keyword, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void testEditTagStatus() {
		ContentTag co = new ContentTag();
		Long tagId = 400216004170800L;
		co.status = ContentTag.STATUS.ENABLED.v();
		try {
			contentTagRepository.updateByKey(conn, "id", tagId, co, true);
//			contentTagService.editTagStatus(conn, tagId, status);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetTagsByStatus() {
		Byte status = 1;
		Integer count = 10;
		Integer offset = 0;
		try {
			contentTagService.getTags(conn, status, keyword, count, offset);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateTagGroup() {
		String type = "第二个分组";
		String keyword = "第二个分组";
		String remark = "这是第二个分组";
		try {
			contentTagService.createTagGroup(conn, type, keyword, remark);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void testGetTagGroupTypes() {
		try {
			List<ContentTagGroup> tagGroupTypes = contentTagService.getTagGroupTypes(conn);
			for (ContentTagGroup contentTagGroup : tagGroupTypes) {
				System.out.println(contentTagGroup.keyword);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
