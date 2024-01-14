package kg.alymzhan.petchatgpt;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}

