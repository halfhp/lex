package com.halfhp.lex;

import android.support.annotation.NonNull;

public class UnparsedLexString extends LexString {

    UnparsedLexString(@NonNull CharSequence templateText) {
        super(new LexContext(templateText));
    }

    @NonNull
    public LexString delimitBy(@NonNull String openDelimiter, @NonNull String closeDelimiter) {
        context.setDelimiters(openDelimiter, closeDelimiter);
        return this;
    }
}
