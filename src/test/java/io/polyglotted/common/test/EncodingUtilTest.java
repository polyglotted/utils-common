package io.polyglotted.common.test;

import io.polyglotted.common.model.Pair;
import io.polyglotted.common.util.EncodingUtil;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.polyglotted.common.model.Pair.pair;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class EncodingUtilTest extends EncodingUtil {

    @Test
    public void testEncodeDecode() throws Exception { assertThat(decodeBase64(encodeBase64("foo".getBytes())), is("foo".getBytes())); }

    @SneakyThrows public static Object[][] urlEncodeInputs() {
        return new Object[][]{
            {urlEncode("foo-bar-baz"), "foo-bar-baz"},
            {urlEncode("steve@gmail.com"), "steve@gmail.com"},
            {urlEncode("steve+test/5@gmail.com"), "steve+test/5@gmail.com"},
            {urlEncode("foo±§!@#$%^&*()_+-={}[];'\\:|,./<>?\""), "foo±§!@#$%^&*()_+-={}[];'\\:|,./<>?\""},
        };
    }

    @Test @Parameters(method = "urlEncodeInputs")
    public void encodeDecodeUrl(String input, String expected) throws Exception { assertThat(urlDecode(input), is(expected)); }

    @Test @Parameters(method = "urlEncodeInputs")
    public void testDecodeUrl(String input, String expected) throws Exception { assertThat(uriEncode(input, ""), is(expected)); }

    @SneakyThrows public static Object[][] uriSuffixInputs() {
        return new Object[][]{
            {"http://foo.se.com/api/v1/records/mymodel/steve+test/5@gmail.com", "/records/", pair("mymodel", "steve+test/5@gmail.com")},
            {"http://foo.se.com/api/v1/records/find-by-id/mymodel/steve+test/5@gmail.com", "/find-by-id/", pair("mymodel", "steve+test/5@gmail.com")},
        };
    }

    @Test @Parameters(method = "uriSuffixInputs")
    public void testDecodeUriSuffix(String input, String path, Pair<String, String> pair) { assertThat(uriSuffixPair(input, path), is(pair)); }
}