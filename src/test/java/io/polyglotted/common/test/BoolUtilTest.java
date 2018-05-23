package io.polyglotted.common.test;

import io.polyglotted.common.util.BoolUtil;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class BoolUtilTest extends BoolUtil {

    public static Object[][] boolInputs() {
        return new Object[][]{
            {null, false, false, true},
            {"", true, true, true},
            {"true", true, false, true},
            {"false", false, true, false},
        };
    }

    @Test @Parameters(method = "boolInputs")
    public void isTrueSuccess(String input, boolean expTrue, boolean expFalse, boolean expNotFalse) {
        assertThat(isTrue(input), is(expTrue));
        assertThat(isFalse(input), is(expFalse));
        assertThat(isNotFalse(input), is(expNotFalse));
    }
}