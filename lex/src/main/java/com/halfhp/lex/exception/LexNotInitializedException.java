package com.halfhp.lex.exception;

/**
 * Created by nick on 7/27/17.
 */
public class LexNotInitializedException extends RuntimeException {
    public LexNotInitializedException() {
        super("Lex has not been initialized!  Make a call into Lex.init() " +
                "appears at the top of your Application's onCreate() method.");
    }
}
