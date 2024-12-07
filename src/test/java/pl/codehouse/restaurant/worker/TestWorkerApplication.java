package pl.codehouse.restaurant.worker;

import org.springframework.boot.SpringApplication;
import pl.codehouse.restaurant.WorkerApplication;

public class TestWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.from(WorkerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
