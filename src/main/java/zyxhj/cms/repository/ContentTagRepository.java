package zyxhj.cms.repository;

import zyxhj.cms.domain.ContentTag1;
import zyxhj.utils.data.rds.RDSRepository;

public class ContentTagRepository extends RDSRepository<ContentTag1> {

	public ContentTagRepository() {
		super(ContentTag1.class);
	}

}
