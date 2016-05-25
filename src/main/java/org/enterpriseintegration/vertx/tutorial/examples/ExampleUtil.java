package org.enterpriseintegration.vertx.tutorial.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class ExampleUtil {
	public static Future<Vertx> deployVerticle(AbstractVerticle verticle) {
		Future<Vertx> future = Future.future();
		
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(verticle, res -> {
			if (res.succeeded()) {
				System.out.println("Verticle deployed");
				future.complete(vertx);
			} else {
				System.out.println("Error while deploying a verticle: " + res.cause().getMessage());
				future.fail(res.cause().getMessage());
			}
		});
		
		return future;
	}
}
