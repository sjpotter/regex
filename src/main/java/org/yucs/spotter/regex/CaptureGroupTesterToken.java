package org.yucs.spotter.regex;

// Really only used in the If of an IfThenElse Token
// Q. does it need to proceed to the next match (i.e. can it be part of a longer if clause?)
//
// Even if not, shouldn't make a difference as its next token will be a NullToken and IfClause
// is run with an empty nextToken stack.

class CaptureGroupTesterToken extends Token implements TestableToken {
    final private int group;

    CaptureGroupTesterToken(String capture) throws RegexException {
        try {
            group = Integer.parseInt(capture);
        } catch (NumberFormatException e) {
            throw new RegexException("CaptureGroupTesterToken: Unable to convert " + capture + " to an Integer");
        }
    }

    @Override
    public boolean match(Matcher m) throws RegexException {
        return m.getGroup(group) != null && next.match(m);
    }
}