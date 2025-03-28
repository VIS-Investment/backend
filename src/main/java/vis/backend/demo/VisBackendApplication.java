package vis.backend.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VisBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(VisBackendApplication.class, args);
    }

}
