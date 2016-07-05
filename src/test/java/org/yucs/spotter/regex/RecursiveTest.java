package org.yucs.spotter.regex;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecursiveTest {
    @Test
    public void recursiveTest() throws Exception {
        Regex r = new Regex("a(?R)?z");
        Matcher m = r.Matcher();

        assertTrue(m.match("aaazzz123"));
        assertEquals(m.getGroup(0), "aaazzz");

        assertFalse(m.match("aaabbzzz"));

        r = new Regex("(.)(?R)?(\\1)");
        m = r.Matcher();
        assertTrue(m.match("abba"));
        assertEquals(m.getGroup(0), "abba");
        assertEquals(m.getGroup(1), "a");
        assertEquals(m.getGroup(2), "a");
    }
}
