package org.enterpriseintegration.vertx.tutorial.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class ExampleUtil {
	public static void deployVerticle(AbstractVerticle verticle) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(verticle, res -> {
			if (res.succeeded()) {
				System.out.println("Verticle deployed");
			} else {
				System.out.println("Error while deploying a verticle: " + res.cause().getMessage());
			}
		});
	}
}
