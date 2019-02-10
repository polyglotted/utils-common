package io.polyglotted.common.web;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.polyglotted.common.model.Pair;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PathRouter<T> {
    static final Pattern GROUP_PATTERN = Pattern.compile("\\{(.*?)\\}");
    private static final Pattern WILD_CARD_PATTERN = Pattern.compile("\\*\\*");

    private final int maxPathParts;
    private final List<Pair<Pattern, RouteDestinationWithGroups>> patternRouteList;

    public static <T> PathRouter<T> create(int maxPathParts) { return new PathRouter<>(maxPathParts); }

    private PathRouter(int maxPathParts) {
        this.maxPathParts = maxPathParts;
        this.patternRouteList = Lists.newArrayList();
    }

    public void add(final String source, final T destination) {
        // replace multiple slashes with a single slash.
        String path = source.replaceAll("/+", "/");
        path = (path.endsWith("/") && path.length() > 1) ? path.substring(0, path.length() - 1) : path;

        String[] parts = path.split("/");
        if (parts.length - 1 > maxPathParts) {
            throw new IllegalArgumentException("Number of parts of path " + source + " exceeds allowed limit " + maxPathParts);
        }
        StringBuilder sb = new StringBuilder();
        List<String> groupNames = Lists.newArrayList();
        for (String part : parts) {
            Matcher groupMatcher = GROUP_PATTERN.matcher(part);
            if (groupMatcher.matches()) {
                groupNames.add(groupMatcher.group(1));
                sb.append("([^/]+?)");
            }
            else if (WILD_CARD_PATTERN.matcher(part).matches()) { sb.append(".*?"); }
            else {
                sb.append(part);
            }
            sb.append("/");
        }
        //Ignore the last "/"
        sb.setLength(sb.length() - 1);

        Pattern pattern = Pattern.compile(sb.toString());
        patternRouteList.add(Pair.pair(pattern, new RouteDestinationWithGroups(destination, groupNames)));
    }

    public List<RoutableDestination<T>> getDestinations(String path) {
        String cleanPath = (path.endsWith("/") && path.length() > 1) ? path.substring(0, path.length() - 1) : path;

        List<RoutableDestination<T>> result = Lists.newArrayList();
        for (Pair<Pattern, RouteDestinationWithGroups> patternRoute : patternRouteList) {
            ImmutableMap.Builder<String, String> groupNameValuesBuilder = ImmutableMap.builder();
            Matcher matcher = patternRoute._a.matcher(cleanPath);

            if (matcher.matches()) {
                int matchIndex = 1;
                for (String name : patternRoute._b.groupNames) {
                    String value = matcher.group(matchIndex);
                    groupNameValuesBuilder.put(name, value);
                    matchIndex++;
                }
                result.add(new RoutableDestination<>(patternRoute._b.destination, groupNameValuesBuilder.build()));
            }
        }
        return result;
    }

    @ToString
    @RequiredArgsConstructor
    private final class RouteDestinationWithGroups {
        final T destination;
        final List<String> groupNames;
    }

    @RequiredArgsConstructor
    public static final class RoutableDestination<T> {
        public final T destination;
        public final Map<String, String> groupNameValues;
    }
}