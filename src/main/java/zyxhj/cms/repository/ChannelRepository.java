package zyxhj.cms.repository;

import zyxhj.cms.domain.Channel;
import zyxhj.utils.data.rds.RDSRepository;

public class ChannelRepository extends RDSRepository<Channel> {

	public ChannelRepository() {
		super(Channel.class);
	}

}
