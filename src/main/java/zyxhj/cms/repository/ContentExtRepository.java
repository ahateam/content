package zyxhj.cms.repository;

import zyxhj.cms.domain.ContentExt;
import zyxhj.utils.data.rds.RDSRepository;

public class ContentExtRepository extends RDSRepository<ContentExt> {

	public ContentExtRepository() {
		super(ContentExt.class);
	}

}
