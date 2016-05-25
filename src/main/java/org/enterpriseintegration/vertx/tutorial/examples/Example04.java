package org.enterpriseintegration.vertx.tutorial.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;

/**
 * Example 04: Basic pipe and filter example by exposing an Event Bus endpoint and sending an enriched message to another Event Bus endpoint 
 */
public class Example04 extends AbstractVerticle {

	public static void main(String[] args) {
		Future<Vertx> deploymentFuture = ExampleUtil.deployVerticle(new Example04());
		
		//We need to send an endpoint once the verticle has been deployed so we create a custom handler on the deployment process
		deploymentFuture.setHandler(handler -> {
			EventBus eventBus = handler.result().eventBus();
			
			//Publish a message that will be managed by the verticle
			eventBus.publish("customer.create", new JsonObject().put("name", "ben"));
		});
	}
	
	@Override
	public void start(Future<Void> startFuture) {
		//Retrieve the EventBus object form the vertx one
		EventBus eventBus = vertx.eventBus();

		//Create a EventBus consumer and instantiate a JsonObject type message consumer
		MessageConsumer<JsonObject> createConsumer = vertx.eventBus().consumer("customer.create");
		
		//Handle new messages on customer.create endpoint
		createConsumer.handler(json -> {
			System.out.println("Received new customer: " + json.body());
			
			//Enrich the customer object
			JsonObject enrichedCustomer = enrichCustomer(json.body());
			
			//Publish the enriched message on another endpoint
			eventBus.publish("customer.completion", enrichedCustomer);
		});
		
		startFuture.complete();
	}
	
	//Enrich a JsonObject customer
	private JsonObject enrichCustomer(JsonObject input) {
		JsonObject output = new JsonObject(input.encode());
		//Do something
		output.put("id", "042");
		return output;
	}
}
