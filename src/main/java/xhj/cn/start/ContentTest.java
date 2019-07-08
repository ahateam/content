package xhj.cn.start;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.cms.domain.Content1;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.rds.RDSUtils;

public class ContentTest {

	private static DruidPooledConnection conn;

	public static void main(String[] args) {

		testDB();
		
	}

	private static void testDB() {
		System.out.println("testDB");
		try {
			DruidDataSource dds = DataSource.getDruidDataSource("rdsDefault.prop");

			// RDSUtils.dropTableByEntity(dds, Tunnel.class);

			RDSUtils.createTableByEntity(dds, Content1.class);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
