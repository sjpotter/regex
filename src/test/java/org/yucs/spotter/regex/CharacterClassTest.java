package org.yucs.spotter.regex;

import org.junit.Test;

import static org.junit.Assert.*;

public class CharacterClassTest {
    private static final String numbers = "0123456789";
    private static final String words = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Test
    public void testShortcuts() throws Exception {
        CharacterClass c1 = new CharacterClass("\\d", 0, 1);
        CharacterClass c2 = new CharacterClass(numbers, 0, numbers.length() - 1);

        assertEquals(c2, c1);

        c1 = new CharacterClass("\\w", 0, 1);
        c2 = new CharacterClass(words, 0, words.length()-1);

        assertEquals(c2, c1);

        c1 = new CharacterClass("\\s", 0, 1);
        c2 = new CharacterClass(" \r\n\t\f",0, 4);

        assertEquals(c2, c1);
        assertTrue(c1.match(' '));
        assertTrue(c1.match('\r'));
        assertTrue(c1.match('\n'));
    }

    @Test
    public void testEscaped() throws Exception {
        CharacterClass c1 = new CharacterClass("$", 0, 0);
        CharacterClass c2 = new CharacterClass("\\$", 0, 1);

        assertEquals(c1, c2);
        assertTrue(c1.match('$'));

        c1 = new CharacterClass("\\\\", 0, 1);
        assertTrue(c1.match('\\'));

        c1 = new CharacterClass("+", 0, 0);
        c2 = new CharacterClass("\\+", 0, 1);
        assertEquals(c1, c2);
        assertTrue(c1.match('+'));

        c1 = new CharacterClass("*", 0, 0);
        c2 = new CharacterClass("\\*", 0, 1);
        assertEquals(c1, c2);
        assertTrue(c1.match('*'));

        c1 = new CharacterClass("?", 0, 0);
        c2 = new CharacterClass("\\?", 0, 1);
        assertEquals(c1, c2);
        assertTrue(c1.match('?'));

        c1 = new CharacterClass("a^", 0, 1);
        c2 = new CharacterClass("a\\^", 0, 2);
        assertEquals(c1, c2);
        assertTrue(c1.match('^'));
    }

    @Test(expected = RegexException.class)
    public void invalidRangeOrder() throws Exception {
        new CharacterClass("9-0", 0, 2);
    }

    @Test
    public void testRange() throws Exception {
        CharacterClass c1 = new CharacterClass("0-9", 0, 2);
        CharacterClass c2 = new CharacterClass(numbers, 0, 9);

        assertEquals(c2, c1);
        assertEquals(c2.hashCode(), c1.hashCode());

        c1 = new CharacterClass("0-9a-zA-Z", 0, 8);
        c2 = new CharacterClass(words, 0, words.length()-1);

        assertEquals(c2, c1);
        assertEquals(c2.hashCode(), c1.hashCode());
    }

    @Test
    public void negated() throws Exception {
        CharacterClass c1 = new CharacterClass("^\\d", 0, 2);
        CharacterClass c2 = new CharacterClass("\\D", 0, 1);

        assertTrue(c1.match('a'));
        assertTrue(c2.match('a'));
        assertFalse(c1.match('1'));
        assertFalse(c2.match('1'));
        assertEquals(c1, c2);

        c1 = new CharacterClass("^\\w", 0, 2);
        c2 = new CharacterClass("\\W", 0, 1);
        assertEquals(c2, c1);

        c1 = new CharacterClass("^\\s", 0, 2);
        c2 = new CharacterClass("\\S", 0, 1);
        assertEquals(c2, c1);
        c1 = new CharacterClass("\\s", 0, 1);
        c2 = new CharacterClass("^\\S", 0, 2);
        assertEquals(c2, c1);

        c1 = new CharacterClass("^0-9", 0, 3);
        c2 = new CharacterClass("^" + numbers, 0, numbers.length());
        assertEquals(c1, c2);

        assertTrue(c1.match('a'));
        assertFalse(c1.match('1'));
    }
}