package edu.hhu.lvbo.SimpleMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText accountEditText;
    private EditText passwordEditText;
    private EditText repasswordEditText;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText birthdayEditText;
    private Spinner provinceSpinner;
    private TextView middleLineTextView;
    private Spinner citySpinner;
    private EditText interestEditText;
    private EditText introductionEditText;
    private Button registerButton;
    private Button cancelButton;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private int provinceIndex = 0;
    private int cityIndex = -1;
    private final String[] interestArray = new String[]{"唱歌", "跳舞", "篮球", "足球", "旅游", "美食", "电影", "游戏"};
    private boolean[] initInterestChooseArray = new boolean[8];

    // 负责取数据
    private SharedPreferences sp;
    // 负责保存数据
    private Editor editor;
    // 数据的分隔符
    private final String FGF = ",_,";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏actionbar
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_register);
        init();
        addAction();
    }

    // 邮箱
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^\\w+([-+.]\\w+)*@[a-zA-Z0-9]+([-.][a-zA-Z0-9]+)*\\.[a-zA-Z0-9]+([-.][a-zA-Z0-9]+)*$");

    public static boolean checkEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // 手机号
//    private static final Pattern PHONE_NUMBER = Pattern.compile("^\\+?([0-9]+[ -]?)+[0-9]$");
    private static final Pattern PHONE_NUMBER = Pattern.compile("^1\\d{10}$");

    public static boolean checkPhoneNumber(String phoneNumber) {
        return PHONE_NUMBER.matcher(phoneNumber).matches();
    }

    /**
     * dialog消息提示封装
     *
     * @param msg
     */
    private void showDialog(String msg) {
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(RegisterActivity.this)
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

    private void showDialog(String msg, Intent intent) {
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(RegisterActivity.this)
                .setTitle("提示")
                .setMessage(msg)
                .setIcon(R.drawable.icon)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(intent);
                        finish();
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
    private void showMessage(String msg) {
        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        accountEditText = (EditText) findViewById(R.id.accountEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        repasswordEditText = (EditText) findViewById(R.id.repasswordEditText);
        genderRadioGroup = (RadioGroup) findViewById(R.id.genderRedioGroup);
        maleRadioButton = (RadioButton) findViewById(R.id.maleRadioButton);
        femaleRadioButton = (RadioButton) findViewById(R.id.femaleRadioButton);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        birthdayEditText = (EditText) findViewById(R.id.birthdayEditText);
        provinceSpinner = (Spinner) findViewById(R.id.provinceSpinner);
        middleLineTextView = (TextView) findViewById(R.id.middleLineTextView);
        citySpinner = (Spinner) findViewById(R.id.citySpinner);
        interestEditText = (EditText) findViewById(R.id.interestEditText);
        introductionEditText = (EditText) findViewById(R.id.introductionEditText);
        registerButton = (Button) findViewById(R.id.registerButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        sp = getSharedPreferences("baidumap", Context.MODE_PRIVATE);
        editor = sp.edit();
        ArrayAdapter<String> provinceAdapter =
                new ArrayAdapter<String>(RegisterActivity.this,
                        android.R.layout.simple_spinner_item,
                        ProvinceCityUtil.PROVINCE_ARRAY);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(provinceAdapter);
    }

    private void addAction() {
        // 用户名校验
        accountEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // 失去焦点校验
                    String account = accountEditText.getText().toString().trim();
                    String accountFromSP = sp.getString(account, "");
                    if (!accountFromSP.equals("")) {
                        // 不为空则说明存在
                        showDialog("用户已存在～");
                        accountEditText.setText("");
                    }
                }
            }
        });

        // 密码校验
        repasswordEditText.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                // 失去焦点校验
                String password = passwordEditText.getText().toString().trim();
                String repassword = repasswordEditText.getText().toString().trim();

                if (!password.equals(repassword)) {
                    // 密码不一致，提醒
                    showDialog("两次密码不一致～");
                    passwordEditText.setText("");
                    repasswordEditText.setText("");
                }
            }
        });

        phoneEditText.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                String phone = phoneEditText.getText().toString();
                if (!checkPhoneNumber(phone)) {
                    showDialog("手机号码格式不正确～");
                    phoneEditText.setText("");
                }
            }
        });

        emailEditText.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                String email = emailEditText.getText().toString();
                if (!checkEmail(email)) {
                    showDialog("邮箱验证不正确～");
                    emailEditText.setText("");
                }
            }
        });

        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int monthOfYear = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                // 更换主题
                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(RegisterActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker arg0, int y, int m, int d) {
                                        // 月份从0开始计算
                                        c.set(y, m, d);
                                        Date now = new Date();
                                        Date after = c.getTime();
                                        if (after.after(now)) {
                                            showDialog("出生日期不正确～");
                                            birthdayEditText.setText("");
                                        } else {
                                            // 日期合法
                                            birthdayEditText.setText(sdf.format(c.getTime()));
                                        }
                                    }
                                }, year, monthOfYear, dayOfMonth);
                datePickerDialog.show();
            }
        });

