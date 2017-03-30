package com.halfhp.lex.demo;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import com.halfhp.lex.Lex;
import com.squareup.phrase.Phrase;

/**
 * Simple activity that does nothing but benchmark string template inflations using
 * Phrase and Lex.  The results are logged to the console.
 */
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();

    private static final int NUM_ITERATIONS = 25000;

    Thread testThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "Starting benchmarks...");

        testThread = new Thread(new Runnable() {
            @Override
            public void run() {


                benchmark(new Runnable() {
                    @Override
                    public void run() {
                        Lex.say(R.string.medium_template)
                                .with(LexKey.item_one, "i1")
                                .with(LexKey.item_two, "i2")
                                .with(LexKey.item_three, "i3")
                                .with(LexKey.item_four, "i4").make();
                    }
                }, NUM_ITERATIONS, "Lex-medium");

                benchmark(new Runnable() {
                    @Override
                    public void run() {
                        Phrase.from(MainActivity.this, R.string.medium_template)
                                .put("item_one", "i1")
                                .put("item_two", "i2")
                                .put("item_three", "i3")
                                .put("item_four", "i4").format();
                    }
                }, NUM_ITERATIONS, "Phrase-medium");

                benchmark(new Runnable() {
                    @Override
                    public void run() {
                        CharSequence cs = Lex.say(R.string.short_template)
                                .with(LexKey.size, "small").make();
                    }
                }, NUM_ITERATIONS, "Lex-short");

                benchmark(new Runnable() {
                    @Override
                    public void run() {
                        CharSequence cs = Phrase.from(MainActivity.this, R.string.short_template)
                                .put("size", "small").format();
                    }
                }, NUM_ITERATIONS, "Phrase-short");

            }
        });
        testThread.start();
    }

    void benchmark(final Runnable runnable, final int numIterations, final String name) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numIterations; i++) {
            runnable.run();
        }
        final long ellapsed = System.currentTimeMillis() - startTime;
        Log.i(TAG, String.format("%s took: %d milliseconds to run %d iterations.", name, ellapsed, numIterations));
    }

}
