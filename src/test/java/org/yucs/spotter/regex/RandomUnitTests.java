package org.yucs.spotter.regex;

import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class RandomUnitTests {
    @Test(expected = RegexException.class)
    public void invalidAnchor() throws Exception {
        Matcher matcher = Mockito.mock(Matcher.class);
        when(matcher.getText()).thenReturn("abc");
        when(matcher.getTextPosition()).thenReturn(0);

        Token a = new AnchorToken('c');
        a.match(matcher);
    }
}
