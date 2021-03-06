package io.polyglotted.common.test;

import io.polyglotted.common.util.SlackPublisher;
import io.polyglotted.common.util.SlackPublisher.SlackConfig;
import io.polyglotted.common.util.ThreadUtil;
import org.junit.Test;

import static io.polyglotted.common.util.ListBuilder.immutableList;
import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static io.polyglotted.common.util.MapBuilder.immutableMapBuilder;
import static io.polyglotted.common.util.NullUtil.sysVar;

public class SlackPublisherTest {
    @Test
    public void publish() throws Exception {
        SlackConfig slackConfig = new SlackConfig().setEnabled(true).setChannel("releases").setHookUrl(sysVar("slack.hook.url"));
        try (SlackPublisher publisher = new SlackPublisher(slackConfig)) {
            publisher.publish("test", immutableMapBuilder()
                .put("fallback", "test success")
                .put("pretext", "test pretext")
                .put("color", "#08C309")
                .put("author_name", "Shankar Vasudevan")
                .put("title", "Task Summary")
                .put("text", "")
                .put("fields", immutableList(immutableMap("title", "Foo", "value", "/foo/feeds/do/everything/in/url", "short", false),
                    immutableMap("title", "Bar", "value", "bar", "short", true), immutableMap("title", "Baz", "value", "baz", "short", true)))
                .put("footer", "SteelEye Trade Sink")
                .put("footer_icon", "https://pinafore.steeleye.co/asset/favicon/c866deefad2715b7942ce6324702af6f/favicon-16x16.png")
                .put("icon", ":crying_cat_face:")
                .result()
            );
            ThreadUtil.safeSleep(100);
        }
    }
}