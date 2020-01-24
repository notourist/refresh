package eu.kvfg.refresh.grade;

import org.junit.Test;

import static eu.kvfg.refresh.grade.GradeType.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;


public class GradeTypeTest {

    @Test
    public void all() {
        assertEquals(ALL, GradeType.of("BLÄ"));
        assertEquals(ALL, GradeType.of("?"));
    }

    @Test
    public void junior() {
        assertEquals(JUNIOR, GradeType.of("5A"));
        assertEquals(JUNIOR, GradeType.of("6BD"));
        assertEquals(JUNIOR, GradeType.of("7ABC8A"));
        assertEquals(JUNIOR, GradeType.of("7ABC9BDE10BE"));
        assertEquals(JUNIOR, GradeType.of("5A6A7A8A9A10AB"));
    }

    @Test
    public void senior() {
        assertEquals(SENIOR, GradeType.of("JG1"));
        assertEquals(SENIOR, GradeType.of("JG2"));
        assertEquals(SENIOR, GradeType.of("JG3"));
    }

    @Test
    public void cantParse() {
        assertThatExceptionOfType(GradeParseException.class)
            .isThrownBy(() -> GradeType.of("blä"));
        assertThatExceptionOfType(GradeParseException.class)
            .isThrownBy(() -> GradeType.of("5a"));
        assertThatExceptionOfType(GradeParseException.class)
            .isThrownBy(() -> GradeType.of("6BC7DE8abcd9ABC"));
        assertThatExceptionOfType(GradeParseException.class)
            .isThrownBy(() -> GradeType.of("Jg1"));
        assertThatExceptionOfType(GradeParseException.class)
            .isThrownBy(() -> GradeType.of("jG1"));
        assertThatExceptionOfType(GradeParseException.class)
            .isThrownBy(() -> GradeType.of("AWDGJVPGÄÖ"));
        assertThatExceptionOfType(GradeParseException.class)
            .isThrownBy(() -> GradeType.of("10abc 9abc 8abc"));
    }
}
