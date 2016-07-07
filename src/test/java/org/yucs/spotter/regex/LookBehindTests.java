package org.yucs.spotter.regex;

import org.junit.Test;

import static org.junit.Assert.*;

public class LookBehindTests {
    @Test
    public void oneLetter() throws Exception {
        Regex r = new Regex("(?<=a)b");
        assertTrue(r.match("cab"));
        assertFalse(r.match("bed"));
    }

    @Test
    public void twoLetter() throws Exception {
        Regex r = new Regex("(?<=ca)b");
        assertTrue(r.match("cab"));
        assertFalse(r.match("bed"));
    }

    @Test
    public void threeLetter() throws Exception {
        Regex r = new Regex("(?<=cab)b");
        assertTrue(r.match("cabb"));
        assertFalse(r.match("bedb"));
    }

    // Implementation doesn't backtrack greedy correctly.
    @Test
    public void lookBehindGroup() throws Exception {
        Regex r = new Regex("c(.*)(?<=\\1)b");
        Matcher m = r.Matcher();
        assertTrue(m.match("cab"));
    }

    @Test
    public void lookBehindQuantifier() throws Exception {
        Regex r = new Regex("(?<=.{3})(.*)");
        Matcher m = r.Matcher();
        assertFalse(m.match("a"));
        assertFalse(m.match("ab"));
        assertFalse(m.match("abc"));
        assertTrue(m.match("abcd"));
        assertEquals("d", m.getGroup(0));
        assertEquals("d", m.getGroup(1));
        assertTrue(m.match("abcde"));
        assertEquals("de", m.getGroup(0));
        assertEquals("de", m.getGroup(1));
    }
}
