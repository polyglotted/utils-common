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

@SuppressWarnings("WeakerAccess")
public abstract class HttpClientFactory {

    public static CloseableHttpClient httpClient(HttpConfig config) {
        return HttpClientBuilder.create().setDefaultRequestConfig(config.requestConfig())
            .setSSLContext(config.insecureContext()).setSSLHostnameVerifier(config.hostnameVerifier()).build();
    }

    @NoArgsConstructor @Getter @Setter @Accessors(chain = true)
    public static class HttpConfig {
        int connectTimeout = 3000;
        int socketTimeout = 10000;
        boolean disableHostVerification = false;
        boolean insecure = false;
        String sslHost = null;
        int sslPort = 443;

        HostnameVerifier hostnameVerifier() { return disableHostVerification ? new NoopHostnameVerifier() : null; }

        SSLContext insecureContext() { return insecure ? insecureSslContext(sslHost, sslPort) : null; }

        RequestConfig requestConfig() { return RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build(); }
    }
}