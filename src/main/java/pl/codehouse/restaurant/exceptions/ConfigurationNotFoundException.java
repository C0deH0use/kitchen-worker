package pl.codehouse.restaurant.exceptions;

/**
 * Exception thrown when a Configuration is not found.
 */
public class ConfigurationNotFoundException extends RuntimeException {
    private final int menuItemId;

    public ConfigurationNotFoundException(int menuItemId) {
        super("Missing configuration for the following Menu Item: " + menuItemId);
        this.menuItemId = menuItemId;
    }

    public int getMenuItemId() {
        return menuItemId;
    }
}
