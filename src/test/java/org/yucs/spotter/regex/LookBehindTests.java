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
    public void broken() throws Exception {
        Regex r = new Regex("c(.*)(?<=\\1)b");
        Matcher m = r.Matcher();
        assertTrue(m.match("cab"));
    }
}