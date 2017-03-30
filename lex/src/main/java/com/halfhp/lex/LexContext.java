package com.halfhp.lex;

import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LexContext {

    static String OPEN_DELIMITER = "{";
    static String CLOSE_DELIMITER = "}";
    static Pattern LEX_KEY_PATTERN;

    protected Pattern keyPattern;
    protected String openDelimiter;
    protected String closeDelimiter;

    // instantiate as members so they can be reused in larger replacement sequences
    final String[] keyReplaceHolder = new String[1];
    final CharSequence[] valueReplaceHolder = new CharSequence[1];

    protected Matcher matcher;

    protected final CharSequence templateText;
    protected CharSequence inflatedText;
    protected HashSet<String> inflatedKeys = new HashSet<>();

    protected LexTransform lastTransform = new LexTransform();

    LexContext(@NonNull CharSequence templateText) {
        this(templateText, LEX_KEY_PATTERN, OPEN_DELIMITER, CLOSE_DELIMITER);
    }

    LexContext(@NonNull CharSequence templateText, @NonNull Pattern keyPattern,
               @NonNull String openDelimiter, @NonNull String closeDelimiter) {
        this.templateText = templateText;
        this.inflatedText = templateText;
        this.keyPattern = keyPattern;
        setDelimiters(openDelimiter, closeDelimiter);
    }

    void setDelimiters(@NonNull String openDelimiter, @NonNull String closeDelimiter) {
        final boolean delimitersChanged = openDelimiter !=
                this.openDelimiter || closeDelimiter != this.closeDelimiter;
        this.openDelimiter = openDelimiter;
        this.closeDelimiter = closeDelimiter;
        if (delimitersChanged) {
            this.keyPattern = buildKeyPattern(openDelimiter, closeDelimiter);
        }
    }

    static void setDefaultDelimiters(@NonNull String openDelimiter, @NonNull String closeDelimiter) {
        LEX_KEY_PATTERN = buildKeyPattern(openDelimiter, closeDelimiter);
    }

    private static Pattern buildKeyPattern(@NonNull String openDelimiter, @NonNull String closeDelimiter) {
        return Pattern.compile(".*("
                + Pattern.quote(openDelimiter)
                + "[a-zA-Z0-9_]+?"
                + Pattern.quote(closeDelimiter)
                + ").*");
    }
}
