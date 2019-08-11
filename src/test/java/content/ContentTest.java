package content;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.cms.domian.Template;
import zyxhj.cms.service.ContentService;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;

public class ContentTest {

	private static DruidPooledConnection conn;

	private static ContentService contentService;
	private static SyncClient client;

	public static List<String> getJSArgs(String src) {
		int ind = 0;
		int start = 0;
		int end = 0;
		ArrayList<String> ret = new ArrayList<>();
		while (true) {
			start = src.indexOf("{{", ind);
			if (start < ind) {
				// 没有找到新的{，结束
				break;
			} else {

				// 找到{，开始找配对的}
				end = src.indexOf("}}", start);
				if (end > start + 3) {
					// 找到结束符号
					ind = end + 2;// 记录下次位置

					ret.add(src.substring(start + 2, end));
				} else {
					// 没有找到匹配的结束符号，终止循环
					break;
				}
			}
		}
		return ret;
	}

//	public static void main(String[] args) {
//
//		ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");
//		try {
//
//			SimpleBindings simpleBindings = new SimpleBindings();
//
//			// String js = "{{COL1}} + {{COL2}} + {{COL3}} + {{COL4}} + {{COL5}}";
//
//			String js = "if ({{COL1}} < 18) { '未成年'} else { '成年'}";
//
//			System.out.println("oldjs>>>" + js);
//
//			List<String> temps = getJSArgs(js);
//
//			int xxx = 10;
//			for (String temp : temps) {
//				// temp = temp.substring(1, temp.length() - 1);
//				System.out.println(temp);
//				simpleBindings.put(temp, xxx);
//
//				xxx += 10;
//			}
//			System.out.println();
//
//			////
//
//			js = StringUtils.replaceEach(js, new String[] { "{{", "}}" }, new String[] { "(", ")" });
//
//			System.out.println("newjs>>>" + js);
//
//			Object ret = nashorn.eval(js, simpleBindings);
//			System.out.println(JSON.toJSONString(ret));
//		} catch (ScriptException e) {
//			e.printStackTrace();
//		}

//	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
			client = DataSource.getTableStoreSyncClient("tsDefault.prop");

			contentService = Singleton.ins(ContentService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	private static final Long upUserId = 1234567890L;

	private static final Long upChannelId = 400353274527014L;

	private static final String module = "kkqt";

	private static final String id = "16c0";

	private static final Long contentId = 400197074746052L;

//	@Test
	public void testCreateContentDraft() {
		Byte type = 1;
		String title = "一棵树";
		String data = "{}";
		String text = "第一次创建内容  默认为草稿";

		try {
//			contentService.createContentDraft(client, text, type, upUserId, upChannelId, title, data, text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void testCreateContentPublished() {
		Byte type = 3;
		String title = "第四堂课";
		String data = "{\"show\":0,\"text\":\"猜猜猜猜猜擦此案猜猜猜才\",\"url\":\"http://127.0.0.1:20002/static/temVideos/c9c277d2ad5785c6764290d0c7d8df6c.mp4\"}";
		String tags = "[\"夸夸\"]";

		try {
//			contentService.createContentPublished(client, module, type, upUserId, upChannelId, title,tags, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void delContentById() {

		try {
			contentService.delContentById(client, id, contentId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//
//	@Test
	public void getContentById() {

		try {
			JSONObject contentById = contentService.getContentById(client, id, contentId);
			System.out.println(contentById);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void getContents() {
		String module = "kkqt";
		Integer count = 10;
		Integer offset = 0;
		try {
			System.out.println(contentService.getContents(conn, client, module, count, offset));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void queryContentsByTags() {

		Byte type = 3;
		Byte status = 1;
		String groupKeyword = "心灵鸡汤";
		String module = "234";
		Integer count = 10;
		Integer offset = 0;

		try {
//			System.out.println(contentService.queryContentsByTags(client,module, type, status,groupKeyword, count, offset));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void searchContentsByKeyword() {

		Byte type = 1;
		Byte status = 0;
		String keywords = "第一";
		Integer count = 10;
		Integer offset = 0;

		try {
			System.out.println(contentService.searchContentsByKeyword(client, type, status, upUserId, upChannelId,
					keywords, count, offset));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void setContentTags() {

		String groupKeyword = "{第一}";

		try {
			contentService.setContentTags(client, id, contentId, groupKeyword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getTemplate() throws Exception {
//		List<Template> template = contentService.getTemplate(conn);
//		for (Template template2 : template) {
//			System.out.println(template2.name);
//		}
	}

}
