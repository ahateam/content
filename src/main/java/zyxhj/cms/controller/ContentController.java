package zyxhj.cms.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.cms.domian.Content;
import zyxhj.cms.domian.ContentTag;
import zyxhj.cms.domian.ContentTagGroup;
import zyxhj.cms.service.ChannelService;
import zyxhj.cms.service.ContentService;
import zyxhj.cms.service.ContentTagService;
import zyxhj.cms.service.TaskWallService;
import zyxhj.core.domain.User;
import zyxhj.core.service.UserService;
import zyxhj.kkqt.domain.TaskList;
import zyxhj.kkqt.domain.TaskWall;
import zyxhj.utils.ServiceUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;

public class ContentController extends Controller {

	private static Logger log = LoggerFactory.getLogger(ContentController.class);

	private DruidDataSource dds;
	private SyncClient client;
	private ContentService contentService;
	private ContentTagService contentTagService;
	private ChannelService channelService;
	private TaskWallService taskWallService;
	private UserService userService;

	public ContentController(String node) {
		super(node);
		try {
			dds = DataSource.getDruidDataSource("rdsDefault.prop");
			client = DataSource.getTableStoreSyncClient("tsDefault.prop");

			contentService = Singleton.ins(ContentService.class);
			contentTagService = Singleton.ins(ContentTagService.class);
			channelService = Singleton.ins(ChannelService.class);
			taskWallService = Singleton.ins(TaskWallService.class);
			userService = Singleton.ins(UserService.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@ENUM(des = "内容类型")
	public Content.TYPE[] contentTypes = Content.TYPE.values();

	@ENUM(des = "内容状态")
	public Content.STATUS[] contentStatus = Content.STATUS.values();

	@ENUM(des = "标签状态")
	public ContentTag.STATUS[] contentTagStatus = ContentTag.STATUS.values();

	@ENUM(des = "标签分组类型")
	public ContentTagGroup.TAGGROUPTYPE[] contentTagGroupTypes = ContentTagGroup.TAGGROUPTYPE.values();

	@ENUM(des = "任务类型")
	public TaskWall.TYPE[] taskWallTypes = TaskWall.TYPE.values();

	@ENUM(des = "任务需求")
	public TaskWall.NEED[] taskWallNeed = TaskWall.NEED.values();

	@ENUM(des = "任务等级")
	public TaskWall.LEVEL[] taskWallLevel = TaskWall.LEVEL.values();

	@ENUM(des = "任务状态")
	public TaskWall.STATUS[] taskWallStatus = TaskWall.STATUS.values();

	@ENUM(des = "任务接取")
	public TaskWall.ACCESSSTATUS[] taskWallAccess = TaskWall.ACCESSSTATUS.values();

	@ENUM(des = "接取任务类型")
	public TaskList.TYPE[] taskListType = TaskList.TYPE.values();

	@ENUM(des = "任务完成状态")
	public TaskList.STATUS[] taskListStatus = TaskList.STATUS.values();

	/**
	 * 
	 */
	@POSTAPI(path = "createContentDraft", //
			des = "创建内容，保存为草稿", //
			ret = "所创建的对象"//
	)
	public APIResponse createContentDraft(//
			@P(t = "用户编号") Long userId, //
			@P(t = "用户编号") String module, //
			@P(t = "内容类型Content.TYPE") Byte type, //
			@P(t = "上传专栏编号", r = false) Long upChannelId, //
			@P(t = "标题") String title, //
			@P(t = "数据（JSON）") String data //
	) throws Exception {
		Long upUserId = userId;
		try (DruidPooledConnection conn = dds.getConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(
					contentService.createContentDraft(client, module, type, upUserId, upChannelId, title, data));
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
			@P(t = "用户编号") String module, //
			@P(t = "内容类型Content.TYPE") Byte type, //
			@P(t = "上传专栏编号", r = false) Long upChannelId, //
			@P(t = "标题") String title, //
			@P(t = "数据（JSON）") String data//
	) throws Exception {
		Long upUserId = userId;
		try (DruidPooledConnection conn = dds.getConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(
					contentService.createContentPublished(client, module, type, upUserId, upChannelId, title, data));
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
			@P(t = "片区编号") String id, //
			@P(t = "内容编号") Long contentId //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			contentService.auth(conn, client, contentId);// content鉴权
			contentService.delContentById(client, id, contentId);
			return APIResponse.getNewSuccessResp();
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
			@P(t = "用户编号") String _id, //
			@P(t = "内容编号") Long contentId, //
			@P(t = "标签分组关键字") String groupKeyword, //
			@P(t = "标签") String tag//
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			contentService.auth(conn, client, contentId);// content鉴权

			contentService.addContentTag(conn, _id, contentId, groupKeyword, tag);

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
			@P(t = "片区编号") String id, //
			@P(t = "内容编号") Long contentId, //
			@P(t = "标签分组关键字") String groupKeyword, //
			@P(t = "标签") String tag//
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			contentService.auth(conn, client, contentId);// content鉴权
			contentService.delContentTag(client, id, contentId, groupKeyword);
			return APIResponse.getNewSuccessResp();
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
			@P(t = "标签分组关键字", r = false) String groupKeyword, //
			@P(t = "隶属") String module, //
			@P(t = "数量") Integer count, //
			@P(t = "偏移量") Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(contentService.queryContentsByTags(client, module, contentType, status,
					groupKeyword, count, offset));
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
		try (DruidPooledConnection conn = dds.getConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			JSONObject ret = contentService.searchContentsByKeyword(client, contentType, status, upUserId, upChannelId,
					keyword, count, offset);

			return APIResponse.getNewSuccessResp(ret);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getContents", //
			des = "获取内容列表", //
			ret = "内容列表"//
	)
	public APIResponse getContents(//
			@P(t = "用户编号") Long userId, //
			@P(t = "所属模块") String module, //
			Integer count, //
			Integer offset //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(
					ServiceUtils.checkNull(contentService.getContents(conn, client, module, count, offset)));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getContentByType", //
			des = "根据类型获取内容", //
			ret = "内容列表"//
	)
	public APIResponse getContentByType(//
			@P(t = "用户编号") Long userId, //
			@P(t = "类型") Byte type, //
			Integer count, //
			Integer offset //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(
					ServiceUtils.checkNull(contentService.getContentByType(client, type, count, offset)));
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
			@P(t = "片区编号") String id, //
			@P(t = "内容编号") Long contentId//
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse
					.getNewSuccessResp(ServiceUtils.checkNull(contentService.getContentById(client, id, contentId)));
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
		try (DruidPooledConnection conn = dds.getConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(contentService.getContentTagsById(client, contentId, groupKeyword));
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
			@P(t = "标签分组id") Long groupId, //
			@P(t = "标签分组关键字") String groupKeyword, //
			@P(t = "隶属") String module, //
			@P(t = "标签名称") String name //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			ContentTag ret = contentTagService.createTag(conn, module, groupId, groupKeyword, name);

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
		try (DruidPooledConnection conn = dds.getConnection()) {
			contentTagService.editTagStatus(conn, tagId, status);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getContentTag", //
			des = "获取内容标签列表", //
			ret = "内容标签列表"//
	)
	public APIResponse getContentTag(//
			@P(t = "状态") Byte status, //
			@P(t = "隶属") String module, //
			@P(t = "标签分组id") String keyword, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			return APIResponse
					.getNewSuccessResp(contentTagService.getTags(conn, module, status, keyword, count, offset));
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
			@P(t = "隶属") String module, //
			@P(t = "标签分组类型") Byte tagGroupType, //
			@P(t = "标签分组关键字") String keyword, //
			@P(t = "备注", r = false) String remark //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			ContentTagGroup ret = contentTagService.createTagGroup(conn, module, tagGroupType, type, keyword, remark);

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
	public APIResponse getContentTagGroupTypes(//
			@P(t = "标签分组类型") Byte tagGroupType, //
			@P(t = "隶属") String module //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			return APIResponse.getNewSuccessResp(contentTagService.getTagGroupTypes(conn, module, tagGroupType));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getTags", //
			des = "获取标签", //
			ret = "标签列表"//
	)
	public APIResponse getTags(//
			@P(t = "隶属") String module, //
			@P(t = "标签状态") Byte status, //
			@P(t = "标签") String keyword, //
			Integer count, //
			Integer offset //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			return APIResponse
					.getNewSuccessResp(contentTagService.getTags(conn, module, status, keyword, count, offset));
		}
	}

	///////////////////////////////////////////
	///////////////////////////////////////////
	///////////////////////////////////////////
	///////////////////////////////////////////
	// 专栏

	/**
	 * 
	 */
	@POSTAPI(//
			path = "createChannel", //
			des = "创建专栏", //
			ret = "专栏"//
	)
	public APIResponse createChannel(//
			@P(t = "隶属") String module, //
			@P(t = "专栏标题") String title, //
			@P(t = "标签") String tags, //
			@P(t = "专栏数据") String data //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			return APIResponse.getNewSuccessResp(channelService.createChannel(client, module, title, tags, data));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "editChannel", //
			des = "修改专栏", //
			ret = "专栏"//
	)
	public APIResponse editChannel(//
			@P(t = "分片id") String _id, //
			@P(t = "专栏id") Long id, //
			@P(t = "专栏标题") String title, //
			@P(t = "专栏数据") String data //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			channelService.editChannel(client, _id, id, title, data);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "editChannelStatus", //
			des = "修改专栏状态", //
			ret = "专栏"//
	)
	public APIResponse editChannelStatus(//
			@P(t = "分片id") String _id, //
			@P(t = "专栏id") Long id, //
			@P(t = "专栏状态") Byte status //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			channelService.editChannelStatus(client, _id, id, status);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getContentByChannelId", //
			des = "根据专栏id获取内容", //
			ret = "专栏"//
	)
	public APIResponse getContentByChannelId(//
			@P(t = "隶属") String module, //
			@P(t = "专栏id") Long channelId, //
			@P(t = "专栏状态") Byte status, //
			Integer count, //
			Integer offset //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			return APIResponse.getNewSuccessResp(
					channelService.getContentByChannelId(client, module, channelId, status, count, offset));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getChannel", //
			des = "获取专栏列表", //
			ret = "专栏"//
	)
	public APIResponse getChannel(//
			@P(t = "隶属") String module, //
			Integer count, //
			Integer offset //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			return APIResponse.getNewSuccessResp(channelService.getChannel(client, module, count, offset));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getChannelByStatus", //
			des = "根据状态获取专栏列表", //
			ret = "专栏"//
	)
	public APIResponse getChannel(//
			@P(t = "隶属") String module, //
			@P(t = "状态") Byte status, //
			Integer count, //
			Integer offset //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			return APIResponse
					.getNewSuccessResp(channelService.getChannelByStatus(client, module, status, count, offset));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getChannelByTags", //
			des = "根据标签获取专栏列表", //
			ret = "专栏"//
	)
	public APIResponse getChannelByTags(//
			@P(t = "隶属") String module, //
			@P(t = "状态") Byte status, //
			@P(t = "标签") String tags, //
			Integer count, //
			Integer offset //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			return APIResponse
					.getNewSuccessResp(channelService.getChannelByTags(client, module, status, tags, count, offset));
		}
	}

///////////////////////////////////////////
///////////////////////////////////////////
///////////////////////////////////////////
///////////////////////////////////////////
// 任务墙

	/**
	 * 
	 */
	@POSTAPI(//
			path = "createTask", //
			des = "创建任务", //
			ret = "任务"//
	)
	public APIResponse createTask(//
			@P(t = "隶属") String module, //
			@P(t = "任务类型") Byte type, //
			@P(t = "任务等级") Byte level, //
			@P(t = "需求") String needs, //
			@P(t = "创建Id") Long upUsreId, //
			@P(t = "任务时间") Date time, //
			@P(t = "地区", r = false) String pos, //
			@P(t = "需求标题") String title, //
			@P(t = "需求金额") Double money, //
			@P(t = "需求详细") String detail, //
			@P(t = "任务参与人数") Byte accessStatus, //
			@P(t = "标签") String tags //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			return APIResponse.getNewSuccessResp(taskWallService.createTaskWallPublished(client, module, type, level,
					needs, upUsreId, time, pos, title, tags, money, detail, accessStatus));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "editTaskWallStatus", //
			des = "修改任务状态", //
			ret = ""//
	)
	public APIResponse editTaskWallStatus(//
			@P(t = "任务分片编号") String _id, //
			@P(t = "任务id") Long taskWallId, //
			@P(t = "状态") Byte status //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			taskWallService.editTaskListStatus(client, _id, taskWallId, status);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getTask", //
			des = "根据标签/类型/状态获取任务", //
			ret = "任务列表"//
	)
	public APIResponse getTaskByTag(//
			@P(t = "隶属") String module, //
			@P(t = "状态  如无此条件  为null", r = false) Byte status, //
			@P(t = "类型  如无此条件  为null", r = false) Byte type, //
			@P(t = "标签  如无此条件  为null", r = false) String tags, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			return APIResponse
					.getNewSuccessResp(taskWallService.getTaskByTag(client, module, type, status, tags, count, offset));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getTaskById", //
			des = "获取任务详情", //
			ret = "任务列表"//
	)
	public APIResponse getTaskById(//
			@P(t = "任务分片编号") String _id, //
			@P(t = "任务id") Long taskWallId //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			return APIResponse.getNewSuccessResp(taskWallService.getTaskById(client, _id, taskWallId));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "delTaskById", //
			des = "删除任务", //
			ret = ""//
	)
	public APIResponse delTaskById(//

			@P(t = "任务分片编号") String _id, //
			@P(t = "任务id") Long taskWallId //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			taskWallService.delTaskById(client, _id, taskWallId);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getTaskByUpUserId", //
			des = "用户根据状态/类型获取自己的发布列表", //
			ret = "任务列表"//
	)
	public APIResponse getTaskByUpUserId(//
			@P(t = "隶属") String module, //
			@P(t = "用户编号") Long upUserId, //
			@P(t = "状态  如无此条件  为null", r = false) Byte status, //
			@P(t = "类型  如无此条件  为null", r = false) Byte type, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			return APIResponse.getNewSuccessResp(
					taskWallService.getTaskByUpUserId(client, module, upUserId, type, status, count, offset));
		}
	}

///////////////////////////////////////////
///////////////////////////////////////////
///////////////////////////////////////////
///////////////////////////////////////////
//任务接取

	/**
	 * 
	 */
	@POSTAPI(//
			path = "acceptanceTask", //
			des = "接取任务", //
			ret = "任务"//
	)
	public APIResponse acceptanceTask(//
			@P(t = "隶属") String module, //
			@P(t = "接取人id") Long accUserId, //
			@P(t = "状态") Byte status, //
			@P(t = "任务类型") Byte type, //
			@P(t = "任务标题") String taskTitle, //
			@P(t = "任务分片编号") String task_id, //
			@P(t = "任务id") Long taksId, //
			@P(t = "上传者用户编号") Long upUserId, //
			@P(t = "任务人数 （单人   多人）") Byte accessStatus, //
			@P(t = "私密信息") String proviteData //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			return APIResponse.getNewSuccessResp(taskWallService.acceptanceTask(client, module, accUserId, type,
					taskTitle, task_id, taksId, upUserId, accessStatus, proviteData));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getTaskList", //
			des = " 根据任务状态或类型查询任务", //
			ret = "任务列表"//
	)
	public APIResponse getTaskList(//
			@P(t = "隶属") String module, //
			@P(t = "用户Id") Long accUserId, //
			@P(t = "任务状态   如无此条件  为null", r = false) Byte status, //
			@P(t = "任务类型  如无此条件  为null", r = false) Byte type, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			return APIResponse.getNewSuccessResp(
					taskWallService.getTaskListByTypeORStatus(client, module, accUserId, type, status, count, offset));
		}
	}

