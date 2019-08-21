package zyxhj.pay.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zyxhj.custom.domain.GLpayApi;


public class PayUtil {

	private static Logger log = LoggerFactory.getLogger(PayUtil.class);

	public static String UID = "3150";

	public static String NOTIFY_URL = "http://您自己的域名/qpay/notifyPay";

	public static String RETURN_URL = "http://您自己的域名/qpay/returnPay";

	public static String BASE_URL = "http://pay.ebkf.net";

	public static String TOKEN = "ZHwfiHhZmfLFt8MTP7flBpW8pWZpF8f7";

	public static Map<String, Object> payOrder(Map<String, Object> remoteMap) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uid", UID);
		paramMap.put("notify_url", NOTIFY_URL);
		paramMap.put("return_url", RETURN_URL);
		paramMap.putAll(remoteMap);
		paramMap.put("key", getKey(paramMap));
		return paramMap;
	}

	public static String getKey(Map<String, Object> remoteMap) throws Exception {
		String key = "";
		if (null != remoteMap.get("goodsname")) {
			key += remoteMap.get("goodsname");
		}
		if (null != remoteMap.get("istype")) {
			key += remoteMap.get("istype");
		}
		if (null != remoteMap.get("notify_url")) {
			key += remoteMap.get("notify_url");
		}
		if (null != remoteMap.get("orderid")) {
			key += remoteMap.get("orderid");
		}
		if (null != remoteMap.get("orderuid")) {
			key += remoteMap.get("orderuid");
		}
		if (null != remoteMap.get("price")) {
			key += remoteMap.get("price");
		}
		if (null != remoteMap.get("return_url")) {
			key += remoteMap.get("return_url");
		}
		key += TOKEN;
		if (null != remoteMap.get("uid")) {
			key += remoteMap.get("uid");
		}
		return MD5Util.encryption(key);
	}

	public static boolean checkPayKey(GLpayApi payAPI) throws Exception {
		String key = "";
		if (!StringUtils.isBlank(payAPI.getOrderid())) {
			log.info("支付回来的订单号：" + payAPI.getOrderid());
			key += payAPI.getOrderid();
		}
		if (!StringUtils.isBlank(payAPI.getOrderuid())) {
			log.info("支付回来的支付记录的ID：" + payAPI.getOrderuid());
			key += payAPI.getOrderuid();
		}
		if (!StringUtils.isBlank(payAPI.getTrade_no())) {
			log.info("支付回来的平台订单号：" + payAPI.getTrade_no());
			key += payAPI.getTrade_no();
		}
		if (!StringUtils.isBlank(payAPI.getPrice())) {
			log.info("支付回来的价格：" + payAPI.getPrice());
			key += payAPI.getPrice();
		}
		if (!StringUtils.isBlank(payAPI.getRealprice())) {
			log.info("支付回来的真实价格：" + payAPI.getRealprice());
			key += payAPI.getRealprice();
		}
		log.info("支付回来的Key：" + payAPI.getKey());
		key += TOKEN;
		log.info("我们自己拼接的Key：" + MD5Util.encryption(key));
		return payAPI.getKey().equals(MD5Util.encryption(key));
	}

	public static String getOrderIdByUUId() {
		int machineId = 1;// 最大支持1-9个集群机器部署
		int hashCodeV = UUID.randomUUID().toString().hashCode();
		if (hashCodeV < 0) {// 有可能是负数
			hashCodeV = -hashCodeV;
		}
		// 0 代表前面补充0;d 代表参数为正数型
		return machineId + String.format("%01d", hashCodeV);
	}

}
