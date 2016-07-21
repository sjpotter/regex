package org.yucs.spotter.regex;

class Quantifier {
    enum type { GREEDY, NONGREEDY, POSSESSIVE}

    final int min;
    final int max;
    type matchType;

    Quantifier(int min, int max) {
        this.min = min;
        this.max = max;
        this.matchType = type.GREEDY;
    }

    private Quantifier(int min, int max, type matchType) {
        this.min = min;
        this.max = max;
        this.matchType = matchType;
    }

    Quantifier cloneDecrement() {
        int min = (this.min > 0) ? this.min-1 : this.min;
        int max = (this.max > 0) ? this.max-1 : this.max;

        return new Quantifier(min, max, this.matchType);
    }
}