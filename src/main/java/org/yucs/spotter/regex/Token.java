package org.yucs.spotter.regex;

abstract class Token {
    Token next = null;

    abstract boolean match(Matcher m) throws RegexException;

    Token reverse() throws RegexException {
        return reverse(this);
    }

    Token reverse(Token cur) throws RegexException {
        if (cur.next == NullToken.Instance)
            return cur;

        Token prev = cur.next.reverse();
        cur.next = NullToken.Instance;

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