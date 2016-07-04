package org.yucs.spotter.regex;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CaptureGroups {
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
        assertEquals(m.getGroup(1), "aefa");
    }

    @Test
    public void slashes() throws Exception {
        Regex r = new Regex("^\\\\(.*)$");
        Matcher m = r.Matcher();
        assertTrue(m.match("\\abc"));
        assertEquals(m.getGroup(1), "abc");
    }
}
