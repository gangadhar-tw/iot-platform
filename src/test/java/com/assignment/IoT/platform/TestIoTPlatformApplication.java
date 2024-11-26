package com.assignment.IoT.platform;

import org.springframework.boot.SpringApplication;

public class TestIoTPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.from(IoTPlatformApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
