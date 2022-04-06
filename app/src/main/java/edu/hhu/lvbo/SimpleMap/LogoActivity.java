package edu.hhu.lvbo.SimpleMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class LogoActivity extends AppCompatActivity {

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                //跳转到登陆页
                //定义意图对象
                Intent intent = new Intent(LogoActivity.this, LoginActivity.class);
                //执行行动
                startActivity(intent);
                //关闭当前页面
                finish();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏actionbar
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_logo);
        // 延时
        handler.sendEmptyMessageDelayed(1, 3000);
    }
}
