package org.enterpriseintegration.vertx.tutorial.examples.example06;

import org.enterpriseintegration.vertx.tutorial.examples.ExampleUtil;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;

/**
 * Example 06: Vert.x filesystem API
 */
public class Sample extends AbstractVerticle {
	public static void main(String[] args) {
		ExampleUtil.deployVerticle("org.enterpriseintegration.vertx.tutorial.examples.example06.Sample");
	}

	@Override
	public void start() {
		readFile();
		writeFile();
	}

	//Read file method
	private void readFile() {
		//Retrieve a FileSystem object from vertx instance and call the non-blocking readFile method
		vertx.fileSystem().readFile("/tmp/read", handler -> {
			if (handler.succeeded()) {
				System.out.println(handler.result());
			} else {
				System.err.println("Error while reading from file: " + handler.cause().getMessage());
			}
		});
	}

	//Write file method
	private void writeFile() {
		//Retrieve a FileSystem object from vertx instance and call the non-blocking writeFile method
		vertx.fileSystem().writeFile("/tmp/write", Buffer.buffer("content"), handler -> {
			if (handler.succeeded()) {
				System.out.println("File written");
			} else {
				System.err.println("Error while writing in file: " + handler.cause().getMessage());
			}
		});
	}
}
