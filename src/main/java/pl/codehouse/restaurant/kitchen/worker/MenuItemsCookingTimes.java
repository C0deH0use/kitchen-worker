package pl.codehouse.restaurant.kitchen.worker;

import java.util.HashMap;
import java.util.Map;

/**
 * Class used to store configuration for cooking times of given meals.
 */
public class MenuItemsCookingTimes extends HashMap<Integer, Integer> {

    private MenuItemsCookingTimes() {
        super();
    }

    private MenuItemsCookingTimes(Map<Integer, Integer> map) {
        super(map);
    }

    /**
     * Creates a new Cooking configuration instance our of a Map.Entry var arg values.
     *
     * @param cookingTimes meal per time values.
     * @return MenuItemsCookingTimes instance
     */
    @SafeVarargs
    public static MenuItemsCookingTimes from(Entry<Integer, Integer>... cookingTimes) {
        return new MenuItemsCookingTimes(Map.ofEntries(cookingTimes));
    }

    /**
     * Creates a new Cooking configuration instance our of  Map of values.
     *
     * @param cookingTimes meal per time values.
     * @return MenuItemsCookingTimes instance
     */
    public static MenuItemsCookingTimes ofMap(Map<Integer, Integer> cookingTimes) {
        return new MenuItemsCookingTimes(cookingTimes);
    }
}
