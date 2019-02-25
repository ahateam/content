package xhj.cn.start;

import io.vertx.core.Vertx;
import zyxhj.cms.controller.ContentController;
import zyxhj.core.controller.TestController;
import zyxhj.utils.Singleton;
import zyxhj.utils.ZeroVerticle;
import zyxhj.utils.data.DataSourceUtils;

public class ContentVerticle extends ZeroVerticle {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new ContentVerticle());
	}

	public String name() {
		return "content";
	}

	protected void init() throws Exception {

		DataSourceUtils.initDataSourceConfig();

		initCtrl(ctrlMap, Singleton.ins(TestController.class, "test"));

		initCtrl(ctrlMap, Singleton.ins(ContentController.class, "content"));

	}

}
