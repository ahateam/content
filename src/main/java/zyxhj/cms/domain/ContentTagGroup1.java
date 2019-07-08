package zyxhj.cms.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 
 * 通用标签
 */
@RDSAnnEntity(alias = "tb_cms_content_tag_group")
public class ContentTagGroup1 {

	/**
	 * 大类
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String type;

	/**
	 * 分组关键字（分类前缀 + 关键字）
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String keyword;

	/**
	 * 备注
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String remark;
}
