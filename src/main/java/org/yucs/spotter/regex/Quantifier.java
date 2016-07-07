package org.yucs.spotter.regex;

class Quantifier {
    final int min;
    final int max;
    boolean greedy;

    Quantifier(int min, int max) {
        this.min = min;
        this.max = max;
        this.greedy = true;
    }

    private Quantifier(int min, int max, boolean greedy) {
        this.min = min;
        this.max = max;
        this.greedy = greedy;
    }

    Quantifier cloneDecrement() {
        int min = (this.min > 0) ? this.min-1 : this.min;
        int max = (this.max > 0) ? this.max-1 : this.max;

        return new Quantifier(min, max, this.greedy);
    }
}