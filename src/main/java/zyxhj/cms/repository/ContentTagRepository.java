package zyxhj.cms.repository;

import zyxhj.cms.domain.ContentTag;
import zyxhj.utils.data.rds.RDSRepository;

public class ContentTagRepository extends RDSRepository<ContentTag> {

	public ContentTagRepository() {
		super(ContentTag.class);
	}

}
