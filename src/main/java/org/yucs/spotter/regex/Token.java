package org.yucs.spotter.regex;

abstract class Token {
    Token next = null;

    abstract boolean match(Matcher m) throws RegexException;

    Token reverse() throws RegexException {
        if (next == NullToken.Instance)
            return this;

        Token prev = next.reverse();
        Token tmp = prev;
        while (tmp.next != NullToken.Instance)
            tmp = tmp.next;
        tmp.next = this;

        return prev;
    }

    Token() {
        next = NullToken.Instance;
    }

    int captureGroup() {
        return -1;
    }
}