package content;

import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import zyxhj.cms.domain.Content;
import zyxhj.cms.service.ContentService;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;

public class ContentTest {

	private static DruidPooledConnection conn;

	private static ContentService contentService;

	static {
		try {
			contentService = Singleton.ins(ContentService.class);

			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Long CID = 398028682618067L;

	public static void main(String[] args) throws Exception {
		// testCreateContent();

		// testAddTag(CID,"tag6");

		// testGetContentTags(CID);

		// testDelTag(CID,"tag5");

		// JSONArray arr = new JSONArray();
		// arr.add("tag1");
		// arr.add("tag2");
		// testSetTags(CID, arr);

		testGetByTags();
	}

	private static void testCreateContent() {

		try {
			Content cnt = contentService.createContentPublished(conn, Content.TYPE.H5.v(), 1L, 1L, "testh5",
					"{\"data\":\"sdfksdf\"}");

			System.out.println(JSON.toJSONString(cnt));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testSetTags(Long contentId, JSONArray tags) {
		try {

			int ret = contentService.setContentTags(conn, contentId, "kind_type", tags);
			System.out.println(ret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testAddTag(Long conntentId, String tag) {
		try {

			int ret = contentService.addContentTag(conn, conntentId, "kind_type", tag);
			System.out.println(ret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testGetContentTags(Long conntentId) {
		try {

			JSONArray array = contentService.getContentTagsById(conn, conntentId, "kind_type");
			System.out.println(JSON.toJSONString(array));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void testDelTag(Long conntentId, String tag) {
		try {

			int ret = contentService.delContentTag(conn, conntentId, "kind_type", tag);
			System.out.println(ret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void testGetByTags() {
		try {

			List<Content> lc = contentService.queryContentsByTags(conn, null, null, null, null, "kind_type",
					new String[] { "tag3", "tag4" }, 10, 0);
			System.out.println(JSON.toJSONString(lc, true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
