package com.yuanmeng.cqboy.graduationview;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.widget.TextView;

import com.yuanmeng.cqboy.graduationview.view.BaseScaleView;
import com.yuanmeng.cqboy.graduationview.view.HorizontalScaleView;
import com.yuanmeng.cqboy.graduationview.view.RoundScaleView;
import com.yuanmeng.cqboy.graduationview.view.VerticalScaleView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 界面数据初始化
        RoundScaleView ll_info_weight = (RoundScaleView) findViewById(R.id.ll_info_weight);
        ll_info_weight.setOnScaleChangeListener(weightListener);
        ll_info_weight.setScaleValue(60);
        VerticalScaleView ll_info_height = (VerticalScaleView) findViewById(R.id.ll_info_height);
        ll_info_height.setOnScrollListener(heightListener);
        ll_info_height.setScaleValue(170);
        HorizontalScaleView ll_info_age = (HorizontalScaleView) findViewById(R.id.ll_info_age);
        ll_info_age.setOnScrollListener(ageListener);
        ll_info_age.setScaleValue(26);
    }

    private BaseScaleView.OnScrollListener ageListener = new BaseScaleView.OnScrollListener() {
        @Override
        public void onScaleScroll(int scale) {
//            Log.d("TAG", "onScaleScroll: age=" + scale);
        }
    };

    private BaseScaleView.OnScrollListener heightListener = new BaseScaleView.OnScrollListener() {
        @Override
        public void onScaleScroll(int scale) {
//            Log.d("TAG", "onScaleScroll: height=" + scale);
        }
    };

    private RoundScaleView.OnScaleChangeListener weightListener = new RoundScaleView.OnScaleChangeListener() {
        @Override
        public void onScaleChange(int scale) {

//            Log.d("TAG", "onScaleChange: weight=" + scale);
            TextView tv_info_height = (TextView) findViewById(R.id.tv_info_weight);
            tv_info_height.setText(scale + "KG");
            SpannableStringBuilder spanBuilder = new SpannableStringBuilder(
                    tv_info_height.getText().toString());
            TextAppearanceSpan span = new TextAppearanceSpan(null, 0,
                    (int) (15 * getResources().getDisplayMetrics().density),
                    ColorStateList.valueOf(0xff999999), null);
            spanBuilder.setSpan(span, tv_info_height.length() - 2,
                    tv_info_height.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            tv_info_height.setText(spanBuilder);
        }
    };
}
