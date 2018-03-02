package io.polyglotted.common.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import static io.polyglotted.common.util.BaseSerializer.serialize;
import static java.util.Collections.singletonList;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

@Slf4j
public class SlackPublisher implements AutoCloseable {
    private final CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom()
        .setConnectTimeout(3000).setSocketTimeout(3000).build()).build();
    private final SlackConfig slackConfig;
    private final Pattern pattern;

    public SlackPublisher(SlackConfig slackConfig) {
        this.slackConfig = slackConfig; this.pattern = Pattern.compile(slackConfig.filter);
    }

    @Override public void close() throws IOException { client.close(); }

    public void publish(String route, Map<String, Object> attachment) {
        if (!slackConfig.enabled || !pattern.matcher(route).matches()) { return; }
        HttpPost post = new HttpPost(slackConfig.hookUrl);
        post.setEntity(new StringEntity(serialize(MapBuilder.immutableMap("channel", slackConfig.channel, "username", attachment.get("author_name"),
            "attachments", singletonList(attachment), "icon_emoji", attachment.get("icon"))), APPLICATION_JSON));
        HttpUtil.executePlain(client, post);
    }

    @NoArgsConstructor @Getter @Setter @Accessors(chain = true)
    public static class SlackConfig {
        private boolean enabled = false;
        private String hookUrl = "";
        private String channel = "sns-notifications";
        private String filter = ".*";
    }
}