package eu.kvfg.refresh.cover;

import com.fasterxml.jackson.annotation.JsonGetter;
import eu.kvfg.refresh.grade.Grade;
import lombok.Getter;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * POJO for a column of the cover plan.
 *
 * @author Lukas Nasarek
 */
class Cover {

    @Getter
    private final LocalDate date;

    @Getter
    private final Grade grade;

    @Getter
    private final String lesson;

    @Getter
    private final boolean doubleLesson;

    @Getter
    private final String teacher;

    @Getter
    private final String subject;

    @Getter
    private final String room;

    @Getter
    private final String newTeacher;

    @Getter
    private final String newSubject;

    @Getter
    private final String newRoom;

    @Getter
    private final String message;

    @Getter
    private final boolean canceled;

    @Getter
    private final String replaces;

    Cover(LocalDate date, Grade grade, String lesson, boolean doubleLesson,
          String teacher, String subject, String room, String newTeacher,
          String newSubject, String newRoom, String message, boolean canceled, String replaces) {
        this.date = date;
        this.grade = grade;
        this.lesson = lesson;
        this.doubleLesson = doubleLesson;
        this.teacher = teacher;
        this.subject = subject;
        this.room = room;
        this.newTeacher = newTeacher;
        this.newSubject = newSubject;
        this.newRoom = newRoom;
        this.message = message;
        this.canceled = canceled;
        this.replaces = replaces;
    }

    @SuppressWarnings("unused")
    @JsonGetter("grade")
    public String getJsonGrade() {
        return grade.getLiteral().toUpperCase();
    }

    List<String> getTeachers() {
        List<String> teachers = new ArrayList<>();
        if (!teacher.isEmpty()) {
            teachers.add(teacher);
        }
        if (!newTeacher.isEmpty()) {
            teachers.add(newTeacher);
        }
        return teachers;
    }

    List<String> getGrades() {
        switch (grade.getGradeType()) {

            case ALL:
            case SENIOR:
                return Collections.singletonList(grade.getLiteral());
            case JUNIOR:
                List<String> containedGrades = new ArrayList<>();

                String literal = grade.getLiteral();

                Pattern pattern = Pattern.compile("[\\d]{1,2}[\\p{javaUpperCase}]+");
                Matcher matcher = pattern.matcher(literal);

                int find = 0;
                while (matcher.find()) {
                    String numberPart = literal.replaceAll("[\\D]", "");
                    String alphabeticalPart = literal.replaceAll("[\\d]", "");

                    for (int i = 0; i < alphabeticalPart.length(); i++) {
                        String singleGrade = String.valueOf(numberPart.charAt(find)) + alphabeticalPart.charAt(i);
                        //Grade is 10a but shows up as 1a
                        if (singleGrade.startsWith("1")) {
                            singleGrade = "10" + singleGrade.charAt(1);
                        }
                        containedGrades.add(singleGrade);
                    }
                    find++;
                }
                return containedGrades;
        }
        throw new RuntimeException("This should be unreachable");
    }

    Cover withoutTeacherNames() {
        return new Cover(date, grade, lesson, doubleLesson,
            "", subject, room, "",
            newSubject, newRoom, message, canceled, replaces);
    }

    private boolean hasChanges() {
        return !room.equals(newRoom)
            || !teacher.equals(newTeacher)
            || !subject.equals(newSubject)
            || !"".equals(replaces)
            || !"".equals(message)
            || canceled;
    }

    /**
     * Provides an easy way of creating a {@link Stream<Cover>} from the cover html.
     */
    static class Streamer {

        private final LocalDate date;

        Streamer(LocalDate date) {
            this.date = date;
        }

        Stream<Cover> fromHtml(Elements column) {
            String grade = column.get(0).text().toUpperCase();
            Cover cover = new Cover(date,
                Grade.fromCover(grade),
                column.get(1).text(),
                column.get(1).text().contains("-"),
                column.get(2).text(),
                column.get(3).text(),
                column.get(4).text(),
                column.get(5).text().replace("---", ""),
                column.get(6).text().replace("---", ""),
                column.get(7).text().replace("---", ""),
                column.get(8).text(),
                column.get(9).text().contains("x"),
                column.get(10).text());

            if (cover.hasChanges()) {
                return Stream.of(cover);
            }

            return Stream.empty();
        }
    }
}
