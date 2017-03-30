package com.halfhp.lex.exception;

import android.support.annotation.NonNull;

/**
 * Created by nick on 7/27/17.
 */
public class LexInvalidValueException extends RuntimeException {
    public LexInvalidValueException(@NonNull String message) {
        super(message);
    }
}
