package space.artway.artwaycontent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableEurekaClient
@EnableFeignClients
public class ArtwayContentMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArtwayContentMsApplication.class, args);
    }

}
