package org.enterpriseintegration.vertx.tutorial.examples.example05;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

/**
 * Example 05 - Standard: Deploy a standard verticle
 */
public class StandardSample extends AbstractVerticle {

	private static final int instances = 2;

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		// Create a custom configuration from a JsonObject
		JsonObject config = new JsonObject().put("sleep", 3000);

		vertx.deployVerticle("org.enterpriseintegration.vertx.tutorial.examples.example05.StandardSample",
				// Instantiate a DeploymentOptions by setting an explicit number
				// of instances and referencing a JSON configuration
				new DeploymentOptions().setInstances(instances).setConfig(config), res -> {
					if (res.succeeded()) {
						System.out.println("Standard verticle deployed");

						// Send messages on the event bus
						EventBus eventBus = vertx.eventBus();
						eventBus.publish("event", "event01");
						eventBus.send("event", "event02");
						eventBus.send("event", "event03");
					} else {
						System.out.println("Error while deploying a verticle: " + res.cause().getMessage());
					}
				});
	}

	@Override
	public void start(Future<Void> startFuture) {
		System.out.println("Creating an instance with PID " + Thread.currentThread().getId());

		// Retrieving the configuration initialized by the deployer
		int sleep = config().getInteger("sleep");

		EventBus eventBus = vertx.eventBus();

		// Consume messages on the event bus
		eventBus.consumer("event", message -> {
			System.out.println("PID " + Thread.currentThread().getId() + " received " + message.body());
			try {
				// In this example we show how bad it is to block the event
				// loop. You will receive alerts from the Vert.x engine
				Thread.sleep(sleep);
			} catch (Exception e) {
			}
		});

		startFuture.complete();
	}
}