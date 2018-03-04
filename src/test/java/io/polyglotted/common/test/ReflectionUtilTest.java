package io.polyglotted.common.test;

import io.polyglotted.common.util.ReflectionUtil;
import lombok.Data;
import org.junit.Test;

import static io.polyglotted.common.model.MapResult.immutableResult;
import static io.polyglotted.common.util.MapBuilder.immutableMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ReflectionUtilTest extends ReflectionUtil {

    @Test public void testSafeForName() throws Exception {
        assertEquals(safeForName(null), null);
        assertEquals(safeForName("io.pgmodel.DoesNotExist"), null);
        assertEquals(safeForName("io.polyglotted.common.test.ReflectionUtilTest$MyInner"), MyInner.class);
    }

    @Test public void testCreate() throws Exception {
        assertEquals(create(MyInner.class), new MyInner(null, 0));
        assertEquals(create(MyEmptyConstructor.class), new MyEmptyConstructor());
    }

    @Test(expected = InstantiationException.class)
    public void testCreateFail() { create(MyAbsConstructor.class); }

    @Test public void testAsEnum() throws Exception {
        assertEquals(asEnum(MyInner.class, "STRING"), "STRING");
    }

    @Test public void testIsEnum() throws Exception {
        assertFalse(isEnum(MyInner.class));
        assertTrue(isEnum(Thread.State.class));
    }

    @Test public void testIsAssignable() throws Exception {
        assertFalse(isAssignable(MyInner.class, null));
        assertFalse(isAssignable(MyChild.class, MyInner.class));
        assertTrue(isAssignable(MyInner.class, MyChild.class));
    }

    @Test public void testDeclaredField() throws Exception {
        assertNotNull(declaredField(MyChild.class, "value"));
        assertNull(declaredField(MyChild.class, "foo"));
    }

    @Test public void testGetFieldValue() throws Exception {
        assertEquals(fieldValue(new MyInner("hi", 3), "value"), "hi");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetFieldValueFail() throws Exception {
        fieldValue(new MyInner("hi", 3), "boo");
    }

    @Test public void testSetFieldValue() throws Exception {
        assertEquals(fieldValue(new MyInner("hi", 3), "value", "hello"), new MyInner("hello", 3));
    }

    @Test(expected = IllegalStateException.class)
    public void testSetFieldValueFail() throws Exception {
        fieldValue(new MyInner("hi", 3), "boo", "boo");
    }

    @Test public void testFieldValues() throws Exception {
        assertEquals(fieldValues(new MyInner("hi", 3)), immutableResult(immutableMap("value", "hi")));
        assertEquals(fieldValues(new MyInner(null, 3)), immutableResult(immutableMap()));
    }

    @Test public void testInvoke() throws Exception {
        assertNull(safeInvoke(null, null));
        safeInvoke(new InvokeHelper(), "handleNoResult", (Object) "hello".getBytes());
        assertEquals(safeInvoke(new InvokeHelper(), "handleEmpty"), "hello");
        assertEquals(safeInvoke(new InvokeHelper(), "handle", (Object) "hello".getBytes()), "hello");
    }

    @Test(expected = NoSuchMethodException.class)
    public void testInvokeFail() throws Exception {
        safeInvoke(new InvokeHelper(), "handleNotFound");
    }

    @Data
    static class MyInner {
        private final static int STA = 5;
        private final String value;
        private final transient int none;
        private volatile boolean vol = false;
    }

    public static class MyChild extends MyInner {
        public MyChild(String value, int none) { super(value, none); }
    }

    @Data
    private static class MyEmptyConstructor {
    }

    private abstract class MyAbsConstructor {
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class InvokeHelper {
        public void handleNoResult(byte[] message) { assertEquals("hello", handle(message)); }

        public String handleEmpty() { return "hello"; }

        public String handle(byte[] message) { return new String(message); }
    }
}