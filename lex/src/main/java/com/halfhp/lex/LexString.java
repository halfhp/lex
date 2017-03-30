package com.halfhp.lex;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;

import com.halfhp.lex.exception.LexInvalidKeyException;
import com.halfhp.lex.exception.LexInvalidValueException;

import java.text.NumberFormat;

class LexString {

    protected final LexContext context;

    LexString(@NonNull LexContext context) {
        this.context = context;
    }

    @NonNull
    public ParsedLexString with(@NonNull final LexKey key, @NonNull final CharSequence value) {
        if (key == null) {
            throw new LexInvalidKeyException("Attempt into use null value as a Key.");
        } else if (value == null) {
            throw new LexInvalidValueException(String.format("Attempt into use null value for Key: %s%s%s",
                    context.openDelimiter, key, context.closeDelimiter));
        }
        return applyKey(key.name(), value.toString(), false);
    }

    @NonNull
    public ParsedLexString with(@NonNull LexKey key, @StringRes int resourceId) {
        return with(key, Lex.resources.getString(resourceId));
    }

    @NonNull
    public ParsedLexString withPlural(@NonNull LexKey key, int quantity, @PluralsRes int resourceId) {
        return with(key, Lex.resources.getQuantityString(resourceId, quantity));
    }

    @NonNull
    public ParsedLexString withNumber(@NonNull LexKey key, @NonNull Number number) {
        return withNumber(key, number, null);
    }

    @NonNull
    public ParsedLexString withNumber(@NonNull LexKey key, @NonNull Number number, @Nullable NumberFormat format) {
        return with(key, format != null ? format.format(number) : number.toString());
    }

    @NonNull
    protected ParsedLexString applyKey(@NonNull final String key, @NonNull final String value, final boolean isOptional) {

        if (!context.lastTransform.isEmpty()) {
            context.lastTransform.apply(context);
        }

        context.lastTransform.store(key, value, isOptional);
        return getParsedLexString(context);
    }

    @NonNull
    protected ParsedLexString getParsedLexString(@NonNull LexContext context) {
        return new ParsedLexString(context);
    }
}
