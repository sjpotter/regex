package org.yucs.spotter.regex;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecursiveTests {
    @Test
    public void recursiveTest() throws Exception {
        Regex r = new Regex("a(?R)?z");
        Matcher m = r.Matcher();

        assertTrue(m.match("aaazzz123"));
        assertEquals("aaazzz", m.getGroup(0));

        assertFalse(m.match("aaabbzzz"));

        r = new Regex("(.)(?R)?(\\1)");
        m = r.Matcher();
        assertTrue(m.match("abba"));
        assertEquals(m.getGroup(0), "abba");
        assertEquals(m.getGroup(1), "a");
        assertEquals(m.getGroup(2), "a");
    }

    @Test
    public void subgroupRecursion() throws Exception {
        Regex r = new Regex("^(.)(\\1)(?2)?b$");
        // expects this to be 2 or more a's followed by a final b, but not working, as the recursion isn't just matching
        // the capture token, but chains to b inside the recursion, which isn't correct
        assertTrue(r.match("aaab"));

        r = new Regex("^(.)(b\\1)((?2)*)b$");
        Matcher m = r.Matcher();
        assertTrue(m.match("ababababababababab"));
        assertEquals(m.getGroup(0), "ababababababababab");
        assertEquals(m.getGroup(1), "a");
        assertEquals(m.getGroup(2), "ba");
        assertEquals(m.getGroup(3), "bababababababa");
    }
}
