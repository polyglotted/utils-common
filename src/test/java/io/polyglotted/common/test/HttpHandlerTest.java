package io.polyglotted.common.test;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import static io.polyglotted.common.util.ResourceUtil.readResourceAsMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(JUnitParamsRunner.class)
public class HttpHandlerTest {
    private static final Map<String, String> MESSAGES = readResourceAsMap(HttpHandlerTest.class, "files/httpex.txt");

    public static Object[][] httpInputs() {
        return new Object[][]{
            {"simple-get-agw-i", "simple-get-agw-o"},
            {"simple-get-alb-i", "simple-get-alb-o"},
        };
    }

    @Test @Parameters(method = "httpInputs")
    public void doHttpHandle(String input, String output) throws Exception {
        HttpDemoHandler handler = new HttpDemoHandler();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        handler.handle(new ByteArrayInputStream(MESSAGES.get(input).getBytes()), outputStream);

        String actual = new String(outputStream.toByteArray());
        assertThat(actual, actual, is(MESSAGES.get(output)));
    }
}