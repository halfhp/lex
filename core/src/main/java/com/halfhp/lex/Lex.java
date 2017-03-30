package com.halfhp.lex;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.TextView;

public final class Lex {

    private static Application app;
    private static String OPEN_DELIMITER = "{";
    private static String CLOSE_DELIMITER = "}";

    public static void init(@NonNull Application app) {
        init(app, OPEN_DELIMITER, CLOSE_DELIMITER);
    }

    public static void init(@NonNull Application app, String openDelimiter, String closeDelimiter) {
        if(Lex.app != null) {
            throw new LexAlreadyInitializedException();
        }
        Lex.app = app;
        Lex.OPEN_DELIMITER = openDelimiter;
        Lex.CLOSE_DELIMITER = closeDelimiter;
    }

    public static @NonNull
    LexString say(@StringRes int resourceId) {
        checkState();
        return say(app.getString(resourceId));
    }

    public static @NonNull
    LexString say(@NonNull CharSequence pattern) {
        return say(pattern.toString());
    }

    public static @NonNull
    LexString say(@NonNull String pattern) {
        checkState();
        return new LexString(pattern);
    }

    private static void checkState() {
        if(app == null) {
            throw new LexNotInitializedException();
        }
    }

    public static class LexString {

        protected CharSequence text;

        private LexString(CharSequence text) {
            this.text = text;
        }

        public @NonNull
        ParsedLexString with(@NonNull LexKey key, @NonNull CharSequence value) {
            if(key == null) {
                throw new LexInvalidKeyException("Attempt to use null value as a LexKey.");
            } else if(value == null) {
                throw new LexInvalidValueException(String.format("Attempt to use null value for LexKey: %s", key));
            }
            return applyKey(key.name(), value.toString(), false);
        }

        public @NonNull
        ParsedLexString with(@NonNull LexKey key, @StringRes int resourceId) {
            return with(key, app.getString(resourceId));
        }

        protected ParsedLexString applyKey(@NonNull String key, @NonNull String value, boolean isOptional) {
            final CharSequence result = TextUtils.replace(text,
                    new String[]{OPEN_DELIMITER + key + CLOSE_DELIMITER},
                    new String[]{value});
            if (!isOptional && TextUtils.equals(text, result)) {
                throw new LexInvalidKeyException(String.format("Non-optional LexKey missing in pattern string: %s", key));
            }
            return new ParsedLexString(result);
        }
    }

    public static class ParsedLexString extends LexString {

        private ParsedLexString(CharSequence text) {
            super(text);
        }

        public void to(@NonNull TextView textView) {
            textView.setText(text);
        }

        public @NonNull CharSequence make() {
            return text;
        }

        public @NonNull String makeString() {
            return text.toString();
        }
    }

    public interface LexKey {
        String name();
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
