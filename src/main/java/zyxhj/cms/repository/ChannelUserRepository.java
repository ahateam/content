package zyxhj.cms.repository;

import zyxhj.cms.domain.ChannelUser;
import zyxhj.utils.data.rds.RDSRepository;

public class ChannelUserRepository extends RDSRepository<ChannelUser> {

	public ChannelUserRepository() {
		super(ChannelUser.class);
	}

	

}
