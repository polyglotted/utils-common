package io.polyglotted.common.test;

import io.polyglotted.common.util.HttpRequestBuilder;
import io.polyglotted.common.util.HttpUtil;
import org.junit.Test;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class HttpUtilTest extends HttpUtil {
    @Test
    public void buildUriSuccess() throws Exception {
        URI expectedUri = new URI("https://dev-584811.oktapreview.com/oauth2/v1/userinfo");
        HttpRequestBuilder expected = buildGet(expectedUri.toString(), null, "Basic a=u");
        HttpRequestBuilder actual = buildGet("https://dev-584811.oktapreview.com", "oauth2/v1/userinfo", "Basic a=u");
        assertThat(actual.buildUri(), is(expected.buildUri()));
    }
}