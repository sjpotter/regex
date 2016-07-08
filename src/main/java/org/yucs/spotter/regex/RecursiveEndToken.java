package org.yucs.spotter.regex;

class RecursiveEndToken extends Token {
    final private Matcher m_old;

    RecursiveEndToken(Matcher m, Token next) {
        m_old = m;
        this.next = next;
    }

    @Override
    boolean match(Matcher m) throws RegexException {
        m_old.copy(m);
        return next.match(m_old);
    }
}