//        birthdayEditText.setOnFocusChangeListener((view, b) -> {
//            if (!b) {
//                // 验证日期
//                Date now = new Date();
//                String birth = birthdayEditText.getText().toString().trim();
//                String[] split = birth.split("-");
//                Date select = new Date(Integer.parseInt(split[0]), Integer.parseInt(split[1]) + 1, Integer.parseInt(split[2]));
//                showDialog(now.toString() + " " + select.toString());
//                if (select.after(now)) {
//                    showDialog("出生日期不正确～");
//                    birthdayEditText.setText("");
//                }
//            }
//        });

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0,
                                       View arg1, int position, long arg3) {
                provinceIndex = position;
                String[] cityArray = ProvinceCityUtil.CITY_ARRAY[provinceIndex];
                if (position == 0 || position == 1 || position == 2 || position == 3 ||
                        position == 31 || position == 32) {
                    middleLineTextView.setVisibility(View.INVISIBLE);
                    citySpinner.setVisibility(View.INVISIBLE);
                    cityIndex = -1;
                } else {
                    middleLineTextView.setVisibility(View.VISIBLE);
                    citySpinner.setVisibility(View.VISIBLE);
                    cityIndex = 0;
                    ArrayAdapter<String> cityAdapter =
                            new ArrayAdapter<String>(RegisterActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    cityArray);
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(cityAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                cityIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        interestEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //1.根据上下文对象创建一个简单对话框的创建器
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                //2.设置该对话框的一些信息
                builder.setIcon(R.drawable.icon);
                builder.setTitle("请选择你的兴趣爱好");
                //3.设置对话框的消息内容
                builder.setMultiChoiceItems(interestArray, initInterestChooseArray, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int index, boolean flag) {
                        initInterestChooseArray[index] = flag;
                    }
                });
                //4.为"确定"和"取消"按钮绑定事件监听
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String result = "";
                        for (int i = 0; i < initInterestChooseArray.length; i++) {
                            if (initInterestChooseArray[i]) {
                                result += interestArray[i] + " ";
                            }
                        }
                        if (!result.equals("")) {
                            interestEditText.setText(result.substring(0, result.length() - 1));
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                //5.创建对话框
                builder.create();
                //6.显示对话框
                builder.show();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String name = nameEditText.getText().toString().trim();
                String account = accountEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String repassword = repasswordEditText.getText().toString().trim();
                String gender = "男";
                if (femaleRadioButton.isChecked()) {
                    gender = "女";
                }
                String phone = phoneEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String birthday = birthdayEditText.getText().toString().trim();
                String birthPlace = ProvinceCityUtil.PROVINCE_ARRAY[provinceIndex];
                if (cityIndex != -1) {
                    birthPlace += "-" + ProvinceCityUtil.CITY_ARRAY[provinceIndex][cityIndex];
                }
                String interest = interestEditText.getText().toString().trim();
                String introduction = introductionEditText.getText().toString().trim();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

                showDialog("姓名：" + name + "\n" +
                        "密码：" + password + "\n" +
                        "重复密码：" + repassword + "\n" +
                        "性别：" + gender + "\n" +
                        "手机：" + phone + "\n" +
                        "邮箱：" + email + "\n" +
                        "生日：" + birthday + "\n" +
                        "籍贯：" + birthPlace + "\n" +
                        "兴趣：" + interest + "\n" +
                        "自我介绍：" + introduction, intent);

                String key = account;
                String value = password + FGF + name + FGF + gender + FGF + phone + FGF + email + FGF + birthday + FGF + birthPlace + FGF + interest + FGF + introduction;
                editor.putString(key, value);
                editor.putString(key + "RememberStatus", "0,0"); //
                editor.commit();
                showMessage("注册成功!");
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}