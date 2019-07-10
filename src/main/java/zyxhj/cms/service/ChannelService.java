package zyxhj.cms.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.cms.domain.Channel;
import zyxhj.cms.repository.ChannelRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;

public class ChannelService {

	private static Logger log = LoggerFactory.getLogger(ChannelService.class);

	private ChannelRepository channelRepository;

	public ChannelService() {
		try {
			channelRepository = Singleton.ins(ChannelRepository.class);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 创建专栏
	 */
	public Channel createChannel(DruidPooledConnection conn, String title, String data) throws Exception {
		Channel channel = new Channel();
		channel.id = IDUtils.getSimpleId();
		channel.status = Channel.STATUS.NORMAL.v();
		channel.createTime = new Date();
		channel.title = title;
		channel.data = data;

		channelRepository.insert(conn, channel);
		return channel;
	}

	/**
	 * 编辑专栏
	 */
	public int editChannel(DruidPooledConnection conn, Long id, String title, String data) throws Exception {
		Channel renew = new Channel();
		renew.title = title;
		renew.data = data;

		return channelRepository.updateByKey(conn, "id", id, renew, true);
	}

	public int editChannelStatus(DruidPooledConnection conn, Long id, Byte status) throws Exception {
		Channel renew = new Channel();
		renew.status = status;

		return channelRepository.updateByKey(conn, "id", id, renew, true);
	}

	/**
	 * 根据标签和Channel的一些属性，筛选和查询Channel
	 * 
	 */
	public void queryChannels(DruidPooledConnection conn) throws Exception {
	}

}
