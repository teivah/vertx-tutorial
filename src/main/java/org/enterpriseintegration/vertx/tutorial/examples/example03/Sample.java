package org.enterpriseintegration.vertx.tutorial.examples.example03;

import org.enterpriseintegration.vertx.tutorial.examples.ExampleUtil;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * Example 03: Describe how to orchestrate asynchronous executions
 */
public class Sample extends AbstractVerticle {

	public static void main(String[] args) {
		ExampleUtil.deployVerticle("org.enterpriseintegration.vertx.tutorial.examples.example03.Sample");
	}

	@Override
	public void start() throws Exception {
		method1();
		method2();
	}

	// Most verbose but natural method using setHandler
	private void method1() {
		// Create first step
		Future<String> future1 = doSmthg1();

		// Set handler on future1
		future1.setHandler(res1 -> {
			if (res1.succeeded()) {
				Future<String> future2 = doSmthg2(res1.result());

				// Set handler on future 2
				future2.setHandler(res2 -> {
					if (res2.succeeded()) {
						Future<String> future3 = doSmthg3(res2.result());

						// Set handler on future 3
						future3.setHandler(res3 -> {
							if (res3.succeeded()) {
								System.out.println(res3.result());
							} else {
								// Manage doSmthg3 errors
							}
						});
					} else {
						// Manage doSmthg2 errors
					}
				});
			} else {
				// Manage doSmthg1 errors
			}
		});
	}

	// Compose method using Future.compose(), possible from vertx-core 3.2.1
	private void method2() {
		// Create first step
		Future<String> future1 = doSmthg1();

		// Define future1 composition
		future1.compose(s1 -> {
			Future<String> future2 = doSmthg2(future1.result());

			// Define future2 composition
			future2.compose(s2 -> {
				Future<String> future3 = doSmthg3(future2.result());

				// Because the future3 is the last, we define here a handler
				future3.setHandler(handler -> {
					if (handler.succeeded()) {
						System.out.println(handler.result());
					} else {
						// Manage doSmthg3 errors
					}
				});
			} , Future.future().setHandler(handler -> {
				// Manage doSmthg2 errors
			}));
		} , Future.future().setHandler(handler -> {
			// Manage doSmthg1 errors
		}));
	}

	// Utility method 1
	private Future<String> doSmthg1() {
		Future<String> future = Future.future();

		// Do something
		future.complete("!");

		return future;
	}

	// Utility method 2
	private Future<String> doSmthg2(String input) {
		Future<String> future = Future.future();

		// Do something
		future.complete("world" + input);

		return future;
	}

	// Utility method 3
	private Future<String> doSmthg3(String input) {
		Future<String> future = Future.future();

		// Do something
		future.complete("Hello " + input);

		return future;
	}
}