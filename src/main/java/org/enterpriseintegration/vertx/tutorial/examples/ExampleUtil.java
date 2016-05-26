package org.enterpriseintegration.vertx.tutorial.examples;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class ExampleUtil {
	public static Future<Vertx> deployVerticle(String ref, int instances) {
		Future<Vertx> future = Future.future();
		
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(ref, new DeploymentOptions().setInstances(instances), res -> {
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
	
	public static Future<Vertx> deployVerticle(String ref) {
		return deployVerticle(ref, 1);
	}
}
