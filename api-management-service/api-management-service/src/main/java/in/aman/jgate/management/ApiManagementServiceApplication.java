package in.aman.jgate.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication
public class ApiManagementServiceApplication {

	public static void main(String[] args) {

		System.out.println("Working Directory: "
				+ System.getProperty("user.dir"));


		SpringApplication.run(ApiManagementServiceApplication.class, args);

		System.out.println("Api maangement service started at 8081");
	}

}
