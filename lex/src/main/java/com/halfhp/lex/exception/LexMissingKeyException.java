package com.halfhp.lex.exception;

import android.support.annotation.NonNull;

public class LexMissingKeyException extends RuntimeException {
    public LexMissingKeyException(@NonNull String message) {
        super(message);
    }
}
