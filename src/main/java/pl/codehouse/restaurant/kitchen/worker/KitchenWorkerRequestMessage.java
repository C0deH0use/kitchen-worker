package pl.codehouse.restaurant.kitchen.worker;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a message for notifying New Kitchen Request for a given Menu Item.
 * This record is used for deserialization when listening on menu requests by kitchen workers.
 */
public record KitchenWorkerRequestMessage(
        @JsonProperty("menuItemId")
        int menuItemId,
        @JsonProperty("quantity")
        int quantity
) {
}
