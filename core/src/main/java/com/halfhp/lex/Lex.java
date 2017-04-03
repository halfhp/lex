package com.halfhp.lex;

import android.app.Application;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Lex {

    protected static Resources resources;
    private static String OPEN_DELIMITER = "{";
    private static String CLOSE_DELIMITER = "}";
    private static Pattern LEX_KEY_PATTERN;

    public static void init(@NonNull Application app) {
        init(app, OPEN_DELIMITER, CLOSE_DELIMITER);
    }

    public static void init(@NonNull Application app, @NonNull String openDelimiter, @NonNull String closeDelimiter) {
        if(Lex.resources != null) {
            throw new LexAlreadyInitializedException();
        }

        Lex.resources = app.getResources();
        Lex.OPEN_DELIMITER = openDelimiter;
        Lex.CLOSE_DELIMITER = closeDelimiter;
        LEX_KEY_PATTERN = buildKeyPattern(OPEN_DELIMITER, CLOSE_DELIMITER);
    }

    private static Pattern buildKeyPattern(@NonNull String openDelimiter, @NonNull String closeDelimiter) {
        return Pattern.compile(".*("
                + Pattern.quote(openDelimiter)
                + "[a-zA-Z0-9_]+?"
                + Pattern.quote(closeDelimiter)
                + ").*");
    }

    public static @NonNull UnparsedLexString say(@StringRes int resourceId) {
        checkState();
        return say(resources.getString(resourceId));
    }

    public static @NonNull UnparsedLexString say(@NonNull CharSequence pattern) {
        checkState();
        return new UnparsedLexString(pattern);
    }

    private static void checkState() {
        if(resources == null) {
            throw new LexNotInitializedException();
        }
    }

    private static class LexContext {
        protected Pattern keyPattern;
        protected String openDelimiter;
        protected String closeDelimiter;

        // instantiate as members so they can be reused in larger replacement sequences
        private final String[] keyReplaceHolder = new String[1];
        private final String[] valueReplaceHolder = new String[1];

        protected Matcher matcher;

        protected final CharSequence templateText;
        protected CharSequence inflatedText;
        protected HashSet<String> inflatedKeys = new HashSet<>();

        LexContext(@NonNull CharSequence templateText) {
            this.templateText = templateText;
            this.inflatedText = templateText;
            setDelimiters(OPEN_DELIMITER, CLOSE_DELIMITER);
        }

        private void setDelimiters(@NonNull String openDelimiter, @NonNull String closeDelimiter) {
            this.openDelimiter = openDelimiter;
            this.closeDelimiter = closeDelimiter;
            if(openDelimiter == OPEN_DELIMITER && closeDelimiter == CLOSE_DELIMITER) {
                this.keyPattern = LEX_KEY_PATTERN;
            } else {
                this.keyPattern = buildKeyPattern(openDelimiter, closeDelimiter);
            }
        }
    }

    public static class LexString {

        protected final LexContext context;

        private LexString(@NonNull LexContext context) {
            this.context = context;
        }

        public @NonNull
        ParsedLexString with(@NonNull final Key key, @NonNull final CharSequence value) {
            if(key == null) {
                throw new LexInvalidKeyException("Attempt to use null value as a Key.");
            } else if(value == null) {
                throw new LexInvalidValueException(String.format("Attempt to use null value for Key: %s%s%s",
                        context.openDelimiter, key, context.closeDelimiter));
            }
            return applyKey(key.name(), value.toString(), false);
        }

        public @NonNull
        ParsedLexString with(@NonNull Key key, @StringRes int resourceId) {
            return with(key, resources.getString(resourceId));
        }

        protected ParsedLexString applyKey(@NonNull final String key, @NonNull final String value, final boolean isOptional) {
            if (context.inflatedKeys.contains(key)) {
                throw new LexDuplicateKeyException(key);
            }
            context.inflatedKeys.add(key);
            context.keyReplaceHolder[0] = context.openDelimiter + key + context.closeDelimiter;
            context.valueReplaceHolder[0] = value;
            final CharSequence result = TextUtils.replace(context.inflatedText,
                    context.keyReplaceHolder,
                    context.valueReplaceHolder);
            if (!isOptional && TextUtils.equals(context.inflatedText, result)) {
                throw new LexInvalidKeyException(String.format("Non-optional Key missing in pattern. " +
                        "key: %s%s%s template: [%s]", context.openDelimiter, key, context.closeDelimiter, context.templateText));
            } else {
                context.inflatedText = result;
            }
            return new ParsedLexString(context);
        }
    }

    public static class UnparsedLexString extends LexString {

        private UnparsedLexString(@NonNull CharSequence templateText) {
            super(new LexContext(templateText));
        }

        public LexString delimitBy(@NonNull String openDelimiter, @NonNull String closeDelimiter) {
            context.setDelimiters(openDelimiter, closeDelimiter);
            return this;
        }
    }

    public static class ParsedLexString extends LexString {

        private ParsedLexString(LexContext context) {
            super(context);
        }

        public void to(@NonNull TextView textView) {
            textView.setText(context.inflatedText);
        }

        private CharSequence getValidatedText() {
            // TODO: this match slows things down quite a bit...could use some optimization.
            if(context.matcher == null) {
                context.matcher = context.keyPattern.matcher(context.inflatedText);
            } else {
                context.matcher.reset(context.inflatedText);
            }
            if(context.matcher.find()) {
                throw new LexMissingKeyException(String
                        .format("Key %s not supplied to template: [%s]", context.matcher.group(1), context.templateText));
            }
            return context.inflatedText;
        }

        public @NonNull CharSequence make() {
            return getValidatedText();
        }

        public @NonNull String makeString() {
            return getValidatedText().toString();
        }
    }

    public interface Key {
        String name();
    }

    public static class LexDuplicateKeyException extends RuntimeException {
        public LexDuplicateKeyException(@NonNull String key) {
            super(String.format("Duplicate Key detected: %s", key));
        }
    }

    public static class LexMissingKeyException extends RuntimeException {
        public LexMissingKeyException(@NonNull String message) {
            super(message);
        }
    }

    public static class LexInvalidKeyException extends RuntimeException {
        public LexInvalidKeyException(@NonNull String message) {
            super(message);
        }
    }

    public static class LexInvalidValueException extends RuntimeException {
        public LexInvalidValueException(@NonNull String message) {
            super(message);
        }
    }

    public static class LexNotInitializedException extends RuntimeException {
        public LexNotInitializedException() {
            super("Lex has not been initialized!  Make a call to Lex.init() " +
                    "appears at the top of your Application's onCreate() method.");
        }
    }

    public static class LexAlreadyInitializedException extends RuntimeException {
        public LexAlreadyInitializedException() {
            super("Lex has already been initialized! (Attempt to invoke " +
                    "Lex.init(...) more than once.");
        }
    }
}
