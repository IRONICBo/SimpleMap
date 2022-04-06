package edu.hhu.lvbo.SimpleMap;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

/**
 * 使用AppCompatActivity
 * <p>
 * 1.界面上带有toolbar
 * 2.theme主题只能用android:theme=，不能使用android:style
 * <p>
 * 观察者模式
 * 事件源： 发生各种事件的对象
 * 事件：   事件源所发生的具体动作
 * 监听器：
 */
public class LoginActivity extends AppCompatActivity {
    private EditText accountEditText; // 账号
    private EditText passwordEditText; // 密码
    private CheckBox rememberAccountCheckBox;
    private CheckBox rememberPasswordCheckBox;
    private Button loginButton; // 登陆按钮
    private Button registerButton;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    // 数据的分隔符
    private final String FGF = ",_,";

    /**
     * dialog消息提示封装
     *
     * @param msg
     */
    private void showMessage(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                .setTitle("提示")
                .setMessage(msg)
                .setIcon(R.drawable.icon)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
        alertDialog.show();
    }

    /**
     * toast消息封装
     *
     * @param msg
     */
    private void showToast(String msg) {
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏actionbar
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);
        init(); // 初始化组建
        addAction(); // 添加监听器
    }

    private void addAction() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = accountEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (account.length() == 0) {
                    showMessage("账户名不能为空");
                    accountEditText.setText("");
                    return;
                }
                if (password.length() == 0) {
                    showMessage("密码不能为空");
                    passwordEditText.setText("");
                    return;
                }
                // 默认value为""
                String accountFromSP = sp.getString(account, "");
                if (accountFromSP.equals("")) {
                    showMessage("账号不存在!");
                    accountEditText.setText("");
                    return;
                }
                // password, name, gender, phone, email, birth, birthplace, interest, text
                String passwordFromSP = accountFromSP.split(FGF)[0];
                System.out.println("password : " + accountFromSP);
                System.out.println("input : " + password);
                if (!password.equals(passwordFromSP)) {
                    showMessage("密码错误!");
                    passwordEditText.setText(password);
                    return;
                }

                String rememberStatus = "0,0";
                if (!rememberAccountCheckBox.isChecked() && !rememberPasswordCheckBox.isChecked()) {
                    rememberStatus = "0,0";
                }
                if (rememberAccountCheckBox.isChecked() && !rememberPasswordCheckBox.isChecked()) {
                    rememberStatus = "1,0";
                }
                if (rememberAccountCheckBox.isChecked() && rememberPasswordCheckBox.isChecked()) {
                    rememberStatus = "1,1";
                }
                editor.putString(account + "RememberStatus", rememberStatus);
                editor.putString("currentAccount", account);
                editor.commit(); // 立刻刷入内存

                showToast("登录成功~");
                Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                intent.putExtra("account", account); // 携带参数
                startActivity(intent);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        rememberAccountCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compont, boolean flag) {
                rememberAccountCheckBox.setChecked(flag);
            }
        });

        rememberPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compont, boolean flag) {
                rememberPasswordCheckBox.setChecked(flag);
            }
        });
    }

    private void init() {
        accountEditText = (EditText) findViewById(R.id.accountEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);
        rememberAccountCheckBox = (CheckBox) findViewById(R.id.rememberAccountCheckBox);
        rememberPasswordCheckBox = (CheckBox) findViewById(R.id.rememberPasswordCheckBox);
        sp = getSharedPreferences("baidumap", Context.MODE_PRIVATE);
        editor = sp.edit();

        // 默认用户密码
        String currentAccount = sp.getString("currentAccount", "123");
        String rememberStatus = sp.getString(currentAccount + "RememberStatus", "0,0");
        if (!currentAccount.equals("") && !rememberStatus.equals("") && rememberStatus.length() >= 3) {
            String rememberAccountStatus = rememberStatus.split(",")[0];
            String rememberPasswordStatus = rememberStatus.split(",")[1];
            if (rememberAccountStatus.equals("1")) {
                accountEditText.setText(currentAccount);
                rememberAccountCheckBox.setChecked(true);
            }
            if (rememberPasswordStatus.equals("1")) {
                String password = sp.getString(currentAccount, "").split(FGF)[0];
                passwordEditText.setText(password);
                rememberPasswordCheckBox.setChecked(true);
            }
        }
    }
}
