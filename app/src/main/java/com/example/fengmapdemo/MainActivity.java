package com.example.fengmapdemo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.fengmapdemo.utils.FMLocationAPI;
import com.fengmap.android.FMDevice;
import com.fengmap.android.FMErrorMsg;
import com.fengmap.android.FMMapSDK;
import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.exception.FMObjectException;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapUpgradeInfo;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.FMPickMapCoordResult;
import com.fengmap.android.map.FMViewMode;
import com.fengmap.android.map.animator.FMLinearInterpolator;
import com.fengmap.android.map.event.OnFMCompassListener;
import com.fengmap.android.map.event.OnFMMapClickListener;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.event.OnFMNodeListener;
import com.fengmap.android.map.event.OnFMSwitchGroupListener;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.layer.FMFacilityLayer;
import com.fengmap.android.map.layer.FMImageLayer;
import com.fengmap.android.map.layer.FMModelLayer;
import com.fengmap.android.map.marker.FMModel;
import com.fengmap.android.map.marker.FMNode;
import com.fengmap.android.widget.FM3DControllerButton;
import com.fengmap.android.widget.FMFloorControllerComponent;
import com.fengmap.android.widget.FMSwitchFloorComponent;
import com.fengmap.android.widget.FMZoomComponent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity implements OnFMMapInitListener, OnFMCompassListener,
                                OnFMMapClickListener{

    static FMMap mFMMap;
    String bid = "1495596552216612865";
    String mapName = "复旦大学江湾校区 A 教学楼";
    private FMMapView mapView;
    //定位按钮
    FloatingActionButton btnMyLocation;
    //底部信息栏
    TextView textViewBottomMessage;
    //2D/3D 地图切换工具
    private FM3DControllerButton m3DTextButton;
    //楼层控制组件
//    private FMFloorControllerComponent mFloorComponent;
//    //楼层组件切换标记
//    private boolean isAnimateEnd = true;
//    //楼层切换控件
//    private FMSwitchFloorComponent mSwitchFloorComponent;
    //地图缩放控制组件
    private FMZoomComponent mZoomComponent;
//    //公共设施图层
//    private FMFacilityLayer mFacilityLayer;
//    //模型图层
//    private FMModelLayer mModelLayer;
//    //初始楼层
//    private int mGroupId = 1;
//    //点击的模型
//    private FMModel mClickdModel;
//    //图片图层
//    private FMImageLayer mImageLayer;
    //底端消息窗
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //初始化蜂鸟地图 SDK
        FMMapSDK.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化ToolBar控件
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //配置actionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        //点击定位Button
        btnMyLocation = (FloatingActionButton) findViewById(R.id.btn_my_location);
        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 定位
                textViewBottomMessage.setText("正在定位");
            }
        });

        textViewBottomMessage = (TextView) findViewById(R.id.txt_bottom_message);
        String message = "等待定位";
        textViewBottomMessage.setText(message);

        openMapById();

    }

    @Override
    public void onBackPressed() {
        if(mFMMap != null) {
            mFMMap.onDestroy();
            mFMMap = null;
        }
        super.onBackPressed();
        this.finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    private void openMapById() {
        mapView = (FMMapView) findViewById(R.id.mapview);
        mFMMap = mapView.getFMMap();
        mFMMap.openMapById(bid, true);
        //监听地图的加载状况
        mFMMap.setOnFMMapInitListener(this);
        //监听地图点击事件
        mFMMap.setOnFMMapClickListener(this);
    }

    @Override
    public void onMapInitSuccess(String path) {
        //加载主题
//        mFMMap.loadThemeById("1495651325510901762");
        //2D/3D 控制
        init3DControllerComponent();
        //地图缩放控件
        initZoomComponent();
        //设置指北针点击事件
        mFMMap.setOnFMCompassListener(this);
        //显示指南针
        mFMMap.showCompass();
    }

    @Override
    public void onMapInitFailure(String s, int errorCode) {
        Toast.makeText(this, "加载地图失败，请检查网络设置."+FMErrorMsg.getErrorMsg(errorCode), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onUpgrade(FMMapUpgradeInfo fmMapUpgradeInfo) {
        return false;
    }

    //加载 2D/3D 切换控件
    private void init3DControllerComponent() {
        m3DTextButton = new FM3DControllerButton(this);
        //设置初始状态为3D(true),设置为false为2D模式
        m3DTextButton.initState(true);
        m3DTextButton.measure(0, 0);
        int width = m3DTextButton.getMeasuredWidth();
        //设置3D控件位置
        mapView.addComponent(m3DTextButton, FMDevice.getDeviceWidth() - 10 - width, 50);
        //2、3D点击监听
        m3DTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FM3DControllerButton button = (FM3DControllerButton) v;
                if (button.isSelected()) {
                    button.setSelected(false);
                    mFMMap.setFMViewMode(FMViewMode.FMVIEW_MODE_2D);
                } else {
                    button.setSelected(true);
                    mFMMap.setFMViewMode(FMViewMode.FMVIEW_MODE_3D);
                }
            }
        });
    }

    @Override
    public void onCompassClick() {
        mFMMap.resetCompassToNorth();
    }

    @Override
    public void onMapClick(float x, float y) {
        FMPickMapCoordResult mapCoordResult = mFMMap.pickMapCoord(x, y);

        double pX = x;
        double pY = y;
        if (mapCoordResult != null) {
            FMMapCoord mapCoord = mapCoordResult.getMapCoord();
            pX = mapCoord.x;
            pY = mapCoord.y;
        }
//        if (snackbar != null) {
//            snackbar.dismiss();
//        }

        String content = ""+ pX + "," + pY;
        Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
        //ViewHelper.setViewText(MainActivity.this, R.id.map_result, content);
    }

    //初始化缩放控件
    private void initZoomComponent() {
        mZoomComponent = new FMZoomComponent(MainActivity.this);
        mZoomComponent.measure(0, 0);
        int width = mZoomComponent.getMeasuredWidth();
        int height = mZoomComponent.getMeasuredHeight();
        //缩放控件位置
        int offsetX = FMDevice.getDeviceWidth() - width - 10;
        int offsetY = FMDevice.getDeviceHeight() - 400 - height;
        mapView.addComponent(mZoomComponent, offsetX, offsetY);

        mZoomComponent.setOnFMZoomComponentListener(new FMZoomComponent.OnFMZoomComponentListener() {
            @Override
            public void onZoomIn(View view) {
                //地图放大
                mFMMap.zoomIn();
            }

            @Override
            public void onZoomOut(View view) {
                //地图缩小
                mFMMap.zoomOut();
            }
        });
    }

    //模型点击事件

}