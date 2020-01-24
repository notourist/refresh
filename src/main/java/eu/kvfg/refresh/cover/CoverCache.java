package eu.kvfg.refresh.cover;

import eu.kvfg.refresh.grade.Grade;
import eu.kvfg.refresh.grade.GradeComparator;
import eu.kvfg.refresh.util.cache.Cache;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Reads and caches the cover plan every 5 minutes (see cover.refresh-rate).
 *
 * @author Lukas Nasarek
 */
@Component
public class CoverCache extends Cache<Cover> {

    private final Environment environment;

    @Value("${cover.url}")
    private String coverUrl;

    @Value("#{'${cover.directories}'.split(',')}")
    private List<String> directories;

    @Value("${cover.file}")
    private String file;

    @Autowired
    protected CoverCache(@Qualifier("cacheRefreshPool") ThreadPoolTaskScheduler taskScheduler,
                         @Value("${cover.refresh-rate}") long fixedRate,
                         Environment environment) {
        super(taskScheduler, CoverCache.class, fixedRate);
        this.environment = environment;
    }

    private static Predicate<Cover> teacherEquals(String teacherName) {
        final String normalized = teacherName
          							.toUpperCase()
                                    .replaceAll("AE", "Ä")
          							.replaceAll("OE", "Ö")
          							.replaceAll("UE", "Ü")
          							.replaceAll("SS", "ß");
        return cover -> cover.getTeacher().toUpperCase().equals(normalized)
            || cover.getNewTeacher().toUpperCase().equals(normalized)
            || "?".equals(cover.getTeacher())
            || "?".equals(cover.getNewTeacher());
    }

    /**
     * Parses the cover date from the cover header.
     *
     * @param header expected input: {@code 9.5.2018 Mittwoch} or {@code 23.12.2018 Montag}
     * @return the parsed {@link LocalDate}
     */
    private static LocalDate parseDate(String header) {
        // Split at the blank between date and day
        int blankIndex = header.indexOf(" ");
        String realHeader = header.substring(0, blankIndex);

        // Looks complicated is easy: Split the AA.BB.XXXX at the points
        // and make the String[] to Int[]
        Integer[] dateParts = Arrays
            .stream(realHeader.split("[.]"))
            .map(Integer::valueOf)
            .toArray(Integer[]::new);

        // Why are we having more than day, month and year?
        // The array should always have 3 parts.
        if (dateParts.length != 3) {
            throw new DateTimeParseException("DateParts array too big or small. " +
                "Expected length of 3, got " + dateParts.length,
                realHeader, -1);
        }

        return LocalDate.of(dateParts[2], dateParts[1], dateParts[0]);
    }

    /**
     * Reads the number of cover plan pages from the header.
     *
     * @param header the cover plan header
     * @return the number of cover plan pages
     */
    private static int parsePageCount(String header) {
        //'7.11.2017 Dienstag (Seite 2 / 2)' is splitted to "/ 2)" is replaced to '2'
        return Integer
            .parseInt(header
                .split("/")[1]
                .replaceAll("[\\D]", ""));
    }

    /**
     * Returns the cover changes based on the given HTML elements.
     *
     * @param columns the cover plan columns
     * @param date the cover date
     */
    private static List<Cover> coverFromHtml(Elements columns, LocalDate date) {
        Cover.Streamer streamer = new Cover.Streamer(date);

        return columns
            .stream()
            .map(Element::children)
            .flatMap(streamer::fromHtml)
            .collect(Collectors.toList());
    }

    @PostConstruct
    public void start() {
        // Running outside of the school network, use fake cover data.
        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
            start(this::devRefreshCoverCache);
        } else {
            start(this::refreshCoverCache);
        }
    }

    /**
     * Parses the cover plan, scheduled as specified in the application.yml.
     * The cover plan is not accessible from outside the school.
     * For development purposes a mock plan is used.
     */
    private List<Cover> refreshCoverCache() throws Exception {
        List<Cover> tempCache = new ArrayList<>();

        for (String directory : directories) {
            // Get first page to parse the directory header.
            String pageUrl = buildPageUrl(directory, 1);
            Document headerDocument = Jsoup.connect(pageUrl).get();

            String header = headerDocument.getElementsByClass("mon_title").text();

            int maxPages = 1;
            // Parse page count based on the header.
            if (header.contains("/")) {
                maxPages = parsePageCount(header);
            }

            for (int currentPageCount = 1; currentPageCount <= maxPages; currentPageCount++) {
                // Rebuild doc url based on current page count.
                String dynamicPageUrl = buildPageUrl(directory, currentPageCount);
                Document dynamicDocument = Jsoup.connect(dynamicPageUrl).get();

                Elements columns = dynamicDocument.select(".mon_list .odd, .mon_list .even");

                LocalDate date = parseDate(header);
                tempCache.addAll(coverFromHtml(columns, date));
            }
        }
        return tempCache;
    }

    private List<Cover> devRefreshCoverCache() {
        if (!cached.isEmpty()) {
            // Cache is already populated.
            return new ArrayList<>(cached);
        }
        ArrayList<Cover> tempCache = new ArrayList<>();
        tempCache.add(
            new Cover(LocalDate.of(2018, 12, 3),
                Grade.fromCover("Jg1"),
                "1", true, "Lehrer1",
                "Fach1", "Raum1", "Lehrer2",
                "Fach2", "Raum2", "Nachricht1",
                false, ""));
        tempCache.add(
            new Cover(LocalDate.of(2018, 12, 3),
                Grade.fromCover("Jg2"),
                "2", false, "Lehrer2",
                "Fach2", "Raum2", "",
                "", "", "Nachricht2",
                true, ""));
        tempCache.add(
            new Cover(LocalDate.of(2018, 12, 3),
                Grade.fromCover("10abc"),
                "5", false, "",
                "Fach3", "Raum3", "",
                "", "", "Nachricht3",
                false, ""));

        return tempCache;
    }

    /**
     * Returns every change where the grade is either the given grade or '?'.
     *
     * @param grade the grade
     * @return the grades' covers
     */
    List<Cover> getChangesByGrade(Grade grade) {
        return cached
            .stream()
            .filter(cover -> GradeComparator.contains(grade, cover.getGrade()))
            .collect(Collectors.toList());
    }

    /**
     * Returns the grade changes without teacher names.
     *
     * @param grade the user's grade
     * @return the cleaned cover changes
     */
    List<Cover> getChangesByGradeCleaned(Grade grade) {
        return getChangesByGrade(grade)
            .stream()
            .map(Cover::withoutTeacherNames)
            .collect(Collectors.toList());
    }

    /**
     * Returns every change where the old or new teacher name is either the given name or '?'.
     *
     * @param teacherName the teacher's name
     * @return the teacher's covers
     */
    List<Cover> getChangesByTeacher(String teacherName) {
        return cached
            .stream()
            .filter(teacherEquals(teacherName))
            .collect(Collectors.toList());
    }

    /**
     * Creates a cover plan page url based on the {@code directory} and {@code pageCount}
     *
     * @param directory the cover file's directory
     * @param pageNumber the page which contains the covers
     * @return the cover plan page url
     */
    private String buildPageUrl(String directory, int pageNumber) {
        if (pageNumber <= 9) {
            return coverUrl + directory + "/"
                + file.replace("$NUMBER", "00" + pageNumber);
        } else if (pageNumber >= 99) {
            // This should be impossible but you never know.
            return coverUrl + directory + "/"
                + file.replace("$NUMBER", "0" + pageNumber);
        } else {
            return coverUrl + directory + "/"
                + file.replace("$NUMBER", String.valueOf(pageNumber));
        }
    }
}