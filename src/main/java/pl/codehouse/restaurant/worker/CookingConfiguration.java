package pl.codehouse.restaurant.worker;

import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CookingConfiguration {

    @Bean
    MenuItemsCookingTimes menuItemsCookingTimes() {
        return MenuItemsCookingTimes.from(
                Map.entry(1000, 16_000),
                Map.entry(1001, 20_000),
                Map.entry(1002, 10_000),
                Map.entry(1003, 25_000),
                Map.entry(1004, 21_000)
        );
    }
}
