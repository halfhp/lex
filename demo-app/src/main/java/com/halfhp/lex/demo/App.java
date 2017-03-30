package com.halfhp.lex.demo;

import android.app.Application;

import com.halfhp.lex.Lex;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Lex.init(this);
    }
}
