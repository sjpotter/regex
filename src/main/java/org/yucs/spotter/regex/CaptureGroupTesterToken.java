package org.yucs.spotter.regex;

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
    boolean match(Matcher m) throws RegexException {
        return m.getGroup(group) != null && next.match(m);
    }
}