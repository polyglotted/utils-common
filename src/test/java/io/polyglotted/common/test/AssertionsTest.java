package io.polyglotted.common.test;

import io.polyglotted.common.util.Assertions;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class AssertionsTest extends Assertions {

    public static Object[][] betweenInputs() {
        return new Object[][]{
            {0, 0, 10, true, true, true},
            {5, 0, 10, true, true, true},
            {10, 0, 10, true, true, true},
            {0, 0, 10, false, true, false},
            {5, 0, 10, false, true, true},
            {10, 0, 10, false, true, true},
            {0, 0, 10, true, false, true},
            {5, 0, 10, true, false, true},
            {10, 0, 10, true, false, false},
            {0, 0, 10, false, false, false},
            {5, 0, 10, false, false, true},
            {10, 0, 10, false, false, false},
        };
    }

    @Test @Parameters(method = "betweenInputs")
    public void testCheckBetween(int value, int lower, int upper, boolean inclLower, boolean inclUpper, boolean expected) {
        assertThat(checkBetween(value, lower, upper, inclLower, inclUpper), is(expected));
    }
}