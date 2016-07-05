package org.yucs.spotter.regex;

import org.junit.Test;

import static org.junit.Assert.*;

public class RegexTests {
    @Test
    public void dotMatch() throws Exception {
        Regex r = new Regex(".");
        assertTrue(r.match("1"));
        assertTrue(r.match("2135"));
        assertTrue(r.match("abdsfds"));
        assertFalse(r.match(""));
    }

    @Test
    public void startAnchorTest() throws Exception {
        Regex r = new Regex("^\\d");

        assertTrue(r.match("1asdfse"));

        assertFalse(r.match("a1sdfse"));
    }

    @Test
    public void endAnchorTest() throws Exception {
        Regex r = new Regex("\\d$");

        assertTrue(r.match("abcdsfsdfs223dsfsdaf32"));
        assertFalse(r.match("abcdsfsdfs223dsfsdaf"));
    }

    @Test
    public void quantifierTest() throws Exception {
        Regex r = new Regex("^\\d*$");

        assertTrue(r.match(""));
        assertTrue(r.match("123213"));
        assertFalse(r.match("23432vbwef23142"));

        r = new Regex("^\\d?a");
        assertTrue(r.match("a"));
        assertTrue(r.match("1a"));
        assertFalse(r.match("12a"));

        r = new Regex("\\d+");
        assertFalse(r.match(""));
        assertTrue(r.match("123213"));
        assertTrue(r.match("23432vbwef23142"));

        r = new Regex("\\d{1}");
        assertTrue(r.match("123213"));
        assertTrue(r.match("12sdaf3213"));
        assertFalse(r.match("SAdfds"));

        r = new Regex("\\d{5,}");
        assertTrue(r.match("asdfds12324dsaf"));
        assertFalse(r.match("1234abdcdsf"));

        r = new Regex("\\d{10,11}");
        assertTrue(r.match("1234567890abc"));
        assertFalse(r.match("12345"));

        r = new Regex("^\\d{10,abc}");
        assertTrue(r.match("1{10,abc}"));
        assertFalse(r.match("11234567890,abc"));
    }

    @Test(expected = RegexException.class)
    public void invalidClassTest() throws Exception {
        new Regex("[asdf");
    }

    @Test(expected = RegexException.class)
    public void invalidRangeOrder() throws Exception {
        new Regex("[9-0]");
    }

    @Test(expected = RegexException.class)
    public void illegalCharacterTest() throws Exception {
        new Regex("123**");
    }

    @Test
    public void characterClassTest() throws Exception {
        Regex r = new Regex("^[123]*$");
        assertTrue(r.match("1232321"));
        assertFalse(r.match("12324235"));

        r = new Regex("^[^\\d]*$");
        assertTrue(r.match("absdfds"));
        assertFalse(r.match("asdf324sdfsd"));

        r = new Regex("^[a-f]*$");
        assertTrue(r.match("abcdebdca"));
        assertFalse(r.match("asdfewrsdf"));

        r = new Regex("^[^abc]*$");
        assertTrue(r.match("1234"));
        assertFalse(r.match("1234a"));
    }

    @Test(expected = RegexException.class)
    public void invalidEndSlashCharacterTest() throws Exception {
        new Regex("\\");
    }

    @Test(expected = RegexException.class)
    public void invalidSlashCharacterTest() throws Exception {
        new Regex("^\\P$");
    }

    @Test
    public void slashCharacterTest() throws Exception {
        Regex r = new Regex("^\\\\$");
        assertTrue(r.match("\\"));
        assertFalse(r.match("A"));

        r = new Regex("^\\+$");
        assertTrue(r.match("+"));
        assertFalse(r.match("*"));

        r = new Regex("^\\*$");
        assertTrue(r.match("*"));
        assertFalse(r.match("+"));

        r = new Regex("^\\?$");
        assertTrue(r.match("?"));
        assertFalse(r.match("+"));

        r = new Regex("^\\^$");
        assertTrue(r.match("^"));
        assertFalse(r.match("+"));

        r = new Regex("^\\$$");
        assertTrue(r.match("$"));
        assertFalse(r.match("+"));
    }

    @Test
    public void otherShortcutTests() throws Exception {
        Regex r = new Regex("^\\D*$");
        assertTrue(r.match("abcdfd"));
        assertFalse(r.match("asdf324sdf"));

        r = new Regex("^\\w*$");
        assertTrue(r.match("fdsaf08ewws34DWERdsf"));
        assertFalse(r.match("sdf324r;32fw`"));

        r = new Regex("^\\W*$");
        assertTrue(r.match(";';'`@!#$@"));
        assertFalse(r.match("aefsa;213jkjsafs@!@$"));

        r = new Regex("\\s+");
        assertTrue(r.match("asdfd sdf"));
        assertTrue(r.match("sdf\tadfsd"));
        assertTrue(r.match("sdf\t\n32rfsf"));
        assertFalse(r.match("Sdferfsd324"));

        r = new Regex("^\\S+$");
        assertTrue(r.match("Sdfs324"));
        assertFalse(r.match("sdfsd sdfsf"));
    }

    @Test
    public void broken() throws Exception {
        Regex r = new Regex("^(abc|def|(hij*|kl*m)nop)qrs$");
        assertTrue(r.match("hijjnopqrs"));
    }

    @Test
    public void wordBoundaries() throws Exception {
        Regex r = new Regex("\\babc");
        assertTrue(r.match("abc"));
        assertTrue(r.match("ab abc"));
        r = new Regex("\\Babc");
        assertFalse(r.match("abc"));
        assertFalse(r.match("ab abc"));
        r = new Regex("abc\\b");
        assertTrue(r.match("abc"));
        assertTrue(r.match("abc ab"));
        r = new Regex("abc\\B");
        assertFalse(r.match("abc"));
        assertFalse(r.match("abc ab"));
    }

    @Test
    public void atomicGrouping() throws Exception {
        Regex r = new Regex("a(?>bc|b)c");
        assertTrue(r.match("abcc"));
        assertFalse(r.match("abc"));

        r = new Regex("a(?>c|b)c");
        assertTrue(r.match("acc"));
        assertTrue(r.match("abc"));
        assertFalse(r.match("aac"));
    }

    @Test
    public void lookAhead() throws Exception {
        Regex r = new Regex("(?=regex)regex(abc)");
        Matcher m = r.Matcher();
        assertTrue(m.match("regexabc"));
        assertEquals(m.getGroup(0), "regexabc");
        assertEquals(m.getGroup(1), "abc");
    }

    @Test
    public void ifThenElse() throws Exception {
        Regex r = new Regex("^(?(?=regex)(regex)|(abc))$");
        Matcher m = r.Matcher();

        assertTrue(m.match("regex"));
        assertEquals(m.getGroup(1), "regex");
        assertTrue(m.match("abc"));
        assertEquals(m.getGroup(2), "abc");
    }
}