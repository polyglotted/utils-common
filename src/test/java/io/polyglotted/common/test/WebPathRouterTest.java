package io.polyglotted.common.test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.polyglotted.common.web.WebPathRouter;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WebPathRouterTest {

    @Test
    public void testPathRoutings() {
        WebPathRouter<String> webPathRouter = WebPathRouter.create(25);

        webPathRouter.add("/foo/{baz}/b", "foobarb");
        webPathRouter.add("/foo/bar/baz", "foobarbaz");
        webPathRouter.add("/baz/bar", "bazbar");
        webPathRouter.add("/bar", "bar");
        webPathRouter.add("/foo/bar", "foobar");
        webPathRouter.add("//multiple/slash//route", "multipleslashroute");

        webPathRouter.add("/abc/bar", "abc-bar");
        webPathRouter.add("/abc/{type}/{id}", "abc-type-id");

        webPathRouter.add("/multi/match/**", "multi-match-*");
        webPathRouter.add("/multi/match/def", "multi-match-def");

        webPathRouter.add("/multi/maxmatch/**", "multi-max-match-*");
        webPathRouter.add("/multi/maxmatch/{id}", "multi-max-match-id");
        webPathRouter.add("/multi/maxmatch/foo", "multi-max-match-foo");

        webPathRouter.add("**/wildcard/{id}", "wildcard-id");
        webPathRouter.add("/**/wildcard/{id}", "slash-wildcard-id");

        webPathRouter.add("**/wildcard/**/foo/{id}", "wildcard-foo-id");
        webPathRouter.add("/**/wildcard/**/foo/{id}", "slash-wildcard-foo-id");

        webPathRouter.add("**/wildcard/**/foo/{id}/**", "wildcard-foo-id-2");
        webPathRouter.add("/**/wildcard/**/foo/{id}/**", "slash-wildcard-foo-id-2");

        List<WebPathRouter.RoutableDestination<String>> routes;

        routes = webPathRouter.getDestinations("/foo/bar/baz");
        assertEquals(1, routes.size());
        assertEquals("foobarbaz", routes.get(0).destination);
        assertTrue(routes.get(0).groupNameValues.isEmpty());

        routes = webPathRouter.getDestinations("/baz/bar");
        assertEquals(1, routes.size());
        assertEquals("bazbar", routes.get(0).destination);
        assertTrue(routes.get(0).groupNameValues.isEmpty());

        routes = webPathRouter.getDestinations("/foo/bar/baz/moo");
        assertTrue(routes.isEmpty());

        routes = webPathRouter.getDestinations("/bar/121");
        assertTrue(routes.isEmpty());

        routes = webPathRouter.getDestinations("/foo/bar/b");
        assertEquals(1, routes.size());
        assertEquals("foobarb", routes.get(0).destination);
        assertEquals(1, routes.get(0).groupNameValues.size());
        assertEquals("bar", routes.get(0).groupNameValues.get("baz"));

        routes = webPathRouter.getDestinations("/foo/bar");
        assertEquals(1, routes.size());
        assertEquals("foobar", routes.get(0).destination);
        assertTrue(routes.get(0).groupNameValues.isEmpty());

        routes = webPathRouter.getDestinations("/abc/bar/id");
        assertEquals(1, routes.size());
        assertEquals("abc-type-id", routes.get(0).destination);

        routes = webPathRouter.getDestinations("/multiple/slash/route");
        assertEquals(1, routes.size());
        assertEquals("multipleslashroute", routes.get(0).destination);
        assertTrue(routes.get(0).groupNameValues.isEmpty());

        routes = webPathRouter.getDestinations("/foo/bar/bazooka");
        assertTrue(routes.isEmpty());

        routes = webPathRouter.getDestinations("/multi/match/def");
        assertEquals(2, routes.size());
        assertEquals(ImmutableSet.of("multi-match-def", "multi-match-*"),
            ImmutableSet.of(routes.get(0).destination, routes.get(1).destination));
        assertTrue(routes.get(0).groupNameValues.isEmpty());
        assertTrue(routes.get(1).groupNameValues.isEmpty());

        routes = webPathRouter.getDestinations("/multi/match/ghi");
        assertEquals(1, routes.size());
        assertEquals("multi-match-*", routes.get(0).destination);
        assertTrue(routes.get(0).groupNameValues.isEmpty());

        routes = webPathRouter.getDestinations("/multi/maxmatch/id1");
        assertEquals(2, routes.size());
        assertEquals(ImmutableSet.of("multi-max-match-id", "multi-max-match-*"),
            ImmutableSet.of(routes.get(0).destination, routes.get(1).destination));
        //noinspection assertEqualsBetweenInconvertibleTypes
        assertEquals(ImmutableSet.of(ImmutableMap.of("id", "id1"), ImmutableMap.<String, String>of()),
            ImmutableSet.of(routes.get(0).groupNameValues, routes.get(1).groupNameValues)
        );

        routes = webPathRouter.getDestinations("/multi/maxmatch/foo");
        assertEquals(3, routes.size());
        assertEquals(ImmutableSet.of("multi-max-match-id", "multi-max-match-*", "multi-max-match-foo"),
            ImmutableSet.of(routes.get(0).destination, routes.get(1).destination,
                routes.get(2).destination));
        //noinspection assertEqualsBetweenInconvertibleTypes
        assertEquals(ImmutableSet.of(ImmutableMap.of("id", "foo"), ImmutableMap.<String, String>of()),
            ImmutableSet.of(routes.get(0).groupNameValues, routes.get(1).groupNameValues)
        );

        routes = webPathRouter.getDestinations("/foo/bar/wildcard/id1");
        assertEquals(2, routes.size());
        assertEquals(ImmutableSet.of("wildcard-id", "slash-wildcard-id"),
            ImmutableSet.of(routes.get(0).destination, routes.get(1).destination));
        //noinspection assertEqualsBetweenInconvertibleTypes
        assertEquals(ImmutableSet.of(ImmutableMap.of("id", "id1"), ImmutableMap.of("id", "id1")),
            ImmutableSet.of(routes.get(0).groupNameValues, routes.get(1).groupNameValues)
        );

        routes = webPathRouter.getDestinations("/wildcard/id1");
        assertEquals(1, routes.size());
        assertEquals("wildcard-id", routes.get(0).destination);
        assertEquals(ImmutableMap.of("id", "id1"), routes.get(0).groupNameValues);

        routes = webPathRouter.getDestinations("/foo/bar/wildcard/bar/foo/id1");
        assertEquals(2, routes.size());
        assertEquals(ImmutableSet.of("wildcard-foo-id", "slash-wildcard-foo-id"),
            ImmutableSet.of(routes.get(0).destination, routes.get(1).destination));
        //noinspection assertEqualsBetweenInconvertibleTypes
        assertEquals(ImmutableSet.of(ImmutableMap.of("id", "id1"), ImmutableMap.of("id", "id1")),
            ImmutableSet.of(routes.get(0).groupNameValues, routes.get(1).groupNameValues)
        );

        routes = webPathRouter.getDestinations("/foo/bar/wildcard/bar/foo/id1/baz/bar");
        assertEquals(2, routes.size());
        assertEquals(ImmutableSet.of("wildcard-foo-id-2", "slash-wildcard-foo-id-2"),
            ImmutableSet.of(routes.get(0).destination, routes.get(1).destination));
        //noinspection assertEqualsBetweenInconvertibleTypes
        assertEquals(ImmutableSet.of(ImmutableMap.of("id", "id1"), ImmutableMap.of("id", "id1")),
            ImmutableSet.of(routes.get(0).groupNameValues, routes.get(1).groupNameValues)
        );

        routes = webPathRouter.getDestinations("/wildcard/bar/foo/id1/baz/bar");
        assertEquals(1, routes.size());
        assertEquals("wildcard-foo-id-2", routes.get(0).destination);
        assertEquals(ImmutableMap.of("id", "id1"), routes.get(0).groupNameValues);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaxPathParts() {
        WebPathRouter<String> webPathRouter = WebPathRouter.create(5);
        webPathRouter.add("/1/2/3/4/5/6", "max-path-parts");
    }
}
