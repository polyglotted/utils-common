package io.polyglotted.common.test;

import io.polyglotted.common.util.HttpConfig;
import io.polyglotted.common.util.HttpRequestBuilder;
import io.polyglotted.common.util.HttpUtil;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class HttpUtilTest extends HttpUtil {

    public static Object[][] reqBuilderInputs() throws Exception {
        URI expectedUri = new URI("https://dev-584811.oktapreview.com/oauth2/v1/userinfo");
        return new Object[][]{
            {
                buildGet(expectedUri.toString()).withBasicAuth("a:", "u"),
                buildGet("https://dev-584811.oktapreview.com", "oauth2/v1/userinfo")
            },
            {
                buildPost(expectedUri.toString()).withBearerAuth("aByweruiadfta74adfyu"),
                buildPost("https://dev-584811.oktapreview.com", "oauth2/v1/userinfo")
            },
            {
                buildPut(expectedUri.toString()).withJson("{}"),
                buildPut("https://dev-584811.oktapreview.com", "oauth2/v1/userinfo")
            },
            {
                buildDelete(expectedUri.toString()).withBasicAuth("a:", "u"),
                buildDelete("https://dev-584811.oktapreview.com", "oauth2/v1/userinfo")
            },
        };
    }

    @Test @Parameters(method = "reqBuilderInputs")
    public void buildUriSuccess(HttpRequestBuilder expected, HttpRequestBuilder actual) {
        HttpRequestBase expectedBase = expected.request();
        HttpRequestBase actualBase = actual.request();
        assertThat(actualBase.getMethod(), is(expectedBase.getMethod()));
        assertThat(actualBase.getRequestLine().getUri(), is(expectedBase.getRequestLine().getUri()));
    }

    public static Object[][] httpConfigInputs() {
        return new Object[][]{
            { new HttpConfig(), "https://localhost" },
            { new HttpConfig().setScheme("http"), "http://localhost" },
            { new HttpConfig().setPort(9200), "https://localhost:9200" },
            { new HttpConfig().setScheme("http").setPort(80), "http://localhost" },
            { new HttpConfig().setScheme("http").setPort(9200), "http://localhost:9200" },
        };
    }

    @Test @Parameters(method = "httpConfigInputs")
    public void httpConfigTest(HttpConfig config, String expected) {
        assertThat(config.url(), is(expected));
    }
}