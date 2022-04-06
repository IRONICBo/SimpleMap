package edu.hhu.lvbo.SimpleMap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import android.view.MenuItem.OnMenuItemClickListener;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.text.NumberFormat;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMenuItemClickListener {
    private Context context;
    private SharedPreferences sp;
    private BaiduMap baiduMap;
    private RadioGroup maptypeRadioGroup;
    private CheckBox trafficCheckBox;
    private Handler handler;
    private MapView mapView = null;
    private String account = null;
    private final String FGF = ",_,";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏actionbar
//        Objects.requireNonNull(getSupportActionBar()).setTitle();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_map);
        init();
        setListener();
    }

    private void init() {
        Intent intendGetData = getIntent();
        account = (String) intendGetData.getSerializableExtra("account");
        System.out.println(account); // 用户名

        context = MapActivity.this;
        sp = context.getSharedPreferences("baidumap", Context.MODE_PRIVATE);
        mapView = (MapView) findViewById(R.id.mapView);
        //取消放大缩小键
        mapView.showZoomControls(false);
        baiduMap = mapView.getMap();
        toNewAddress(baiduMap, 118.78, 32.07);
        maptypeRadioGroup = (RadioGroup) findViewById(R.id.maptypeRadioGroup);
        trafficCheckBox = (CheckBox) findViewById(R.id.trafficCheckBox);
        handler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                switch (msg.what) {
                    case 1:
                        showToast(context, msg.obj.toString());
                        break;
                }
            }
        };
    }

    private void showDialog(String msg) {
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(MapActivity.this)
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

    private void setListener() {
        maptypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.normalRadioButton) {
                    baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    Toast.makeText(context, "普通地图", Toast.LENGTH_LONG).show();
                }
                if (checkedId == R.id.satelliteRadioButton) {
                    baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    Toast.makeText(context, "卫星地图", Toast.LENGTH_LONG).show();
                }
            }
        });

        trafficCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean flag) {
                if (flag) {
                    //开启交通图
                    baiduMap.setTrafficEnabled(true);
                } else {
                    //关闭交通图
                    baiduMap.setTrafficEnabled(false);
                }
            }
        });
    }

    //添加菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //groupId,itemId,orderId,名称
        menu.add(1, 1, 1, "经纬度定位").setOnMenuItemClickListener((MenuItem.OnMenuItemClickListener) this);
        menu.add(2, 2, 2, "城市定位").setOnMenuItemClickListener((MenuItem.OnMenuItemClickListener) this);
        menu.add(3, 3, 3, "公里数计算").setOnMenuItemClickListener((MenuItem.OnMenuItemClickListener) this);
        menu.add(4, 4, 4, "当前用户信息").setOnMenuItemClickListener((MenuItem.OnMenuItemClickListener) this);
        menu.add(5, 5, 5, "清除屏幕").setOnMenuItemClickListener((MenuItem.OnMenuItemClickListener) this);
        menu.add(6, 6, 6, "退出").setOnMenuItemClickListener((MenuItem.OnMenuItemClickListener) this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setIcon(R.drawable.icon);
        switch (itemId) {
            case 1:
                final View locationView = View.inflate(MapActivity.this, R.layout.dialog_location, null);
                builder.setTitle("经纬度定位");
                builder.setView(locationView);
                final EditText longitudeEditText = (EditText) locationView.findViewById(R.id.longitudeEditText);
                final EditText latitudeEditText = (EditText) locationView.findViewById(R.id.latitudeEditText);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String longitudeString = longitudeEditText.getText().toString().trim();
                        if (longitudeString.trim().length() == 0) {
                            showToast(context, "经度不能为空!");
                            return;
                        }
                        String latitudeString = latitudeEditText.getText().toString().trim();
                        if (latitudeString.trim().length() == 0) {
                            showToast(context, "纬度不能为空!");
                            return;
                        }
                        double longitude = Double.parseDouble(longitudeString);
                        if (longitude < -180 || longitude > 180) {
                            showToast(context, "经度的范围在-180~180之间!");
                            return;
                        }
                        double latitude = Double.parseDouble(latitudeString);
                        if (latitude < -90 || latitude > 90) {
                            showToast(context, "纬度的范围在-90~90之间!");
                            return;
                        }
                        // 定义Maker坐标点
                        pointOverlay(baiduMap, longitude, latitude);
                        // 将地图移动过去
                        toNewAddress(baiduMap, longitude, latitude);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create();
                builder.show();
                break;
            case 2:
                final View cityView = View.inflate(MapActivity.this, R.layout.dialog_city, null);
                builder.setTitle("城市定位");
                builder.setView(cityView);
                final EditText cityEditText = (EditText) cityView.findViewById(R.id.cityEditText);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String city = cityEditText.getText().toString().trim();
                        double longitude = 0.0;
                        double latitude = 0.0;
                        // 默认给南京、北京、上海、深圳
                        if (city.equals("南京") || city.equals("nanjing")) {
                            longitude = 118.8921;
                            latitude = 31.32751;
                        } else if (city.equals("北京") || city.equals("beijing")) {
                            longitude = 116.23128;
                            latitude = 40.22077;
                        } else if (city.equals("盐城") || city.equals("yancheng")) {
                            longitude = 120.50102;
                            latitude = 33.20107;
                        } else {
                            Toast.makeText(context, "数据更新中...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // 定义Maker坐标点
                        pointOverlay(baiduMap, longitude, latitude);
                        // 将地图移动过去
                        toNewAddress(baiduMap, longitude, latitude);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create();
                builder.show();
                break;
            case 3:
                final View distanceView = View.inflate(MapActivity.this, R.layout.dialog_distance, null);
                builder.setTitle("公里数计算");
                builder.setView(distanceView);
                final EditText fromLongitudeEditText = (EditText) distanceView.findViewById(R.id.fromLongitudeEditText);
                final EditText fromLatitudeEditText = (EditText) distanceView.findViewById(R.id.fromLatitudeEditText);
                final EditText toLongitudeEditText = (EditText) distanceView.findViewById(R.id.toLongitudeEditText);
                final EditText toLatitudeEditText = (EditText) distanceView.findViewById(R.id.toLatitudeEditText);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String fromLongitudeString = fromLongitudeEditText.getText().toString().trim();
                        String toLongitudeString = toLongitudeEditText.getText().toString().trim();
                        if (fromLongitudeString.length() == 0 && toLongitudeString.length() == 0) {
                            showToast(context, "经度不能为空!");
                            return;
                        }
                        String fromLatitudeString = fromLatitudeEditText.getText().toString().trim();
                        String toLatitudeString = toLatitudeEditText.getText().toString().trim();
                        if (fromLatitudeString.length() == 0 && toLatitudeString.length() == 0) {
                            showToast(context, "纬度不能为空!");
                            return;
                        }
                        double fromLongitude = Double.parseDouble(fromLongitudeString);
                        double toLongitude = Double.parseDouble(toLongitudeString);
                        if ((fromLongitude < -180 || fromLongitude > 180)
                                && (toLongitude < -180 || toLongitude > 180)) {
                            showToast(context, "经度的范围在-180~180之间!");
                            return;
                        }
                        double fromLatitude = Double.parseDouble(fromLatitudeString);
                        double toLatitude = Double.parseDouble(toLatitudeString);
                        if ((fromLatitude < -90 || fromLatitude > 90)
                                && (toLatitude < -90 || toLatitude > 90)) {
                            showToast(context, "纬度的范围在-90~90之间!");
                            return;
                        }

                        LatLng fromPoint = new LatLng(fromLatitude, fromLongitude);
                        LatLng toPoint = new LatLng(toLatitude, toLongitude);
                        double distance = DistanceUtil.getDistance(fromPoint, toPoint);
                        double kmDistance = distance / 1000.0;

                        // 不使用科学记数法
                        NumberFormat nf = NumberFormat.getInstance();
                        nf.setGroupingUsed(false);
                        nf.setMaximumFractionDigits(2); // 两位小数

                        System.out.println(fromLatitudeString);
                        System.out.println(toLatitudeString);
                        System.out.println(fromLongitudeString);
                        System.out.println(toLongitudeString);
                        System.out.println(fromPoint);
                        System.out.println(toPoint);
                        System.out.println(nf.format(kmDistance));
                        showToast(context, "距离：" + nf.format(kmDistance) + "km");
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create();
                builder.show();
                break;
            case 4:
                String string = sp.getString(account, "");
//                String value = password + FGF + name + FGF + gender + FGF + phone + FGF + email + FGF + birthday + FGF + birthPlace + FGF + interest + FGF + introduction;

                String[] info = string.split(FGF);
                String password = info[0];
                String name = info[1];
                String gender = info[2];
                String phone = info[3];
                String email = info[4];
                String birthday = info[5];
                String birthPlace = info[6];
                String interest = info[7];
                String introduction = info[8];

                showDialog("姓名：" + name + "\n" +
                        "密码：" + password + "\n" +
                        "性别：" + gender + "\n" +
                        "手机：" + phone + "\n" +
                        "邮箱：" + email + "\n" +
                        "生日：" + birthday + "\n" +
                        "籍贯：" + birthPlace + "\n" +
                        "兴趣：" + interest + "\n" +
                        "自我介绍：" + introduction);
                System.out.println(string);
                break;
            case 5:
                baiduMap.clear();
                break;
            case 6:
                builder.setTitle("退出提醒");
                builder.setMessage("你确认退出吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MapActivity.this.finish();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create();
                builder.show();
                break;
            default:
                break;
        }
        return true;
    }

    private void showToast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 将地图的中心点移动到指定点
     *
     * @param baiduMap  百度地图对象
     * @param longitude 经度
     * @param latitude  纬度
     */
    private void toNewAddress(BaiduMap baiduMap, double longitude, double latitude) {
        //设定中心点坐标
        LatLng cenpt = new LatLng(latitude, longitude);
        //定义地图状态
        MapStatus mapStatus = new MapStatus.Builder().target(cenpt).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        //改变地图状态
        baiduMap.setMapStatus(mapStatusUpdate);
    }

    /**
     * 绘制点标记，并将新的点标记添加到地图中
     *
     * @param baiduMap  百度地图对象
     * @param longitude 经度
     * @param latitude  纬度
     */
    private void pointOverlay(BaiduMap baiduMap, double longitude, double latitude) {
        //定义Maker坐标点
        LatLng point = new LatLng(latitude, longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.mark);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        //在地图上添加Marker，并显示
        baiduMap.addOverlay(option);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exist();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    //退出
    private void exist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setIcon(R.drawable.icon);
        builder.setTitle("退出提醒");
        builder.setMessage("你确认退出吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                MapActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create();
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }
}