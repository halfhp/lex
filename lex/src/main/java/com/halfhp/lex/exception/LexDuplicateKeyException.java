package com.halfhp.lex.exception;

import android.support.annotation.NonNull;

public class LexDuplicateKeyException extends RuntimeException {
    public LexDuplicateKeyException(@NonNull String key) {
        super(String.format("Duplicate Key detected: %s", key));
    }
}
