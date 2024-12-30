package pl.codehouse.restaurant;

import org.springframework.boot.SpringApplication;
import pl.codehouse.restaurant.kitchen.WorkerApplication;

public class TestWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.from(WorkerApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }

}
