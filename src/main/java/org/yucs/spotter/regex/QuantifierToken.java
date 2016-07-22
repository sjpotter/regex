package org.yucs.spotter.regex;

abstract class QuantifierToken extends Token {
    private final int min;
    final int max;
    final protected Token t;
    final boolean clone;

    protected QuantifierToken(int min, int max, Token t, boolean clone) {
        this.min = min;
        this.max = max;
        this.t = t;
        this.clone = clone;
    }

    abstract boolean maxQuantifierStrategy(Matcher m) throws RegexException;

    abstract QuantifierToken cloneDecrement();

    @Override
    boolean match(Matcher m) throws RegexException {
        // if haven't matched the minimum # of times yet, stick a decremented QuantifierToken on next stack and
        // try to match the token we are quantifying
        if (min != 0) {
            m.pushNextStack(cloneDecrement());
            return t.match(m);
        }

        // We've matched the minimum needed (0 for *, 1 for + or if specified in {,} so now unto max
        if (max != 0) {
            return maxQuantifierStrategy(m);
        }

        // max doesn't allow us to quantify more
        return next.match(m);
    }

    int decrementMin() {
        return (this.min > 0) ? this.min-1 : this.min;
    }

    int decrementMax() {
        return (this.max > 0) ? this.max-1 : this.max;
    }
}
