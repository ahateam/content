package zyxhj.custom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;

public class WxDataService {

	private static Logger log = LoggerFactory.getLogger(WxDataService.class);

	private WxMaInMemoryConfig wxMaInMemoryConfig;
	private WxMaService wxMaService;

	public WxDataService() {
		try {
			// 微信参数配置
			wxMaInMemoryConfig = new WxMaInMemoryConfig();
			wxMaInMemoryConfig.setAppid(WxDataService.APPID);// APPid
			wxMaInMemoryConfig.setSecret(WxDataService.APPSECRET);// AppSecret
			wxMaService = new WxMaServiceImpl();
			wxMaService.setWxMaConfig(wxMaInMemoryConfig);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static final String APPID = "wxbe41dad7130b6dcf";
	public static final String APPSECRET = "0487f9aab9d0863ac001bd76a9030987";

	public WxMaInMemoryConfig getWxMaInMemoryConfig() {
		return wxMaInMemoryConfig;
	}

	public WxMaService getWxMaService() {
		return wxMaService;
	}

}
