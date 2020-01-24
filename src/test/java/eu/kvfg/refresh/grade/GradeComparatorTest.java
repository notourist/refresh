package eu.kvfg.refresh.grade;

import org.junit.Test;

import static eu.kvfg.refresh.grade.GradeComparator.contains;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Lukas Nasarek
 */
public class GradeComparatorTest {

    private final Grade student1 = Grade.fromStudent("5a");
    private final Grade student2 = Grade.fromStudent("6b");
    private final Grade student3 = Grade.fromStudent("7d");
    private final Grade student4 = Grade.fromStudent("Jg1");
    private final Grade student5 = Grade.fromStudent("1a");

    private final Grade cover1 = Grade.fromCover("5a");
    private final Grade cover2 = Grade.fromCover("6abc");
    private final Grade cover3 = Grade.fromCover("5a6b7d");
    private final Grade cover4 = Grade.fromCover("5bcd6acd7abc");
    private final Grade cover5 = Grade.fromCover("5a6abc7d");
    private final Grade cover6 = Grade.fromCover("Jg1");
    private final Grade cover7 = Grade.fromCover("?");
    private final Grade cover8 = Grade.fromCover("blä");
    private final Grade cover9 = Grade.fromCover("10c");

    @Test
    public void gradeOrder() {
        assertThatExceptionOfType(GradeComparatorException.class)
            .isThrownBy(() -> contains(cover1, cover1));
        assertTrue(contains(student1, cover1));
        assertTrue(contains(student1, student1));
    }

    @Test
    public void comparisons1() {
        assertTrue(contains(student1, cover1));
        assertFalse(contains(student1, cover2));
        assertTrue(contains(student1, cover3));
        assertFalse(contains(student1, cover4));
        assertTrue(contains(student1, cover5));
        assertFalse(contains(student1, cover6));
        assertTrue(contains(student1, cover7));
        assertTrue(contains(student1, cover8));
        assertFalse(contains(student1, cover9));
    }

    @Test
    public void comparisons2() {
        assertFalse(contains(student2, cover1));
        assertTrue(contains(student2, cover2));
        assertTrue(contains(student2, cover3));
        assertFalse(contains(student2, cover4));
        assertTrue(contains(student2, cover5));
        assertFalse(contains(student2, cover6));
        assertTrue(contains(student2, cover7));
        assertTrue(contains(student2, cover8));
        assertFalse(contains(student2, cover9));
    }

    @Test
    public void comparisons3() {
        assertFalse(contains(student3, cover1));
        assertFalse(contains(student3, cover2));
        assertTrue(contains(student3, cover3));
        assertFalse(contains(student3, cover4));
        assertTrue(contains(student3, cover5));
        assertFalse(contains(student3, cover6));
        assertTrue(contains(student3, cover7));
        assertTrue(contains(student3, cover8));
        assertFalse(contains(student3, cover9));
    }

    @Test
    public void comparisons4() {
        assertFalse(contains(student4, cover1));
        assertFalse(contains(student4, cover2));
        assertFalse(contains(student4, cover3));
        assertFalse(contains(student4, cover4));
        assertFalse(contains(student4, cover5));
        assertTrue(contains(student4, cover7));
        assertTrue(contains(student4, cover8));
        assertFalse(contains(student4, cover9));
    }

    @Test
    public void comparisons5() {
        assertFalse(contains(student5, cover1));
        assertFalse(contains(student5, cover2));
        assertFalse(contains(student5, cover3));
        assertFalse(contains(student5, cover4));
        assertFalse(contains(student5, cover5));
        assertFalse(contains(student5, cover6));
        assertTrue(contains(student5, cover7));
        assertTrue(contains(student5, cover8));
        assertFalse(contains(student5, cover9));
    }
}
