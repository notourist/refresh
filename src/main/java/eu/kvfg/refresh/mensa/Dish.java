package eu.kvfg.refresh.mensa;

import lombok.Getter;
import org.jsoup.nodes.Element;

import java.util.Optional;

/**
 * @author Lukas Nasarek
 */
class Dish {

    @Getter
    private final String literal;

    @Getter
    private final String notice;

    Dish(String literal, String notice) {
        this.literal = literal;
        this.notice = notice;
    }

    /**
     * Creates a {@link Dish} from the corresponding HTML element.
     *
     * @param element the HTML element which contains the dish information
     * @return the new dish or an empty {@link Optional}
     */
    static Optional<Dish> fromHtml(Element element) {
        String literal = element.ownText();
        String notice;
        if (literal.isEmpty()) {
            // There might be a p tag before the food text.
            // So we need to look inside it.
            literal = element.selectFirst("p").ownText();
            if (literal.isEmpty()) {
                return Optional.empty();
            }

            // The notice is also in the wrong place
            notice = element.select("p").get(1).ownText();
        } else if (literal.equals("GESCHLOSSEN")
            || literal.contains("FEIERTAG")) {
            return Optional.empty();
        } else {
            notice = element.children().get(0).text();
        }
        return Optional.of(new Dish(literal, notice));
    }
}
