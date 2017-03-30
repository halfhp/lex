package com.halfhp.lex.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.listview_example_button)
    void onListExampleClicked() {
        startActivity(new Intent(this, ListviewExampleActivity.class));
    }

    @OnClick(R.id.benchmark_button)
    void onBenchmarkClicked() {
        startActivity(new Intent(this, BenchmarkActivity.class));
    }
}
