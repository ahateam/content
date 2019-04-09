package zyxhj.cms.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;

import zyxhj.cms.domain.Content;
import zyxhj.cms.domain.ContentTag;
import zyxhj.cms.domain.ContentTagGroup;
import zyxhj.cms.service.ContentService;
import zyxhj.cms.service.ContentTagService;
import zyxhj.core.domain.User;
import zyxhj.utils.CodecUtils;
import zyxhj.utils.ServiceUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.DataSourceUtils;

public class ContentController extends Controller {

	private static Logger log = LoggerFactory.getLogger(ContentController.class);

	private DataSource dsRds;
	private ContentService contentService;
	private ContentTagService contentTagService;

	public ContentController(String node) {
		super(node);
		try {
			dsRds = DataSourceUtils.getDataSource("rdsDefault");

			contentService = Singleton.ins(ContentService.class);
			contentTagService = Singleton.ins(ContentTagService.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@ENUM(des = "内容类型")
	public Content.TYPE[] contentTypes = Content.TYPE.values();

	@ENUM(des = "内容状态")
	public Content.STATUS[] contentStatus = Content.STATUS.values();

	/**
	 * 
	 */
	@POSTAPI(path = "createContentDraft", //
			des = "创建内容，保存为草稿", //
			ret = "所创建的对象"//
	)
	public APIResponse createContentDraft(//
			@P(t = "用户编号") Long userId, //
			@P(t = "内容类型Content.TYPE") Byte type, //
			@P(t = "上传专栏编号", r = false) Long upChannelId, //
			@P(t = "标题") String title, //
			@P(t = "数据（JSON）") String data//
	) throws Exception {
		Long upUserId = userId;
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			Content cnt = contentService.createContentDraft(conn, type, upUserId, upChannelId, title, data);
			return APIResponse.getNewSuccessResp(cnt);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(path = "createContentPublished", //
			des = "创建内容，保存为正常（已发布）", //
			ret = "所创建的对象"//
	)
	public APIResponse createContentPublished(//
			@P(t = "用户编号") Long userId, //
			@P(t = "内容类型Content.TYPE") Byte type, //
			@P(t = "上传专栏编号", r = false) Long upChannelId, //
			@P(t = "标题") String title, //
			@P(t = "数据（JSON）") String data//
	) throws Exception {
		Long upUserId = userId;
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			Content cnt = contentService.createContentPublished(conn, type, upUserId, upChannelId, title, data);
			return APIResponse.getNewSuccessResp(cnt);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(path = "delContentById", //
			des = "删除内容（标记状态）", //
			ret = "影响的记录行数"//
	)
	public APIResponse delContentById(//
			@P(t = "用户编号") Long userId, //
			@P(t = "内容编号") Long contentId //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			Content content = contentService.auth(conn, contentId);// content鉴权

			return APIResponse.getNewSuccessResp(contentService.delContentById(conn, contentId));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(path = "addContentTags", //
			des = "为内容添加标签" //
	)
	public APIResponse addContentTags(//
			@P(t = "用户编号") Long userId, //
			@P(t = "内容编号") Long contentId, //
			@P(t = "标签分组关键字") String groupKeyword, //
			@P(t = "标签") String tag//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			Content content = contentService.auth(conn, contentId);// content鉴权

			contentService.addContentTag(conn, contentId, groupKeyword, tag);

			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(path = "removeContentTags", //
			des = "为内容移除标签", //
			ret = "影响的记录行数"//
	)
	public APIResponse delContentTag(//
			@P(t = "用户编号") Long userId, //
			@P(t = "内容编号") Long contentId, //
			@P(t = "标签分组关键字") String groupKeyword, //
			@P(t = "标签") String tag//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			Content content = contentService.auth(conn, contentId);// content鉴权

			return APIResponse.getNewSuccessResp(contentService.delContentTag(conn, contentId, groupKeyword, tag));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(path = "queryContentsByTags", //
			des = "查询标签", //
			ret = "内容对象数组"//
	)
	public APIResponse queryContentsByTags(//
			@P(t = "用户编号") Long userId, //
			@P(t = "内容类型Content.TYPE", r = false) Byte contentType, //
			@P(t = "内容状态Content.STATUS", r = false) Byte status, //
			@P(t = "上传用户编号", r = false) Long upUserId, //
			@P(t = "上传专栏编号", r = false) Long upChannelId, //
			@P(t = "标题", r = false) String title, //
			@P(t = "标签分组关键字", r = false) String groupKeyword, //
			@P(t = "标签列表（JSONArray）", r = false) JSONArray tags, //
			@P(t = "数量") Integer count, //
			@P(t = "偏移量") Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			String[] strTags = CodecUtils.convertJSONArray2StringArray(tags);
			List<Content> ret = contentService.queryContentsByTags(conn, contentType, status, upUserId, upChannelId,
					groupKeyword, strTags, count, offset);

			return APIResponse.getNewSuccessResp(ret);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "searchContents", //
			des = "搜索标签", //
			ret = "内容对象数组"//
	)
	public APIResponse searchContents(//
			@P(t = "用户编号") Long userId, //
			@P(t = "内容类型Content.TYPE", r = false) Byte contentType, //
			@P(t = "内容状态Content.STATUS", r = false) Byte status, //
			@P(t = "上传用户编号", r = false) Long upUserId, //
			@P(t = "上传专栏编号", r = false) Long upChannelId, //
			@P(t = "搜索关键字") String keyword, //
			@P(t = "数量") Integer count, //
			@P(t = "偏移量") Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			List<Content> ret = contentService.searchContentsByKeyword(conn, contentType, status, upUserId, upChannelId,
					keyword, count, offset);

			return APIResponse.getNewSuccessResp(ret);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getContentById", //
			des = "根据编号获取内容", //
			ret = "编号对应的内容"//
	)
	public APIResponse getContentById(//
			@P(t = "用户编号") Long userId, //
			@P(t = "内容编号") Long contentId//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			Content ret = contentService.getContentById(conn, contentId);

			return APIResponse.getNewSuccessResp(ServiceUtils.checkNull(ret));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getContentTags", //
			des = "获取内容上的标签", //
			ret = "标签名称数组（JSONArray）"//
	)
	public APIResponse getContentTags(//
			@P(t = "用户编号") Long userId, //
			@P(t = "内容编号") Long contentId, //
			@P(t = "标签分组关键字") String groupKeyword //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			JSONArray ret = contentService.getContentTagsById(conn, contentId, groupKeyword);

			return APIResponse.getNewSuccessResp(ret);
		}
	}

	///////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////////

	/**
	 * 
	 */
	@POSTAPI(//
			path = "createContentTag", //
			des = "创建内容标签", //
			ret = "所创建的对象"//
	)
	public APIResponse createContentTag(//
			@P(t = "标签分组关键字") String groupKeyword, //
			@P(t = "标签名称") String name //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {

			ContentTag ret = contentTagService.createTag(conn, groupKeyword, name);

			return APIResponse.getNewSuccessResp(ret);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "editContentTagStatus", //
			des = "设置内容标签状态（启用/禁用）", //
			ret = "影响的记录行数"//
	)
	public APIResponse editContentTagStatus(//
			@P(t = "标签编号") Long tagId, //
			@P(t = "状态") Byte status //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {

			int ret = contentTagService.editTagStatus(conn, tagId, status);

			return APIResponse.getNewSuccessResp(ret);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getContentTags", //
			des = "获取内容标签列表", //
			ret = "内容标签列表"//
	)
	public APIResponse getContentTags(//
			@P(t = "状态") Byte status, //
			@P(t = "标签分组关键字") String groupKeyword, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {

			List<ContentTag> ret = contentTagService.getTags(conn, status, groupKeyword, count, offset);

			return APIResponse.getNewSuccessResp(ret);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "createContentTagGroup", //
			des = "创建内容标签分组", //
			ret = "所创建的对象"//
	)
	public APIResponse createContentTagGroup(//
			@P(t = "标签分组大类") String type, //
			@P(t = "标签分组关键字") String keyword, //
			@P(t = "备注", r = false) String remark //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {

			ContentTagGroup ret = contentTagService.createTagGroup(conn, type, keyword, remark);

			return APIResponse.getNewSuccessResp(ret);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getContentTagGroupTypes", //
			des = "获取内容标签分组大类列表", //
			ret = "内容标签分组大类列表"//
	)
	public APIResponse getContentTagGroupTypes() throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {

			List<String> ret = contentTagService.getTagGroupTypes(conn);

			return APIResponse.getNewSuccessResp(ret);
		}
	}
}
