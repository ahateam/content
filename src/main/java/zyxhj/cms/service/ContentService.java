package zyxhj.cms.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;

import zyxhj.cms.domain.Content;
import zyxhj.cms.repository.ContentExtRepository;
import zyxhj.cms.repository.ContentRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;

public class ContentService {

	private static Logger log = LoggerFactory.getLogger(ContentService.class);

	private ContentRepository contentRepository;
	private ContentExtRepository contentExtRepository;

	public ContentService() {
		try {
			contentRepository = Singleton.ins(ContentRepository.class);
			contentExtRepository = Singleton.ins(ContentExtRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 验证内容是否合法：判断是否下线，等等。TODO 目前待完善
	 */
	public Content auth(DruidPooledConnection conn, Long contentId) throws Exception {
		// 先判断user是否存在
		Content content = contentRepository.getByKey(conn, "id", contentId);
		if (null == content) {
			// content不存在
			throw new ServerException(BaseRC.CMS_CONTENT_NOT_EXISET);
		} else {
			// 再判断content状态是否有效，TODO 目前status没有启用
			return content;
		}
	}

	private Content addContent(DruidPooledConnection conn, Byte type, Byte status, Long upUserId, Long upChannelId,
			String title, String data) throws Exception {

		Content c = new Content();

		c.id = IDUtils.getSimpleId();

		c.type = type;
		if (status == Content.STATUS.DRAFT.v() || status == Content.STATUS.NORMAL.v()) {
			c.status = status;
		} else {
			c.status = Content.STATUS.NORMAL.v();// 首次创建时是正常
		}

		c.createTime = new Date();
		c.updateTime = c.createTime;
		c.upUserId = upUserId;
		c.upChannelId = upChannelId;

		c.title = title;
		if (StringUtils.isBlank(data)) {
			c.data = "{}";// JSON字段不能为空
		} else {
			c.data = data;
		}

		c.tags = "{}";// 设置为JSON数组的空格式，否则后续的编辑操作会没效果（可能是MYSQL的bug）

		contentRepository.insert(conn, c);

		return c;
	}

	/**
	 * 创建内容（默认为草稿）
	 */
	public Content createContentDraft(DruidPooledConnection conn, Byte type, Long upUserId, Long upChannelId,
			String title, String data) throws Exception {
		return addContent(conn, type, Content.STATUS.DRAFT.v(), upUserId, upChannelId, title, data);
	}

	/**
	 * 创建内容（默认为正常状态，已发布）
	 */
	public Content createContentPublished(DruidPooledConnection conn, Byte type, Long upUserId, Long upChannelId,
			String title, String data) throws Exception {
		return addContent(conn, type, Content.STATUS.NORMAL.v(), upUserId, upChannelId, title, data);
	}

	/**
	 * 根据编号删除内容
	 */
	public int delContentById(DruidPooledConnection conn, Long contentId) throws Exception {
		Content renew = new Content();
		renew.status = Content.STATUS.DELETED.v();
		return contentRepository.updateByKey(conn, "id", contentId, renew, true);
	}

	/**
	 * 根据内容编号查询内容
	 */
	public Content getContentById(DruidPooledConnection conn, Long contentId) throws Exception {
		return contentRepository.getByKey(conn, "id", contentId);
	}

	/**
	 * 根据关键字搜索内容
	 */
	public List<Content> searchContentsByKeyword(DruidPooledConnection conn, Byte type, Byte status, Long upUserId,
			Long upChannelId, String keywords, Integer count, Integer offset) throws Exception {
		return contentRepository.searchContentsByKeyword(conn, type, status, upUserId, upChannelId, keywords, count,
				offset);
	}

	/**
	 * 根据标签查询内容
	 */
	public List<Content> queryContentsByTags(DruidPooledConnection conn, Byte type, Byte status, Long upUserId,
			Long upChannelId, String groupKeyword, String[] tags, Integer count, Integer offset) throws Exception {
		return contentRepository.queryContentsByTags(conn, type, status, upUserId, upChannelId, groupKeyword, tags,
				count, offset);
	}

	/**
	 * 读取内容对应的标签
	 * 
	 */
	public JSONArray getContentTagsById(DruidPooledConnection conn, Long contentId, String groupKeyword)
			throws Exception {
		return contentRepository.getContentTags(conn, contentId, groupKeyword);
	}

	/**
	 * 为内容设置标签（覆盖）
	 */
	public int setContentTags(DruidPooledConnection conn, Long contentId, String groupKeyword, JSONArray tags)
			throws Exception {
		return contentRepository.setContentTags(conn, contentId, groupKeyword, tags);
	}

	/**
	 * 为内容添加标签
	 */
	public int addContentTag(DruidPooledConnection conn, Long contentId, String groupKeyword, String tag)
			throws Exception {
		return contentRepository.addContentTag(conn, contentId, groupKeyword, tag);
	}

	/**
	 * 移除内容的标签
	 */
	public int delContentTag(DruidPooledConnection conn, Long contentId, String groupKeyword, String tag)
			throws Exception {
		return contentRepository.delContentTag(conn, contentId, groupKeyword, tag);

	}

}
