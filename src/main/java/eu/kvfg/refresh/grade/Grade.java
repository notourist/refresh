package eu.kvfg.refresh.grade;

import lombok.Getter;

/**
 * @author Lukas Nasarek
 */
public class Grade {

    @Getter
    private final String literal;

    @Getter
    private final GradeType gradeType;

    @Getter
    private final boolean coverParsed;

    public static Grade fromCover(String literal) {
        return new Grade(literal.toUpperCase(), true);
    }

    public static Grade fromStudent(String literal) {
        return new Grade(literal.toUpperCase(), false);
    }

    private Grade(String literal, boolean coverParsed) {
        this(literal, GradeType.of(literal), coverParsed);
    }

    private Grade(String literal, GradeType gradeType, boolean coverParsed) {
        this.literal = literal;
        this.gradeType = gradeType;
        this.coverParsed = coverParsed;
    }
}
