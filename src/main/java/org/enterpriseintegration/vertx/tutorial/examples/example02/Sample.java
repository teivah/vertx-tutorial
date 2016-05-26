package org.enterpriseintegration.vertx.tutorial.examples.example02;

import org.enterpriseintegration.vertx.tutorial.examples.ExampleUtil;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Example 02: Create a POST method, retrieve a content sent in a JSON format,
 * execute a blocking code
 */
public class Sample extends AbstractVerticle {

	public static void main(String[] args) {
		ExampleUtil.deployVerticle("org.enterpriseintegration.vertx.tutorial.examples.example02.Sample");
	}

	@Override
	public void start(Future<Void> startFuture) {
		final Router router = Router.router(vertx);

		// Create a BodyHandler to have the capability to retrieve the body
		router.route().handler(BodyHandler.create());

		router.route(HttpMethod.POST, "/file").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();

			// Retrieve the body as a JsonObject
			JsonObject body = routingContext.getBodyAsJson();
			// Get the filename attribute value
			String filename = body.getString("filename");

			// Execute a blocking part
			vertx.executeBlocking(future -> {
				byte[] bytes = parseLargeFile(filename);
				// Complete the future with the generated objects in the
				// blocking method
				future.complete(bytes);
			} , res -> {
				if (res.succeeded()) {
					// If the blocking part succeedeed
					byte[] bytes = (byte[]) res.result();

					response.putHeader("Content-Type", "application/octet-stream");
					response.setChunked(true);
					response.write(Buffer.buffer(bytes));
					response.setStatusCode(200);
					response.end();
				} else {
					// Otherwise we manage to return an error
					response.setStatusCode(500);
					response.setStatusMessage("Internal error: " + res.cause().getMessage());
					response.end();
				}
			});
		});

		vertx.createHttpServer().requestHandler(router::accept).listen(8080, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});
	}

	private byte[] parseLargeFile(String filename) {
		// Parsing a large file
		byte[] bytes = "a random string just for having a passing example".getBytes();
		return bytes;
	}
}