package eu.kvfg.refresh.grade;

import lombok.Getter;

/**
 * @author Lukas Nasarek
 */
public class GradeParseException extends RuntimeException {

    @Getter
    private final String gradeLiteral;

    GradeParseException(String gradeLiteral) {
        super("Can't parse grade " + gradeLiteral);
        this.gradeLiteral = gradeLiteral;
    }
}