///////////////////////////////////////////
///////////////////////////////////////////
///////////////////////////////////////////
///////////////////////////////////////////
//微信登录

	/**
	 * 
	 */
	@POSTAPI(//
			path = "loginByWxOpenId", //
			des = " 微信号登录", //
			ret = "用户信息"//
	)
	public APIResponse loginByWxOpenId(//
			@P(t = "微信openId") String wxOpenId, //
			@P(t = "用户名") String name, //
			@P(t = "扩展信息") String ext //

	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			return APIResponse.getNewSuccessResp(userService.loginByWxOpenId(conn, wxOpenId, name, ext));
		}
	}

	/**
	 * 修改用户的身份证
	 */
	@POSTAPI(//
			path = "editUserIdNumber", //
			des = "修改用户的身份证", //
			ret = "返回修改信息")
	public APIResponse editUserIdNumber(//
			@P(t = "管理员编号") Long adminUsreId, //
			@P(t = "用户编号") Long userId, //
			@P(t = "用户身份证号码(已添加索引，无需查重）") String IdNumber //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			return APIResponse.getNewSuccessResp(userService.editUserIdNumber(conn, adminUsreId, userId, IdNumber));
		}
	}

	/**
	 * 修改用户的身份证
	 */
	@POSTAPI(//
			path = "editUserInfo", //
			des = "修改用户的信息", //
			ret = "返回修改信息")
	public APIResponse editUserInfo(//
			@P(t = "用户编号") Long userId, //
			@P(t = "用户名", r = false) String name, //
			@P(t = "电话号码", r = false) String mobile, //
			@P(t = "邮箱", r = false) String email //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			return APIResponse.getNewSuccessResp(userService.editUserInfo(conn, userId, name, mobile, email));
		}
	}

	/**
	 * 获取用户
	 */
	@POSTAPI(//
			path = "getUserById", //
			des = "根据id用户的信息", //
			ret = "返回用户信息")
	public APIResponse getUserById(//
			@P(t = "用户编号") Long userId //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			return APIResponse.getNewSuccessResp(userService.getUserById(conn, userId));
		}
	}

}
