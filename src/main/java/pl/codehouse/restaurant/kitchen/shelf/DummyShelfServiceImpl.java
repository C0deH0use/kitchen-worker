package pl.codehouse.restaurant.kitchen.shelf;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
class DummyShelfServiceImpl implements ShelfService {
    @Override
    public Mono<Void> updateMenuItem(long menuItemId, int quantity) {
        return Mono.empty();
    }
}
