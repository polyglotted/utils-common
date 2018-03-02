package io.polyglotted.common.test;

import io.polyglotted.common.util.RegexUtil;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class RegexUtilTest extends RegexUtil {

    public static Object[][] geoJsonInputs() {
        return new Object[][]{
            {"", false},
            {"[\"x\", \"y\"]", false},
            {"[-77.03653, 38.897676]", true},
            {"[[-77.03653, 38.897676], [-77.009051, 38.889939]]", true},
            {"[[ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ]]", true},
            {"[[ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ],[ [100.2, 0.2], " +
                "[100.8, 0.2], [100.8, 0.8], [100.2, 0.8], [100.2, 0.2] ]]", true},
            {"[[ [[102.0, 2.0], [103.0, 2.0], [103.0, 3.0], [102.0, 3.0], [102.0, 2.0]] ],[ " +
                "[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]],[[100.2, 0.2], [100.8, 0.2], " +
                "[100.8, 0.8], [100.2, 0.8], [100.2, 0.2]] ]]", true},
        };
    }

    @Test @Parameters(method = "geoJsonInputs")
    public void isValidGeoJsonSuccess(String json, boolean expected) throws Exception {
        assertThat(isValidGeoJson(json), is(expected));
    }

    public static Object[][] uuidInputs() {
        return new Object[][]{
            {"", false},
            {UUID.randomUUID().toString(), true},
        };
    }

    @Test @Parameters(method = "uuidInputs")
    public void isValidUuidSuccess(String uuid, boolean expected) throws Exception {
        assertThat(uuid, isValidUuid(uuid), is(expected));
    }

    public static Object[][] emailInputs() {
        return new Object[][]{
            {"", false},
            {"@.", false},
            {"x@x.x", true},
            {"x@x.xi", true},
            {"info=centerpeace.org@mail44.suw15.mcsv.net", true},
            {"Beahan_Tianna@Kreiger.me", true},
        };
    }

    @Test @Parameters(method = "emailInputs")
    public void isValidEmailSuccess(String email, boolean expected) throws Exception {
        assertThat(email, isValidEmail(email), is(expected));
    }

    public static Object[][] ipInputs() {
        return new Object[][]{
            {"", false},
            {"....", false},
            {"1.1", false},
            {"1.2.3", false},
            {"256.1.1.1", false},
            {"0.0.0.0", true},
            {"99.199.99.95", true},
            {"255.255.255.255", true},
        };
    }

    @Test @Parameters(method = "ipInputs")
    public void isValidIpSuccess(String ip, boolean expected) throws Exception {
        assertThat(ip, isValidIp(ip), is(expected));
    }

    public static Object[][] urlInputs() {
        return new Object[][]{
            {"", false},
            {"www.google.com", false},
            {"http://foo.bar.co", true},
            {"s3://mybuck.s3.aws.amazon.com/put", true},
            {"https://foo.bar.com?ab=cd&ge", true},
            {"http://12.34.56.8:8080/foldit", true},
        };
    }

    @Test @Parameters(method = "urlInputs")
    public void isValidUrlSuccess(String url, boolean expected) throws Exception {
        assertThat(url, isValidUrl(url), is(expected));
    }
}