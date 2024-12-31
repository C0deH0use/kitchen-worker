package pl.codehouse.restaurant.kitchen.worker;

import java.time.Duration;
import java.util.Optional;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import pl.codehouse.restaurant.kitchen.Command;
import pl.codehouse.restaurant.kitchen.Context;
import pl.codehouse.restaurant.kitchen.ExecutionResult;
import pl.codehouse.restaurant.kitchen.exceptions.ConfigurationNotFoundException;
import pl.codehouse.restaurant.kitchen.shelf.ShelfService;
import reactor.core.publisher.Mono;

@Component
class CookMenuItemCommand implements Command<KitchenWorkerRequestMessage, Boolean> {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(CookMenuItemCommand.class);

    private final MenuItemsCookingTimes menuItemsPerTimeCooked;

    private final ShelfService shelfService;

    CookMenuItemCommand(ShelfService shelfService, MenuItemsCookingTimes cookingTimes) {
        this.shelfService = shelfService;
        this.menuItemsPerTimeCooked = cookingTimes;
    }

    @Override
    public Mono<ExecutionResult<Boolean>> execute(Context<KitchenWorkerRequestMessage> context) {
        KitchenWorkerRequestMessage message = context.request();
        LOGGER.info("Cooking following menu items: {}", message);

        Optional<Integer> cookingTimePerMenuItem = getCookingTimePerMenuItem(message.menuItemId());
        if (cookingTimePerMenuItem.isEmpty()) {
            return Mono.just(ExecutionResult.failure(new ConfigurationNotFoundException(message.menuItemId())));
        }
        long cookingTime = cookingTimePerMenuItem
                .map(time -> time * message.quantity())
                .orElse(1);

        return Mono.delay(Duration.ofMillis(cookingTime))
                .flatMap(signal -> shelfService.updateMenuItem(message.menuItemId(), message.quantity()))
                .thenReturn(ExecutionResult.success(Boolean.TRUE));
    }


    private Optional<Integer> getCookingTimePerMenuItem(int menuItem) {
        return Optional.ofNullable(menuItemsPerTimeCooked.get(menuItem));
    }
}
