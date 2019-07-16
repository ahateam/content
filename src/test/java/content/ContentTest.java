package content;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.cms.service.ContentService;
import zyxhj.flow.domain.TableSchema;
import zyxhj.flow.service.FlowService;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;

public class ContentTest {

	private static DruidPooledConnection conn;

	private static FlowService flowService;
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

			flowService = Singleton.ins(FlowService.class);
			contentService = Singleton.ins(ContentService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	private static final Long upUserId = 400159699711499L;

	private static final Long upChannelId = 400159711950692L;

	private static final String module = "400159724862966";

	private static final String id = "16bf";

	private static final Long contentId = 400197074746052L;

//	@Test
	public void testCreateContentDraft() {
		Byte type = 1;
		String title = "一棵树";
		String data = "{url:xxxxx}";
		String text = "第一次创建内容  默认为草稿";

		try {
			contentService.createContentDraft(client, text, type, upUserId, upChannelId, title, data, text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void testCreateContentPublished() {
		Byte type = 1;
		String title = "一棵树";
		String data = "{url:xxxxx}";
		String text = "第一次创建内容  默认为正常状态  已发布";

		try {
			contentService.createContentPublished(client, module, type, upUserId, upChannelId, title, data, text);
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

		Integer count = 10;
		Integer offset = 0;
		try {
			System.out.println(contentService.getContents(client, count, offset));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void queryContentsByTags() {

		Byte type = 1;
		Byte status = 0;
		String groupKeyword = "第一";
		Integer count = 10;
		Integer offset = 0;

		try {
			System.out.println(contentService.queryContentsByTags(client, type, status, upUserId, upChannelId,
					groupKeyword, count, offset));
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
			System.out.println(contentService.searchContentsByKeyword(client, type, status, upUserId, upChannelId, keywords, count,
					offset));
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
	
	
	
	

}
