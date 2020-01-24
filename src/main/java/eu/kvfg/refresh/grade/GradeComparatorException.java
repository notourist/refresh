package eu.kvfg.refresh.grade;

import lombok.Getter;

/**
 * Indicates that the first grade used with
 * {@link GradeComparator#contains(Grade, Grade)}
 * was parsed from the cover plan.
 *
 * @author Lukas Nasarek
 */
public class GradeComparatorException extends RuntimeException {

    @Getter
    private final Grade exceptionGrade;

    @Getter
    private final Grade secondGrade;

    GradeComparatorException(Grade exceptionGrade, Grade secondGrade) {
        super("First grade was cover parsed.");
        this.exceptionGrade = exceptionGrade;
        this.secondGrade = secondGrade;
    }
}
