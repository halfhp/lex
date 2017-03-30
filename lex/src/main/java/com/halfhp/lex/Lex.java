package com.halfhp.lex;

import android.app.Application;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.halfhp.lex.exception.LexAlreadyInitializedException;
import com.halfhp.lex.exception.LexNotInitializedException;

import java.util.List;

public final class Lex {

    protected static Resources resources;

    /**
     * Initializes Lex.  Must be called before any other Lex methods are invoked and should be called
     * ONLY from {@link Application#onCreate()}.
     * @param app
     */
    public static void init(@NonNull Application app) {
        init(app, LexContext.OPEN_DELIMITER, LexContext.CLOSE_DELIMITER);
    }

    /**
     * Initializes Lex with custom delimiters.  Must be called before any other Lex methods are invoked and should be called
     * ONLY from {@link Application#onCreate()}.
     * @param app
     * @param openDelimiter
     * @param closeDelimiter
     */
    public static void init(@NonNull Application app, @NonNull String openDelimiter, @NonNull String closeDelimiter) {
        if(Lex.resources != null) {
            throw new LexAlreadyInitializedException();
        }

        Lex.resources = app.getResources();
        LexContext.setDefaultDelimiters(openDelimiter, closeDelimiter);
    }

    /**
     * Begin building a string using the specified String resource as the template.
     * @param resourceId The StringRes to use as the template
     * @return
     */
    @NonNull
    public static UnparsedLexString say(@StringRes int resourceId) {
        checkState();
        return say(resources.getString(resourceId));
    }

    /**
     * Begin building a string using the specified CharSequence as the template.
     * @param template The CharSequence to serve as the template.
     * @return
     */
    @NonNull
    public static UnparsedLexString say(@NonNull CharSequence template) {
        checkState();
        return new UnparsedLexString(template);
    }

    /**
     * Begin building a formatted list using the specified List<CharSequence>.
     * @param items
     * @return
     */
    @NonNull
    public static LexList list(@NonNull List<? extends CharSequence> items) {
        return list(items.toArray(new CharSequence[]{}));
    }

    /**
     * Begin building a formatted list using one or more CharSequence.
     * @param text
     * @return
     */
    @NonNull
    public static LexList list(@NonNull CharSequence... text) {
        return new LexList(text);
    }

    private static void checkState() {
        if(resources == null) {
            throw new LexNotInitializedException();
        }
    }

}
