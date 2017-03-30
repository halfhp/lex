package com.halfhp.lex.exception;

/**
 * Created by nick on 7/27/17.
 */
public class LexAlreadyInitializedException extends RuntimeException {
    public LexAlreadyInitializedException() {
        super("Lex has already been initialized! (Attempt into invoke " +
                "Lex.init(...) more than once.");
    }
}
