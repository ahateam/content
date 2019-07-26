package zyxhj.custom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;

public class WxDataService {

	private static Logger log = LoggerFactory.getLogger(WxDataService.class);

	private WxMpInMemoryConfigStorage wxMpConfigStorage;
	private WxMpService wxMpService;

	public WxDataService() {
		try {
			// 微信参数配置
			wxMpConfigStorage = new WxMpInMemoryConfigStorage();
			wxMpConfigStorage.setAppId(WxDataService.APPID); // APPid
			wxMpConfigStorage.setSecret(WxDataService.APPSECRET); // AppSecret
			wxMpService = new WxMpServiceImpl();
			wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static final String APPID = "wxab27851c2563890e";
	public static final String APPSECRET = "dd3aaa313ec78540310ed1086ad76c7f";

	public WxMpInMemoryConfigStorage getWxMpConfigStorage() {
		return wxMpConfigStorage;
	}

	public WxMpService getWxMpService() {
		return wxMpService;
	}

}
