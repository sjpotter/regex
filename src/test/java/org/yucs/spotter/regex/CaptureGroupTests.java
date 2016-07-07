package org.yucs.spotter.regex;

import org.junit.Test;

import static org.junit.Assert.*;

public class CaptureGroupTests {
    @Test
    public void backtracking() throws Exception {
        Regex r = new Regex("^(aef.)*aefbaefcaefd$");
        Matcher m = r.Matcher();
        assertTrue(m.match("aefaaefbaefcaefd"));
        assertEquals(m.getGroup(1), "aefa");

        assertTrue(m.match("aefaaef0aefbaefcaefd"));
        assertEquals(m.getGroup(1), "aef0");
    }

    @Test
    public void nonGreedy() throws Exception {
        Regex r = new Regex("^(aef.)*?aefbaefcaefd$");
        Matcher m = r.Matcher();
        assertTrue(m.match("aefaaefbaefcaefd"));
        assertEquals("aefa", m.getGroup(1));
    }

    @Test
    public void slashes() throws Exception {
        Regex r = new Regex("^\\\\(.*)$");
        Matcher m = r.Matcher();
        assertTrue(m.match("\\abc"));
        assertEquals(m.getGroup(1), "abc");
    }

    @Test
    public void backReference() throws Exception {
        Regex r = new Regex("^(.)\\1$");
        Matcher m = r.Matcher();
        assertTrue(m.match("aa"));
        assertEquals(m.getGroup(0), "aa");
        assertEquals(m.getGroup(1), "a");
        assertFalse(m.match("ab"));
    }

    @Test
    public void nullBackReference() throws Exception {
        Regex r = new Regex("^(a|(.))\\2$");
        assertTrue(r.match("aa"));
    }

    @Test
    public void shouldBeNull() throws Exception {
        Regex r = new Regex("^(.)abc$");
        Matcher m = r.Matcher();
        assertFalse(m.match("defg"));
        assertNull(m.getGroup(0));
        assertNull(m.getGroup(1));
    }
}