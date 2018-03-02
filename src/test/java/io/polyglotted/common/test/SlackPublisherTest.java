package io.polyglotted.common.test;

import com.google.common.collect.ImmutableMap;
import io.polyglotted.common.util.SlackPublisher;
import io.polyglotted.common.util.SlackPublisher.SlackConfig;
import io.polyglotted.common.util.ThreadUtil;
import org.junit.Test;

import static com.google.common.collect.ImmutableList.of;
import static io.polyglotted.common.util.MapBuilder.immutableMap;

public class SlackPublisherTest {
    @Test
    public void publish() throws Exception {
        SlackConfig slackConfig = new SlackConfig().setEnabled(true).setHookUrl(System.getProperty("slack.hook.url"));
        try (SlackPublisher publisher = new SlackPublisher(slackConfig)) {
            publisher.publish("test", ImmutableMap.<String, Object>builder()
                .put("fallback", "test success")
                .put("pretext", "test pretext")
                .put("color", "#08C309")
                .put("author_name", "svasudevan")
                .put("title", "Task Summary")
                .put("text", "")
                .put("fields", of(immutableMap("title", "Foo", "value", "/foo/feeds/do/everything/in/url", "short", false),
                    immutableMap("title", "Bar", "value", "bar", "short", true), immutableMap("title", "Baz", "value", "baz", "short", true)))
                .put("footer", "SteelEye Trade Sink")
                .put("footer_icon", "https://pinafore.steeleye.co/asset/favicon/c866deefad2715b7942ce6324702af6f/favicon-16x16.png")
                .put("icon", ":crying_cat_face:")
                .build()
            );
            ThreadUtil.safeSleep(100);
        }
    }
}