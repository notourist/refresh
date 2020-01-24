package eu.kvfg.refresh.grade;

import lombok.Getter;

/**
 * The specific grade type. Used to determine how to compare
 * grades with each other by {@link GradeComparator}.
 *
 * @author Lukas Nasarek
 */
public enum GradeType {

    ALL("[?]|BLÄ"),
    JUNIOR("([\\d]+[\\p{javaUpperCase}]+)+"),
    SENIOR("JG[\\d]");

    @Getter
    private final String pattern;

    GradeType(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Determines the type of a grade via regex.
     *
     * @param literal the grade literal with all chars upper case
     * @return the type of the grade
     */
    public static GradeType of(String literal) {
        for (GradeType gradeType : GradeType.values()) {
            if (literal.matches(gradeType.getPattern())) {
                return gradeType;
            }
        }
        throw new GradeParseException(literal);
    }
}