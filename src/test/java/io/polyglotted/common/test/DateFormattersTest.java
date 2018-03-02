package io.polyglotted.common.test;

import io.polyglotted.common.util.DateFormatters;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class DateFormattersTest extends DateFormatters {

    public static Object[][] dateTimeInputs() {
        return new Object[][]{
            {"2011-12-03T10:15:30+01:00:00"},
            {"2011-12-03T10:15:30-08:30"},
            {"2011-12-03T10:15:30.987654321Z"},
            {"2011-12-03T10:15:30.987654321"},
            {"2011-12-03T10:15:30.987654Z"},
            {"2011-12-03T10:15:30.987654"},
            {"2011-12-03T10:15:30.987Z"},
            {"2011-12-03T10:15:30.987"},
            {"2011-12-03T10:15:30Z"},
            {"2011-12-03T10:15:30"},
            {"20111203T101530.123"},
            {"20111203 101530.123"},
            {"20111203T101530"},
            {"20111203 101530"},
            {"2016-11-04 06:00:00.987654321"},
            {"2011/12/03 10:15:30.987654321"},
            {"12/03/2011 10:15:30.987654321"},
            {"03-12-2011 10:15:30.987654321"},
            {"03 12 2011 10:15:30.987654321"},
            {"03 Dec 2011 10:15:30.987654321"},
            {"03 December 2011 10:15:30.987654321"},
            {"2016-11-04 06:00:00.345678"},
            {"2011/12/03 10:15:30.345678"},
            {"12/03/2011 10:15:30.345678"},
            {"03-12-2011 10:15:30.345678"},
            {"03 12 2011 10:15:30.345678"},
            {"03 Dec 2011 10:15:30.345678"},
            {"03 December 2011 10:15:30.345678"},
            {"2016-11-04 06:00:00.123"},
            {"2011/12/03 10:15:30.123"},
            {"12/03/2011 10:15:30.123"},
            {"03-12-2011 10:15:30.123"},
            {"03 12 2011 10:15:30.123"},
            {"03 Dec 2011 10:15:30.123"},
            {"03 December 2011 10:15:30.123"},
            {"2011-12-03 10:15:30"},
            {"2011/12/03 10:15:30"},
            {"12/03/2011 10:15:30"},
            {"03-12-2011 10:15:30"},
            {"03 12 2011 10:15:30"},
            {"03 Dec 2011 10:15:30"},
            {"03 December 2011 10:15:30"},
            {"20111203T1015"},
            {"20111203 1015"},
            {"2011-12-03 10:15"},
            {"2011/12/03 10:15"},
            {"12/03/2011 10:15"},
            {"03-12-2011 10:15"},
            {"03 12 2011 10:15"},
            {"03 Dec 2011 10:15"},
            {"03 December 2011 10:15"},
            {"2011-12-03"},
            {"2011/12/03"},
            {"12/03/2011"},
            {"03-12-2011"},
            {"03 12 2011"},
            {"03 Dec 2011"},
            {"03 December 2011"},
            {"1479832319000"},
        };
    }

    @Test @Parameters(method = "dateTimeInputs")
    public void testMultiFormatDateTime(String dateTime) {
        assertNotNull(parseDate(dateTime));
        assertThat(maybeParseDateTime(dateTime), is(String.valueOf(parseDateTime(dateTime).toInstant().toEpochMilli())));
    }
}