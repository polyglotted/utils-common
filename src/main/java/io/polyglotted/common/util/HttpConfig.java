package io.polyglotted.common.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import static io.polyglotted.common.util.InsecureSslFactory.insecureSslContext;
import static org.apache.http.client.config.RequestConfig.custom;

@SuppressWarnings({"unused", "WeakerAccess"}) @Accessors(chain = true)
@NoArgsConstructor @Getter @Setter
public class HttpConfig {
    int connectTimeout = 3000;
    int socketTimeout = 10000;
    boolean insecure = false;
    String scheme = "https";
    String host = "localhost";
    int port = 443;

    public String url() { return scheme + "://" + host + ((port == 80 || port == 443) ? "" : (":" + port)); }

    private HostnameVerifier hostnameVerifier() { return insecure ? new NoopHostnameVerifier() : null; }

    private SSLContext insecureContext() { return insecure ? insecureSslContext(host, port) : null; }

    private RequestConfig requestConfig() { return custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build(); }

    public static CloseableHttpClient httpClient(HttpConfig config) {
        return HttpClientBuilder.create().setDefaultRequestConfig(config.requestConfig())
            .setSSLContext(config.insecureContext()).setSSLHostnameVerifier(config.hostnameVerifier()).build();
    }
}
