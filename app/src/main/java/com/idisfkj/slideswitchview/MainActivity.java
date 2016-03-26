package com.idisfkj.slideswitchview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.idisfkj.slideswitchview.view.SlideSwitchView;

public class MainActivity extends AppCompatActivity {

    private SlideSwitchView mSlideSwitchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSlideSwitchView = (SlideSwitchView) findViewById(R.id.slide_switch);
//        mSlideSwitchView.setState(false);
//        mSlideSwitchView.setShape(2);
//        mSlideSwitchView.setSlideable(false);
        mSlideSwitchView.setSlideSwitchListener(new SlideSwitchView.SlideSwitchListener() {
            @Override
            public void onOpen() {
                Toast.makeText(MainActivity.this,"打开",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClose() {
                Toast.makeText(MainActivity.this,"关闭",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
