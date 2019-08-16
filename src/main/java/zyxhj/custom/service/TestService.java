//package zyxhj.custom.service;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import me.chanjar.weixin.mp.api.WxMpConfigStorage;
//import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
//import me.chanjar.weixin.mp.api.WxMpMessageRouter;
//import me.chanjar.weixin.mp.api.WxMpService;
//
//public class TestService {
//
//	private static Logger log = LoggerFactory.getLogger(TestService.class);
//
//	public TestService() {
//		try {
//
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
//	}
//
//	public static WxMpConfigStorage configStorage() {
//		WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
//		configStorage.setAppId("wx9aaf23b05328a771"); // APPid
//		configStorage.setSecret("6b72b49c33db086d6f62931f94e9ee1b"); // AppSecret
//		configStorage.setToken("wx3ch"); // 设置微信公众号的token
//		configStorage.setAesKey("6tLn50b5o97PhgdiVb5Ek0780VLx6yG97eiKTE9waxZ"); // 设置微信公众号的EncodingAESKey
//		return configStorage;
//	}
//
//	public static WxMpService wxMpService(WxMpConfigStorage configStorage) {
//		WxMpService wxMpService = new me.chanjar.weixin.mp.api.impl.WxMpServiceImpl();
//		wxMpService.setWxMpConfigStorage(configStorage);
//		return wxMpService;
//	}
//
//	public static WxMpMessageRouter messageRouter(WxMpService wxMpService) {
//		WxMpMessageRouter router = new WxMpMessageRouter(wxMpService);
//		return router;
//	}
//
//}
