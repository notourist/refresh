package eu.kvfg.refresh.mensa;

import eu.kvfg.refresh.util.cache.Cache;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ugly and awful mensa menu HTML parsing.
 *
 * @author Lukas Nasarek
 */
@Component
public class MensaCache extends Cache<DailyMenu> {

    private static final DateTimeFormatter DATE_FORMATTER
        = new DateTimeFormatterBuilder()
        .appendPattern("dd.MM.yyyy")
        .toFormatter();

    private static final Pattern NOTE_PATTERN = Pattern.compile("(\\s([\\D])+)");

    @Value("${mensa.url}")
    private String mensaUrl;

    @Value("${mensa.day-count}")
    private int dayCount;

    @Getter
    private Map<Integer, String> notes;

    @Autowired
    protected MensaCache(@Qualifier("cacheRefreshPool") ThreadPoolTaskScheduler taskScheduler,
                         @Value("${mensa.refresh-rate}") long fixedRate) {
        super(taskScheduler, MensaCache.class, fixedRate);
    }

    /**
     * Parses the last mensa day of a mensa week header.
     *
     * @param header expected input: {@code 30.04. - 03.05.2018 (KW18)}
     * @return the parsed {@link LocalDate}
     */
    private static LocalDate getLastMensaDay(String header) {
        header = header.split("[-]")[1];
        // Select the 18 from 2018
        String currentYear = String.valueOf(LocalDate.now().getYear()).substring(2);

        // The splitPos gets selected by the current year ("20" + "19" = "2019")
        int splitPos = header.lastIndexOf("20" + currentYear);

        // Split behind the AA.BB.20XX
        String lastDay = header.substring(1, splitPos + 4);

        return LocalDate.parse(lastDay, DATE_FORMATTER);
    }

    private static Map<Integer, String> parseNotes(String notesString) {
        Map<Integer, String> notes = new HashMap<>();
        Matcher matcher = NOTE_PATTERN.matcher(notesString);
        int pos = 1;
        while (matcher.find()) {
            notes.put(pos++, matcher.group().trim());
        }
        return notes;
    }

    @PostConstruct
    public void start() {
        start(this::refreshMensaCache);
    }

    private List<DailyMenu> refreshMensaCache() throws IOException {
        List<DailyMenu> tempCache = new ArrayList<>();
        Document document = Jsoup.connect(mensaUrl).get();

        Elements headers = document.select("h4");
        Elements tables = document.select("table");

        // Get notes from second <table> tag
        notes = parseNotes(tables.get(1).text());

        int offset = 0;
        for (int i = 0; i < headers.size(); i++) {
            Element header = headers.get(i);

            // Check if the table for our current header even exists.
            // This is not always the case, f.e. if the Mensa is closed during holidays.
            // This decrements the offset by one, so the loop index plus the offset will still point at the right table.
            if (!"table".equals(header.nextElementSibling().nodeName())) {
                offset--;
                continue;
            }

            // Only select every second table as the Mensa suddenly decided to
            // give every food notice its own table.
            Element table = tables.get((i + offset) * 2);

            // Get all elements in the table.
            Elements tableEntries = table.select("tr").select("td");

            LocalDate lastDay = getLastMensaDay(header.text());
            tempCache.addAll(createMenus(tableEntries, lastDay));
        }
        return tempCache;
    }

    private List<DailyMenu> createMenus(Elements elements, LocalDate begin) {
        List<DailyMenu> menus = new ArrayList<>();

        // Iterate through all four days.
        for (int i = dayCount; i > 0; i--) {
            // Every 5th element is needed for a column.

            Map<String, Dish> dishes = new HashMap<>();
            Dish.fromHtml(elements.get(i + 5)).ifPresent(dish -> dishes.put("soup", dish));
            Dish.fromHtml(elements.get(i + 10)).ifPresent(dish -> dishes.put("salad", dish));
            Dish.fromHtml(elements.get(i + 15)).ifPresent(dish -> dishes.put("firstDinner", dish));
            Dish.fromHtml(elements.get(i + 20)).ifPresent(dish -> dishes.put("secondDinner", dish));
            Dish.fromHtml(elements.get(i + 25)).ifPresent(dish -> dishes.put("dessert", dish));

            DailyMenu menu = new DailyMenu(begin, dishes);
            if (!menu.isEmpty()) {
                menus.add(menu);
            }
            begin = begin.minusDays(1);
        }
        menus.sort(Comparator.comparing(DailyMenu::getDay));
        return menus;
    }
}
