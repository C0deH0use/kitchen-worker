package pl.codehouse.restaurant.kitchen.shelf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
class DummyShelfServiceImpl implements ShelfService {
    private static final Logger log = LoggerFactory.getLogger(DummyShelfServiceImpl.class);

    @Override
    public Mono<Void> updateMenuItem(long menuItemId, int quantity) {
        log.info("Updating menu item {} with quantity {}", menuItemId, quantity);
        return Mono.empty();
    }
}
