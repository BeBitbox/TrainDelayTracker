package be.bitbox.traindelay.tracker.core;

import org.junit.Test;

import static be.bitbox.traindelay.tracker.core.ValidationUtils.isNumeric;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ValidationUtilsTest {

    @Test
    public void isNumericValidationTest() throws Exception {
        assertThat(isNumeric(""), is(false));
        assertThat(isNumeric("a"), is(false));
        assertThat(isNumeric("78f"), is(false));
        assertThat(isNumeric("0"), is(true));
        assertThat(isNumeric("784"), is(true));
        assertThat(isNumeric("4.4646"), is(true));
    }

}