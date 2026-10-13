package ca.blockbreak.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ServerApplication implementation.
 */
@SpringBootApplication
public class ServerApplication {

    /**
     * Main function
     *
     * @param args arguments passed to the underlying SpringApplication
     */
	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

}
