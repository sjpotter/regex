package org.yucs.spotter.regex;

class QuantifierFactory {
    private final Quantifier q;
    final int regex_pos;

    private QuantifierFactory(Quantifier q, int regex_pos) {
        this.q = q;
        this.regex_pos = regex_pos;
    }

    // parses out */+/{n}/{n,}/{n,m} syntax
    // */+/? are easy.  {} is complicated, as it can be a quantifier or just a regular regex character
    static QuantifierFactory parse(String regex, int regex_pos) {
        if (regex.length() == regex_pos)
            return null;

        Quantifier q = null;
        switch (regex.charAt(regex_pos)) {
            case '{':
                int end_pos = regex.indexOf('}', regex_pos);
                if (end_pos != -1)
                    q = handleVariable(regex, regex_pos + 1);
                if (q != null)
                    regex_pos = end_pos + 1;
                break;
            case '*':
                q = new Quantifier(0, -1);
                regex_pos++;
                break;
            case '+':
                q = new Quantifier(1, -1);
                regex_pos++;
                break;
            case '?':
                q = new Quantifier(0, 1);
                regex_pos++;
                break;
        }

        if (q != null) {
            if (regex_pos < regex.length()) {
                switch (regex.charAt(regex_pos)) {
                    case '?':
                        q.matchType = Quantifier.type.NONGREEDY;
                        regex_pos++;
                        break;
                    case '+':
                        q.matchType = Quantifier.type.POSSESSIVE;
                        regex_pos++;
                        break;
                }
            }
            return new QuantifierFactory(q, regex_pos);
        }

        return null;
    }

    private static Quantifier handleVariable(String regex, int regex_pos) {
        if (regex.length() > regex_pos && Character.isDigit(regex.charAt(regex_pos))) { //is there a number after the {
            int val = Character.digit(regex.charAt(regex_pos), 10);
            regex_pos++;

            while (regex.length() > regex_pos && Character.isDigit(regex.charAt(regex_pos))) {
                val *= 10;
                val += Character.digit(regex.charAt(regex_pos), 10);
                regex_pos++;
            }

            if (regex.length() > regex_pos && regex.charAt(regex_pos) == '}') { //if it's {#} we need to match exact
                return new Quantifier(val, val);
            } else if (regex.length() > regex_pos && regex.charAt(regex_pos) == ',') { // can be {#,} or {#,#}
                int min = val;

                regex_pos++;
                if (regex.length() > regex_pos && regex.charAt(regex_pos) == '}') { // {#,}
                    return new Quantifier(min, -1);
                } else { // determine if it's a valid // {#,#}
                    if (regex.length() > regex_pos && Character.isDigit(regex.charAt(regex_pos))) {
                        val = Character.digit(regex.charAt(regex_pos), 10);
                        regex_pos++;
                        while (regex.length() > regex_pos && Character.isDigit(regex.charAt(regex_pos))) {
                            val *= 10;
                            val += Character.digit(regex.charAt(regex_pos), 10);
                            regex_pos++;
                        }
                    }

                    if (regex.length() > regex_pos && regex.charAt(regex_pos) == '}') { //maybe valid {#,#}
                        if (min <= val) // {min,val} only valid if min <= val (max)
                            return new Quantifier(min, val);
                    }
                }
            }
        }

        //regex following { invalid as a quantifier
        return null;
    }

    static Token genToken(QuantifierFactory qf, Token t) throws RegexException {
        switch (qf.q.matchType) {
            case GREEDY:
                return new QuantifierGreedyToken(qf.q, t);
            case NONGREEDY:
                return new QuantifierNonGreedyToken(qf.q, t);
            case POSSESSIVE:
                return new QuantifierPossessiveToken(qf.q, t);
            default:
                throw new RegexException("Unknown Quantifier Type: " + qf.q.matchType);
        }
    }
}