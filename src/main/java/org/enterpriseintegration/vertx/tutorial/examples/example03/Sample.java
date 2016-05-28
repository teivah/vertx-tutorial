package org.enterpriseintegration.vertx.tutorial.examples.example03;

import org.enterpriseintegration.vertx.tutorial.examples.ExampleUtil;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

/**
 * Example 03: Describe how to orchestrate asynchronous executions
 */
public class Sample extends AbstractVerticle {

	public static void main(String[] args) {
		ExampleUtil.deployVerticle("org.enterpriseintegration.vertx.tutorial.examples.example03.Sample");
	}

	@Override
	public void start() {
		method1();
		method2();
	}

	// Orchestration based on setHandler
	private void method1() {
		// Create first step
		Future<String> future1 = readFile();

		// Set handler on future1
		future1.setHandler(res1 -> {
			if (res1.succeeded()) {
				Future<String> future2 = writeFile(res1.result());

				// Set handler on future 2
				future2.setHandler(res2 -> {
					if (res2.succeeded()) {
						Future<String> future3 = copyFile(res2.result());

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

	// Orchestration based on compose
	private void method2() {
		// Create first step
		Future<String> future1 = readFile();

		// Define future1 composition
		future1.compose(s1 -> {
			Future<String> future2 = writeFile(future1.result());

			// Define future2 composition
			future2.compose(s2 -> {
				Future<String> future3 = copyFile(future2.result());

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

	// Read file method
	private Future<String> readFile() {
		Future<String> future = Future.future();

		// Retrieve a FileSystem object from vertx instance and call the
		// non-blocking readFile method
		vertx.fileSystem().readFile("src/main/resources/example03/read.txt", handler -> {
			if (handler.succeeded()) {
				System.out.println("Read content: " + handler.result());
				future.complete("read success");
			} else {
				System.err.println("Error while reading from file: " + handler.cause().getMessage());
				future.fail(handler.cause());
			}
		});

		return future;
	}

	// Write file method
	private Future<String> writeFile(String input) {
		Future<String> future = Future.future();

		String file = "src/main/resources/example03/write.txt";

		// Retrieve a FileSystem object from vertx instance and call the
		// non-blocking writeFile method
		vertx.fileSystem().writeFile(file, Buffer.buffer(input), handler -> {
			if (handler.succeeded()) {
				System.out.println("File written with " + input);
				future.complete(file);
			} else {
				System.err.println("Error while writing in file: " + handler.cause().getMessage());

			}
		});

		return future;
	}

	// Write file method
	private Future<String> copyFile(String input) {
		Future<String> future = Future.future();

		// Retrieve a FileSystem object from vertx instance and call the
		// non-blocking writeFile method
		vertx.fileSystem().copy(input, "src/main/resources/example03/writecopy.txt", handler -> {
			if (handler.succeeded()) {
				System.out.println("Copy done of " + input);
				future.complete("Copy success");
			} else {
				System.err.println("Error while copying a file: " + handler.cause().getMessage());
				future.fail(handler.cause());
			}
		});

		return future;
	}
}