package pl.codehouse.restaurant.shelf;

import reactor.core.publisher.Mono;

/**
 * Shelf related communication.
 */
public interface ShelfService {
    /**
     * Update the shelf with given MenuItem and it's quantity.
     *
     * @param menuItemId Updating menu item id.
     * @param quantity Quantity by which the update is done,
     * @return Void On Success.
     */
    Mono<Void> updateMenuItem(long menuItemId, int quantity);
}
