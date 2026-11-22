package in.aman.jgate.smart_gateway_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartGatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartGatewayServiceApplication.class, args);

		System.out.println("Smart-gateway-service started at 8080");
	}

}
