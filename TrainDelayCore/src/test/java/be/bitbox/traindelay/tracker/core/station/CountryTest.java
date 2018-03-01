package be.bitbox.traindelay.tracker.core.station;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CountryTest {

    @Test
    public void translateFromBelgium() {
        Country country = Country.translateFrom("src/main/resources/be");
        assertThat(country, is(Country.BE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void translateFromUnknown() {
        Country.translateFrom("Fake");
    }
}