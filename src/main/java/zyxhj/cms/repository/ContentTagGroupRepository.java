package zyxhj.cms.repository;

import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.cms.domain.ContentTagGroup;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

public class ContentTagGroupRepository extends RDSRepository<ContentTagGroup> {

	public ContentTagGroupRepository() {
		super(ContentTagGroup.class);
	}

	public List<String> getContentTagGroupTypes(DruidPooledConnection conn) throws ServerException {
		return getColumnStrings(conn, "type", null, null, 512, 0);
	}
}
