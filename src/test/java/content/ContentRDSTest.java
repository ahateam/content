package content;

import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.cms.domian.Content;
import zyxhj.cms.service.ContentServiceRDS;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;

public class ContentRDSTest {
	private static DruidPooledConnection conn;
	private static ContentServiceRDS conser;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
			conser = Singleton.ins(ContentServiceRDS.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	@Test
	public void addContent() {
		Content c = new Content();
		String moduleId = "1221";
		Byte type = 1;
		Byte status = 1;
		Byte power = 2;
		String title = "title";
		Long upUserId = 45L;
		Long upChannelId = 54L;

		JSONObject jo = new JSONObject();

		JSONArray a1 = new JSONArray();
		a1.add("tag1");
		a1.add("tag2");

		JSONArray a2 = new JSONArray();
		a2.add("tagaa");
		a2.add("tagbb");

		jo.put("图片", a1);
		jo.put("视频", a2);

		System.out.println(JSON.toJSONString(jo));

		JSONObject tags = jo;
		String proviteData = "";
		String ext = "";
		String data = "data";
		try {
			conser.addContent(conn, moduleId, type, status, power, upUserId, upChannelId, title, tags, data,
					proviteData, ext);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void searchContent() {
		String moduleId = "1221";
		Byte type = null;
		Byte status = null;
		Byte power = null;
		Long upUserId = null;
		Long upChannelId =null;
		//
		JSONObject jo = new JSONObject();
		
		JSONArray a1 = new JSONArray();
		a1.add("tag0");
		a1.add("tag3");

		JSONArray a2 = new JSONArray();
		a2.add("tagaa");
		a2.add("tagbb");

		jo.put("图片",a1);
		jo.put("group2", a2);
		
		 //10.取得JSONObject对象中key的集合
//        Set<String> keySet= jo.keySet();
//        for (String key : keySet) {
//            //System.out.println("   "+key);
//        }
        JSONArray name1 = jo.getJSONArray("图片");
        System.out.println(name1.get(0)+"*"+name1.get(1));
		//
		String tags = jo.toJSONString();
		System.out.println(tags);
		try {
			List<Content> list = conser.getContents(conn, moduleId, type, status, power,upUserId,upChannelId, tags,10, 0);
			System.out.println(list.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void getcontent() {
		try {
			Content c = conser.getConntent(conn, 401280142071039L);
			System.out.println(c.id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void editcontent() {
		Content c = new Content();
		Long id = 401280137781325L;
		Byte type = 3;
		Byte status = 3;
		Byte power = 8;
		String title = "title_a";
		Long upUserId = 80L;
		Long upChannelId = 80L;
		String tags = "{name:Tim,age:25,sex:male}";
		String proviteData = "";
		String ext = "78";
		String data = "data_a";
		try {
			conser.editContent(conn, id, null, type, status, power, upUserId, upChannelId, title, data, proviteData,
					ext);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void delcontent() {
		try {
			conser.delContentById(conn, 401280027423269L);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void setConntentTag() {
JSONObject jo = new JSONObject();
		
		JSONArray a1 = new JSONArray();
		a1.add("tag9");
		a1.add("tag3");

		JSONArray a2 = new JSONArray();
		a2.add("tagaa");
		a2.add("tagbb");

		jo.put("图片1",a2);
		String tags = jo.toJSONString();
		try {
			conser.setConntentTag(conn, 401303127864401L, tags, true);
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
