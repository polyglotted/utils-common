package io.polyglotted.common.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.net.URL;
import java.security.KeyStore;

import static io.polyglotted.common.util.InsecureSslFactory.insecureSslContext;
import static io.polyglotted.common.util.StrUtil.notNullOrEmpty;
import static org.apache.http.client.config.RequestConfig.custom;

@SuppressWarnings({"unused", "WeakerAccess"}) @Accessors(chain = true)
@NoArgsConstructor @Getter @Setter
public class HttpConfig {
    int connectTimeout = 3000;
    int socketTimeout = 10000;
    String trustStore = null;
    boolean insecure = false;
    String scheme = "https";
    String host = "localhost";
    int port = 443;

    public String url() { return scheme + "://" + host + ((port == 80 || port == 443) ? "" : (":" + port)); }

    public static CloseableHttpClient httpClient(HttpConfig config) {
        return HttpClientBuilder.create().setDefaultRequestConfig(config.requestConfig())
            .setSSLContext(config.determineContext()).setSSLHostnameVerifier(config.hostnameVerifier()).build();
    }

    private HostnameVerifier hostnameVerifier() { return notNullOrEmpty(trustStore) || insecure ? new NoopHostnameVerifier() : null; }

    private SSLContext determineContext() {
        return notNullOrEmpty(trustStore) ? internalContext(trustStore) : (insecure ? insecureSslContext(host, port) : null);
    }

    private RequestConfig requestConfig() { return custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build(); }

    @SneakyThrows private static SSLContext internalContext(String trustStore) {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new URL(trustStore).openStream(), new char[0]);
        return SSLContexts.custom().loadTrustMaterial(keyStore, new TrustSelfSignedStrategy()).build();
    }
}