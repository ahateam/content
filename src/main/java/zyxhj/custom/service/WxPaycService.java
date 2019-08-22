package zyxhj.custom.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zyxhj.custom.domain.GLpayApi;
import zyxhj.pay.util.PayUtil;

public class WxPaycService {

	private static Logger log = LoggerFactory.getLogger(WxPaycService.class);

	public WxPaycService() {
		try {
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public Map<String, Object> pay(float price, int istype, String orderUid, String goodsnmae) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> remoteMap = new HashMap<String, Object>();
		remoteMap.put("price", price);
		remoteMap.put("istype", istype);
		remoteMap.put("orderid", PayUtil.getOrderIdByUUId());
		remoteMap.put("orderuid", orderUid);
		remoteMap.put("goodsname", goodsnmae);
		resultMap.put("data", PayUtil.payOrder(remoteMap));
		return resultMap;
	}

	public String notifyPay(GLpayApi payAPI) throws Exception {
		// 保证密钥一致性
		if (PayUtil.checkPayKey(payAPI)) {
			return "OK";
		} else {
			return "ERROR";
		}
	}

//	public ModelAndView returnPay(String orderid) {
//		boolean isTrue = false;
//		ModelAndView view = null;
//		// 根据订单号查找相应的记录:根据结果跳转到不同的页面
//		if (isTrue) {
//			view = new ModelAndView("/正确的跳转地址");
//		} else {
//			view = new ModelAndView("/没有支付成功的地址");
//		}
//		return view;
//	}

}
