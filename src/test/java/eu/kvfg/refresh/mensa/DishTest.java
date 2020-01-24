package eu.kvfg.refresh.mensa;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Dish}
 *
 * @author Lukas Nasarek
 */
public class DishTest {
    
    private final Dish testDish1 = new Dish("Gericht 1", "Extra 1, Extra 2");
    private final Dish testDish2 = new Dish("Gericht 2", "Extra 2, Extra 3");
    private final Dish testDish3 = new Dish("Gericht 3", "Extra 3, Extra 4");

    private final Dish emptyDish1 = new Dish("Leer 1", "");

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void fromElementNormal() {
        assertThat(testDish1)
            .isEqualToComparingFieldByField(createDishFromHtml("<td>Gericht 1 <p class=\"uk-text-small\">Extra 1, Extra 2</p> </td>").get());
        assertThat(testDish2)
            .isEqualToComparingFieldByField(createDishFromHtml("<td>Gericht 2 <p class=\"uk-text-small\">Extra 2, Extra 3</p> </td>").get());
        assertThat(testDish3)
            .isEqualToComparingFieldByField(createDishFromHtml("<td> <p class=\"uk-text-left\">Gericht 3</p> <p class=\"uk-text-small\">Extra 3, Extra 4</p> </td>").get());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void fromElementEmpty() {
        assertThat(emptyDish1)
            .isEqualToComparingFieldByField(createDishFromHtml("<td> Leer 1 <p class=\"uk-text-small\"></p> </td>").get());
        assertThat(Optional.empty())
            .isEqualTo(createDishFromHtml("<td>&nbsp; <p class=\"uk-text-small\">&nbsp;</p> </td>"));
        assertThat(Optional.empty())
            .isEqualTo(createDishFromHtml("<td> <p class=\"uk-text-small\"></p> </td>"));
    }

    @Test
    public void fromElementClosed() {
        assertThat(Optional.empty())
            .isEqualTo(createDishFromHtml("<td> FEIERTAG 1. MAI <p class=\"uk-text-small\"></p> </td>"));
        assertThat(Optional.empty())
            .isEqualTo(createDishFromHtml("<td> GESCHLOSSEN <p class=\"uk-text-small\"></p> </td>"));
    }

    /**
     * Emulates the HTML parsing process used for creating a {@link Dish}.
     *
     * @param html the mensa html fragment
     * @return the Dish parsed by {@link Dish#fromHtml(Element)}
     */
    private static Optional<Dish> createDishFromHtml(String html) {
        return Dish.fromHtml(Jsoup.parseBodyFragment(html).selectFirst("body"));
    }
}
