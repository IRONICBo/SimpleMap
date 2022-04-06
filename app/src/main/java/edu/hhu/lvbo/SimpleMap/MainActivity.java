package edu.hhu.lvbo.SimpleMap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.baidu.mapapi.map.MapView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置内容视图
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide(); // 删除actionbar
        Intent intent = new Intent(this, LogoActivity.class);
        startActivity(intent);
        finish(); // 关闭当前页面
    }
}