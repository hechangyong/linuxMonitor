package com.ms.linuxMonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LinuxMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinuxMonitorApplication.class, args);
	}
}
