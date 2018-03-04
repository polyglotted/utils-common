package io.polyglotted.common.test;

import io.polyglotted.common.model.MapResult;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class MapResultTest {
    public static Object[][] resultInputs() {
        return new Object[][]{
            {MapResult.simpleResult()},
            {MapResult.immutableResult()},
        };
    }

    @Test @Parameters(method = "resultInputs")
    public void mapResultAll(MapResult expected) {
        assertThat(inAndOut(expected), is(expected));
        assertThat(outOnly(expected), is(expected));
        assertThat(inOnly(expected), is(expected));
    }

    private static Map<String, Object> inAndOut(Map<String, Object> map) { return map; }

    private static Map<String, Object> outOnly(MapResult map) { return map; }

    private static MapResult inOnly(Map<String, Object> map) { return (MapResult) map; }
}