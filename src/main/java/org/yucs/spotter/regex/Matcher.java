package org.yucs.spotter.regex;

import java.util.*;

@SuppressWarnings("WeakerAccess")
public class Matcher {
    private Map<Integer, Stack<String>> groups;
    private String text;
    private int text_pos;

    final private int parenCount;
    final private Map<Integer, Token> captureMap;


    private final Token t;

    Matcher(Tokenizer tokenizer) throws RegexException {
        t = tokenizer.tokenize();
        parenCount = tokenizer.captureCount;
        captureMap = tokenizer.captureMap;
    }

    private Matcher(int parenCount,  Map<Integer, Token> captureMap, String text) {
        this.t = null;
        this.parenCount = parenCount;
        this.captureMap = captureMap;
        this.text = text;

        groups = new HashMap<>();
        for(int i=0; i < parenCount; i++) {
            groups.put(i, new Stack<String>());
        }
    }

    /**
     * Returns a boolean that says if the text matched against the Regex
     *
     * @param text the text to match against the regex
     * @return true/false if the text matches against the regex
     * @throws RegexException
     */
    public boolean match(String text) throws RegexException {
        groups        = new HashMap<>();
        for(int i=0; i < parenCount; i++) {
            groups.put(i, new Stack<String>());
        }
        this.text = text;

        for(int i=0; i < text.length() || i == 0; i++) { //need to test empty text string too
            text_pos = i;
            if (t.match(this)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return a List of all capture groups.  Groups captured will have a String, groups not captured will have a null
     */
    public List<String> getGroups() {
        ArrayList<String> ret = new ArrayList<>(parenCount);

        for(int i=0; i < parenCount; i++) {
            if (groups.get(i).size() == 0) {
                ret.add(null);
            } else {
                ret.add(groups.get(i).peek());
            }
        }

        return ret;
    }

    /**
     * @param pos The capturing group to retrieve
     * @return The String from text captured by this group or null if the group wasn't executed in the match
     * @throws RegexException
     */
    public String getGroup(int pos) throws RegexException {
        if (pos >= groups.size())
            throw new RegexException("Group " + pos + " does not exit");

        if (groups.get(pos).size() == 0)
            return null;

        return groups.get(pos).peek();
    }

    void pushGroup(int paren, String rec) {
        groups.get(paren).push(rec);
    }

    void popGroup(int paren) {
        if (paren >= 0) {
            if (groups.get(paren).size() > 0)
                groups.get(paren).pop();
        }
    }

    int getTextPosition() {
        return text_pos;
    }

    void setTextPosition(int pos) {
        text_pos = pos;
    }

    String getText() { return text; }

    Token getCaptureToken(int pos) throws RegexException {
        if (pos >= captureMap.size())
            throw new RegexException("Trying to retrieve a token for a capture group that doesn't exist");

        return captureMap.get(pos);
    }

    public Matcher copy() {
        Matcher m = new Matcher(parenCount, captureMap, text);
        m.setTextPosition(this.text_pos);

        return m;
    }
}