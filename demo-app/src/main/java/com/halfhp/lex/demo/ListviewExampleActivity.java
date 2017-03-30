package com.halfhp.lex.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListviewExampleActivity extends Activity {

    @BindView(R.id.listview)
    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview_example);
        ButterKnife.bind(this);

        listView.setAdapter(new MyListViewAdapter(100));
    }


    private static class MyListViewAdapter extends BaseAdapter {

        private BookList[] items;

        public MyListViewAdapter(int size) {
            super();
            items = new BookList[size];

            for(int i = 0; i < items.length; i++) {
                items[i] = new BookList();
            }
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public BookList getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = new MyListItemView(parent.getContext());
            }
            ((MyListItemView) convertView).bind(getItem(position));
            return convertView;
        }
    }

}
