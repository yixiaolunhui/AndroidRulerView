package com.dalong.androidrulerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.dalong.rulerview.RulerView;

public class MainActivity extends AppCompatActivity {

    private RulerView ruler,ruler2,ruler3;
    private EditText edit,edit2,edit3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ruler=(RulerView)findViewById(R.id.ruler);
        edit=(EditText)findViewById(R.id.edit);
        ruler2=(RulerView)findViewById(R.id.ruler2);
        edit2=(EditText)findViewById(R.id.edit2);
        ruler3=(RulerView)findViewById(R.id.ruler3);
        edit3=(EditText)findViewById(R.id.edit3);
        ruler.setVisibility(View.GONE);
        ruler3.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);
        edit3.setVisibility(View.GONE);
        ruler.setScrollingListener(new RulerView.OnRulerViewScrollListener<String>() {
            @Override
            public void onChanged(RulerView rulerView, String oldValue, String newValue) {
                edit.setText(String.valueOf(newValue));
            }

            @Override
            public void onScrollingStarted(RulerView rulerView) {

            }

            @Override
            public void onScrollingFinished(RulerView rulerView) {

            }
        });
        ruler2.setScrollingListener(new RulerView.OnRulerViewScrollListener<String>() {
            @Override
            public void onChanged(RulerView rulerView, String oldValue, String newValue) {
                edit2.setText(String.valueOf(newValue));
            }

            @Override
            public void onScrollingStarted(RulerView rulerView) {

            }

            @Override
            public void onScrollingFinished(RulerView rulerView) {

            }
        });
        ruler3.setScrollingListener(new RulerView.OnRulerViewScrollListener<String>() {
            @Override
            public void onChanged(RulerView rulerView, String oldValue, String newValue) {
                edit3.setText(String.valueOf(newValue));
            }

            @Override
            public void onScrollingStarted(RulerView rulerView) {

            }

            @Override
            public void onScrollingFinished(RulerView rulerView) {

            }
        });
    }
}
