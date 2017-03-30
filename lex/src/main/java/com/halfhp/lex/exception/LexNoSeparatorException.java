package com.halfhp.lex.exception;

public class LexNoSeparatorException extends RuntimeException {
    public LexNoSeparatorException() {
        super("A separator must be supplied when formatting a list.");
    }
}
