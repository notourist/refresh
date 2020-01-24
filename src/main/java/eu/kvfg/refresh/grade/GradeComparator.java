package eu.kvfg.refresh.grade;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 *
 * @author Lukas Nasarek
 */
public final class GradeComparator {

    /**
     * Checks if the {@code studentGrade} which must be <b>not</b>
     * parsed from the cover plan is contained inside the {@code coverGrade}.
     *
     * @param studentGrade the grade that is associated with a student
     * @param coverGrade the grade that was parsed from the cover plan
     * @return whether the {@code cover} contains the {@code student}.
     */
    public static boolean contains(Grade studentGrade, Grade coverGrade) {
        if (studentGrade.isCoverParsed()) {
            throw new GradeComparatorException(studentGrade, coverGrade);
        }

        if (coverGrade.getGradeType() == GradeType.ALL) {
            return true;
        } else if (studentGrade.getGradeType() != coverGrade.getGradeType()) {
            return false;
        }

        switch (studentGrade.getGradeType()) {

            case JUNIOR:
                return testJunior(studentGrade, coverGrade);
            case SENIOR:
                return studentGrade.getGradeType().equals(GradeType.SENIOR)
                    && studentGrade.getLiteral().equals(coverGrade.getLiteral());
        }
        return false;
    }

    private static boolean testJunior(Grade junior, Grade cover) {
        if (junior.getLiteral().equals(cover.getLiteral())) {
            return true;
        }

        String juniorNumber = parseNumberParts(junior.getLiteral())[0];
        String[] coverNumbers = parseNumberParts(cover.getLiteral());

        int position = -1;
        // Check if the junior grade number is contained inside the cover grade.
        for (int i = 0; i < coverNumbers.length; i++) {
            if (juniorNumber.equals(coverNumbers[i])) {
                position = i;
                break;
            }
        }

        // Grade does not contain junior grade number.
        if (position == -1) {
            return false;
        }

        // If now the char parts parsed from the cover grade (example: "acde")
        // contain the junior char ("a"), the whole junior grade is
        // contained inside the cover grade.
        String juniorChar = parseCharParts(junior.getLiteral())[0];
        String coverChar  = parseCharParts(cover.getLiteral())[position];
        return coverChar.contains(juniorChar);

    }

    /**
     * Parses a grade literal into its number parts.
     * So {@code 5abc6cde7de} turns into {@code [5, 6, 7]}.
     *
     *
     * @param literal the grade literal that should be parsed
     * @return all the number parts inside the grade literal
     */
    private static String[] parseNumberParts(String literal) {
        Pattern pattern = Pattern.compile("[\\d]{1,2}[\\D]+");
        Matcher matcher = pattern.matcher(literal);
        String[] numberParts = new String[6];

        int i = 0;
        while (matcher.find()) {
            String part = matcher.group();

            // Part starts with 1, so the grade is 10.
            if (part.startsWith("10")) {
                numberParts[i] = "10";
            } else {
                numberParts[i] = part.substring(0, 1);
            }
            i++;
        }
        return numberParts;
    }

    /**
     * Parses a grade literal into its alphabetical parts.
     * So {@code 5abc6cde7de} turns into {@code [abc, cde, de]}.
     *
     *
     * @param literal the grade literal that should be parsed
     * @return all the number parts inside the grade literal
     */
    private static String[] parseCharParts(String literal) {
        Pattern pattern = Pattern.compile("[\\d]{1,2}[\\D]+");
        Matcher matcher = pattern.matcher(literal);
        String[] alphabeticalParts = new String[6];

        int i = 0;
        while (matcher.find()) {
            String part = matcher.group();

            if (part.startsWith("10")) {
                alphabeticalParts[i] = part.substring(2);
            } else {
                alphabeticalParts[i] = part.substring(1);
            }
            i++;
        }
        return alphabeticalParts;
    }
}
