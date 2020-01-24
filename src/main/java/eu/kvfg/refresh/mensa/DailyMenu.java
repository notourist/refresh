package eu.kvfg.refresh.mensa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

/**
 * POJO for the daily mensa menu.
 *
 * @author Lukas Nasarek
 */
class DailyMenu {

    @Getter
    private final LocalDate day;

    @Getter
    private final Map<String, Dish> dishes;

    DailyMenu(LocalDate day, Map<String, Dish> dishes) {
        this.day = day;
        this.dishes = dishes;
    }

    @JsonIgnore
    boolean isEmpty() {
        return dishes.values().isEmpty();
    }
}
