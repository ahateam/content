package zyxhj.cms.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.math3.analysis.function.Exp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import zyxhj.cms.domian.Content;
import zyxhj.cms.repository.ContentRepository;
import zyxhj.core.domain.Reply;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.EXP;

public class ContentServiceRDS {

	private static Logger log = LoggerFactory.getLogger(ContentServiceRDS.class);
	private ContentRepository contentRepository;

	public ContentServiceRDS() {
		try {
			contentRepository = Singleton.ins(ContentRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 添加内容
	 */
	public Content addContent(DruidPooledConnection conn, String module, Byte type, Byte status, Byte power,
			Long upUserId, Long upChannelId, String title, JSONObject tags, String data, String proviteData, String ext)
			throws Exception {
		Content c = new Content();
		c.moduleId = module;
		c.id = IDUtils.getSimpleId();
		c.createTime = new Date();
		c.updateTime = c.createTime;
		c.type = type;
		c.status = status;
		c.power = power;
		c.title = title;
		c.upUserId = upUserId;
		c.upChannelId = upChannelId;
		c.tags = tags;
		c.data = data;
		c.proviteData = proviteData;
		c.ext = ext;
		contentRepository.insert(conn, c);

		return c;
	}

	/**
	 * 修改内容
	 */
	public Content editContent(DruidPooledConnection conn, Long id, String module, Byte type, Byte status, Byte power,
			Long upUserId, Long upChannelId, String title, String data, String proviteData, String ext)
			throws Exception {
		Content c = new Content();
		c.moduleId = module;
		c.updateTime = new Date();
		c.type = type;
		c.status = status;
		c.power = power;
		c.title = title;
		c.upUserId = upUserId;
		c.upChannelId = upChannelId;
		c.data = data;
		c.proviteData = proviteData;
		c.ext = ext;
		contentRepository.update(conn, EXP.INS().key("id", id), c, true);
		return c;
	}

	/**
	 * 根据编号删除内容(逻辑删除)
	 */
	public void delContentById(DruidPooledConnection conn, Long id) throws Exception {
		Content c = new Content();
		c.id = id;
		c.updateTime = new Date();
		c.status = c.STATUS_DELETED;
		contentRepository.update(conn, EXP.INS().key("id", id), c, true);
	}

	/**
	 * 根据条件查询内容
	 * 
	 * @throws ServerException
	 */
	public List<Content> searchContents(DruidPooledConnection conn, Byte type, Byte status, int count, int offset)
			throws ServerException {
		if (type == null && status == null) {
			return contentRepository.getList(conn, null, count, offset);
		} else if (type != null && status != null) {
			return contentRepository.getList(conn, EXP.INS().key("type", type).andKey("status", status), count, offset);
		} else if (type != null) {
			return contentRepository.getList(conn, EXP.INS().key("type", type), count, offset);
		} else if (status != null) {
			return contentRepository.getList(conn, EXP.INS().key("type", type), count, offset);
		}
		return null;
	}

	/**
	 * 根据id查询返回一个内容对象
	 */
	public Content getConntent(DruidPooledConnection conn, Long id) throws ServerException {
		return contentRepository.get(conn, EXP.INS().key("id", id));
	}
}
