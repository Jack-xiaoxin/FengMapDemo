package com.example.fengmapdemo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fengmapdemo.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.fengmapdemo.location.Destination;
import com.example.fengmapdemo.location.MapCoord;
import com.example.fengmapdemo.utils.ConvertUtils;
import com.example.fengmapdemo.utils.FMLocationAPI;
import com.example.fengmapdemo.utils.SnackbarUtil;
import com.example.fengmapdemo.utils.ViewHelper;
import com.fengmap.android.FMDevice;
import com.fengmap.android.FMErrorMsg;
import com.fengmap.android.FMMapSDK;
import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.analysis.navi.FMNaviResult;
import com.fengmap.android.exception.FMObjectException;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapUpgradeInfo;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.FMPickMapCoordResult;
import com.fengmap.android.map.FMViewMode;
import com.fengmap.android.map.animator.FMLinearInterpolator;
import com.fengmap.android.map.animator.FMValueAnimation;
import com.fengmap.android.map.event.OnFMCompassListener;
import com.fengmap.android.map.event.OnFMMapClickListener;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.event.OnFMNodeListener;
import com.fengmap.android.map.event.OnFMSwitchGroupListener;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.layer.FMFacilityLayer;
import com.fengmap.android.map.layer.FMImageLayer;
import com.fengmap.android.map.layer.FMLayerProxy;
import com.fengmap.android.map.layer.FMLineLayer;
import com.fengmap.android.map.layer.FMLocationLayer;
import com.fengmap.android.map.layer.FMModelLayer;
import com.fengmap.android.map.marker.FMFacility;
import com.fengmap.android.map.marker.FMImageMarker;
import com.fengmap.android.map.marker.FMLineMarker;
import com.fengmap.android.map.marker.FMLocationMarker;
import com.fengmap.android.map.marker.FMModel;
import com.fengmap.android.map.marker.FMNode;
import com.fengmap.android.map.marker.FMSegment;
import com.fengmap.android.utils.FMMath;
import com.fengmap.android.widget.FM3DControllerButton;
import com.fengmap.android.widget.FMFloorControllerComponent;
import com.fengmap.android.widget.FMSwitchFloorComponent;
import com.fengmap.android.widget.FMZoomComponent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnFMMapInitListener, OnFMCompassListener,
                                OnFMSwitchGroupListener, OnFMMapClickListener, Runnable,
                                FMLocationAPI.OnFMLocationListener{

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
    private FMFloorControllerComponent mFloorComponent;
    //楼层组件切换标记
    private boolean isAnimateEnd = true;
    //楼层切换控件
    private FMSwitchFloorComponent mSwitchFloorComponent;
    //地图缩放控制组件
    private FMZoomComponent mZoomComponent;
    //公共设施图层
    private FMFacilityLayer mFacilityLayer;
    //模型图层
    private FMModelLayer mModelLayer;
    //初始楼层
    private int mGroupId = 1;
    //点击的模型
    private FMModel mClickedModel;
    //图片图层
    private FMImageLayer mImageLayer;
    //底端消息窗
    Snackbar snackbar;
    /**
     * 定位图层
     */
    private FMLocationLayer mLocationLayer;
    /**
     * 定位标记
     */
    private FMLocationMarker mLocationMarker;
    /**
     * 线图层
     */
    protected FMLineLayer mLineLayer;
    /**
     * 导航分析
     */
    protected FMNaviAnalyser mNaviAnalyser;
    /**
     * 路线提示信息
     */
    protected String message = "";
    /**
     * 起点图层
     */
    protected FMImageLayer stImageLayer;
    /**
     * 终点图层
     */
    protected FMImageLayer endImageLayer;

    /**
     * 定位切换楼层
     */
    protected static final int WHAT_LOCATE_SWITCH_GROUP = 4;
    /**
     * 两个点相差最大距离20米
     */
    protected static final double MAX_BETWEEN_LENGTH = 20;
    /**
     * 进入地图显示级别
     */
    protected static final int MAP_NORMAL_LEVEL = 20;
    /**
     * 默认起点
     */
    protected MapCoord stCoord = new MapCoord(1, new FMMapCoord(12961647.576796599, 4861814.63807118));
    /**
     * 默认终点
     */
    protected MapCoord endCoord = new MapCoord(6, new FMMapCoord(12961699.79823795, 4861826.46384646));
    /**
     * 导航行走点集合
     */
    protected ArrayList<ArrayList<FMMapCoord>> mNaviPoints = new ArrayList<>();
    /**
     * 导航行走的楼层集合
     */
    protected ArrayList<Integer> mNaviGroupIds = new ArrayList<>();
    /**
     * 导航行走索引
     */
    protected int mCurrentIndex = 0;
    /**
     * 差值动画
     */
    protected FMLocationAPI mLocationAPI;

    private FMValueAnimation mMoveAnimation;

    /**
     * 行走显示详情
     */
    private static final int WHAT_WALKING_ROUTE_LINE = 3;
    /**
     * 约束过的定位标注
     */
    private FMLocationMarker mHandledMarker;
    /**
     * 上一次行走坐标
     */
    private FMMapCoord mLastMoveCoord;
    /**
     * 是否为第一人称
     */
    private boolean mIsFirstView = true;
    /**
     * 是否为跟随状态
     */
    private boolean mHasFollowed = true;
    /**
     * 总共距离
     */
    private double mTotalDistance;
    /**
     * 剩余距离
     */
    private volatile double mLeftDistance;


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
        //楼层切换
        if(mFloorComponent == null) {
            initFloorControllerComponent();
        }
        if (mSwitchFloorComponent == null) {
            //initSwitchFloorComponent();
            initFloorControllerComponent();
        }

        int groupId = mFMMap.getFocusGroupId();

        //公共设施图层
        mFacilityLayer = mFMMap.getFMLayerProxy().getFMFacilityLayer(groupId);
        mFacilityLayer.setOnFMNodeListener(mOnFacilityClickListener);
        mFMMap.addLayer(mFacilityLayer);

        //模型图层
        mModelLayer = mFMMap.getFMLayerProxy().getFMModelLayer(groupId);
        mModelLayer.setOnFMNodeListener(mOnModelCLickListener);
        mFMMap.addLayer(mModelLayer);

        //图片图层
        mImageLayer = mFMMap.getFMLayerProxy().getFMImageLayer(mFMMap.getFocusGroupId());
        mFMMap.addLayer(mImageLayer);

        //获取定位图层
        mLocationLayer = mFMMap.getFMLayerProxy().getFMLocationLayer();
        mFMMap.addLayer(mLocationLayer);

        //线图层
        mLineLayer = mFMMap.getFMLayerProxy().getFMLineLayer();
        mFMMap.addLayer(mLineLayer);

        //导航分析
        try {
            mNaviAnalyser = FMNaviAnalyser.getFMNaviAnalyserById(bid);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (FMObjectException e) {
            e.printStackTrace();
        }

        //差值动画
        mLocationAPI = new FMLocationAPI();
        mLocationAPI.setFMLocationListener(this);

        //路径规划
        //analyzeNavigation(stCoord, endCoord);
//        analyzeNavigation();
//        mTotalDistance = mNaviAnalyser.getSceneRouteLength();

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

    /**
     * 楼层切换控件初始化
     */
    private void initFloorControllerComponent() {
        // 楼层切换
        mFloorComponent = new FMFloorControllerComponent(this);
        mFloorComponent.setMaxItemCount(4);
        //楼层切换事件监听
        mFloorComponent.setOnFMFloorControllerComponentListener(new FMFloorControllerComponent.OnFMFloorControllerComponentListener() {
            @Override
            public void onSwitchFloorMode(View view, FMFloorControllerComponent.FMFloorMode currentMode) {
                if (currentMode == FMFloorControllerComponent.FMFloorMode.SINGLE) {
                    setSingleDisplay();
                } else {
                    setMultiDisplay();
                }
            }

            @Override
            public boolean onItemSelected(int groupId, String floorName) {
                if (isAnimateEnd) {
                    switchFloor(groupId);
                    return true;
                }
                return false;
            }
        });
        //设置为单层模式
        mFloorComponent.setFloorMode(FMFloorControllerComponent.FMFloorMode.SINGLE);
        int groupId = 1;
        mFloorComponent.setFloorDataFromFMMapInfo(mFMMap.getFMMapInfo(), groupId);

        int offsetX = (int) (FMDevice.getDeviceDensity() * 5);
        int offsetY = (int) (FMDevice.getDeviceDensity() * 130);
        mapView.addComponent(mFloorComponent, offsetX, offsetY);
    }

    /**
     * 单层显示模式
     */
    void setSingleDisplay() {
        int[] gids = {mFMMap.getFocusGroupId()};       //获取当前地图焦点层id
        mFMMap.setMultiDisplay(gids, 0, this);
    }

    /**
     * 多层显示模式
     */
    void setMultiDisplay() {
        int[] gids = mFMMap.getMapGroupIds();    //获取地图所有的group
        FMFloorControllerComponent.FloorData fd = mFloorComponent.getFloorData(mFloorComponent.getSelectedPosition());
        int focus = 0;
        for (int i = 0; i < gids.length; i++) {
            if (gids[i] == fd.getGroupId()) {
                focus = i;
                break;
            }
        }
        mFMMap.setMultiDisplay(gids, focus, this);
    }

    void switchFloor(int groupId) {
        mFMMap.setFocusByGroupIdAnimated(groupId, new FMLinearInterpolator(), this);

        //切换楼层Id
        mGroupId = groupId;

        //清空标记
        mImageLayer.removeAll();

        /**
         * 切换各个图层
         */
        //公共设施图层
        mFacilityLayer = mFMMap.getFMLayerProxy().getFMFacilityLayer(groupId);
        mFacilityLayer.setOnFMNodeListener(mOnFacilityClickListener);
        mFMMap.addLayer(mFacilityLayer);

        //模型图层
        mModelLayer = mFMMap.getFMLayerProxy().getFMModelLayer(groupId);
        mModelLayer.setOnFMNodeListener(mOnModelCLickListener);
        mFMMap.addLayer(mModelLayer);

        //图片图层
        mImageLayer = mFMMap.getFMLayerProxy().getFMImageLayer(mFMMap.getFocusGroupId());
        mFMMap.addLayer(mImageLayer);

        //获取定位图层
        mLocationLayer = mFMMap.getFMLayerProxy().getFMLocationLayer();
        mFMMap.addLayer(mLocationLayer);

        //线图层
        mLineLayer = mFMMap.getFMLayerProxy().getFMLineLayer();
        mFMMap.addLayer(mLineLayer);

        //清空标记
        mImageLayer.removeAll();

        //导航分析
        try {
            mNaviAnalyser = FMNaviAnalyser.getFMNaviAnalyserById(bid);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (FMObjectException e) {
            e.printStackTrace();
        }

        //修改定位点标注
        if (mGroupId != Location.mapCoord.getGroupId()) {
            //切换到非定位楼层，清除定位标注
            clearLocationMarker();
        } else {
            //切换到（回）定位楼层，更新定位标注
            updateLocationMarker();
        }

        Log.d("切换楼层成功", "switchFloor: ");
    }

    /**
     * 组切换开始之前。
     */
    @Override
    public void beforeGroupChanged() {
        //isAnimateEnd = false;
    }

    /**
     * 组切换结束之后。
     */
    @Override
    public void afterGroupChanged() {
        isAnimateEnd = true;
        handler.sendEmptyMessage(WHAT_LOCATE_SWITCH_GROUP);
    }

    /**
     * 公共设施点击事件
     */
    private OnFMNodeListener mOnFacilityClickListener = new OnFMNodeListener() {
        @Override
        public boolean onClick(FMNode node) {

            FMFacility facility = (FMFacility) node;
            final FMMapCoord centerMapCoord = facility.getPosition();

            //显示标记
            showMarket(centerMapCoord);

            //配置snackbar
            CoordinatorLayout mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            snackbar = Snackbar.make(mCoordinatorLayout, "公共设施", Snackbar.LENGTH_INDEFINITE);
            SnackbarUtil.setBackgroundColor(snackbar, SnackbarUtil.blue);
            SnackbarUtil.SnackbarAddView(snackbar, R.layout.snackbar, 0);
            View view = snackbar.getView();
            Button go_there = (Button) view.findViewById(R.id.go);

            //"去这里"按钮的点击事件
            go_there.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Destination.name = "公共设施";
                    //去这里事件
                    goThere(centerMapCoord);
                }
            });
            //显示snackbar
            snackbar.show();
            return true;
        }

        @Override
        public boolean onLongPress(FMNode fmNode) {
            return false;
        }
    };

    /**
     * 显示标记
     *
     * @param centerMapCoord 标记位置
     * @author jin
     */
    public void showMarket(FMMapCoord centerMapCoord) {
        if (centerMapCoord != null && mImageLayer == null) {
            //添加图片标注
            FMImageMarker imageMarker = ViewHelper.buildImageMarker(getResources(),
                    centerMapCoord, R.drawable.ic_marker_blue);
            mImageLayer.addMarker(imageMarker);
        } else if (centerMapCoord != null && mImageLayer != null) {
            //移除现有标记
            mImageLayer.removeAll();
            //添加图片标注
            FMImageMarker imageMarker = ViewHelper.buildImageMarker(getResources(),
                    centerMapCoord, R.drawable.ic_marker_blue);
            mImageLayer.addMarker(imageMarker);
        }
    }

    /**
     * 模型点击事件
     */
    private OnFMNodeListener mOnModelCLickListener = new OnFMNodeListener() {
        @Override
        public boolean onClick(FMNode node) {

            if (mClickedModel != null) {
                mClickedModel.setSelected(false);
            }
            final FMModel model = (FMModel) node;
            //Log.d("FMModel", "onClick: Id: "+model.getDataType());
            if (model.getDataType() == 300000) {
                //Log.d("FMModel", "onClick: is Click id300000");
                return true;
            }

            mClickedModel = model;

            model.setSelected(true);
            mFMMap.updateMap();
            final FMMapCoord centerMapCoord = model.getCenterMapCoord();

            //显示标记
            showMarket(centerMapCoord);

            //建立SnackBar提示用户点击的地图信息
            CoordinatorLayout mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            String name = mClickedModel.getName();
            if (name.length() == 0) {
                name = "未命名区域";
            }

            //配置snackbar
            snackbar = Snackbar.make(mCoordinatorLayout, name, Snackbar.LENGTH_INDEFINITE);
            SnackbarUtil.setBackgroundColor(snackbar, SnackbarUtil.blue);
            SnackbarUtil.SnackbarAddView(snackbar, R.layout.snackbar, 0);
            View view = snackbar.getView();
            Button go_there = (Button) view.findViewById(R.id.go);

            //"去这里"按钮的点击事件
            go_there.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Destination.name = model.getName();
                    //去这里事件
                    goThere(centerMapCoord);
                }
            });

            //显示snackbar
            snackbar.show();
            return true;
        }

        @Override
        public boolean onLongPress(FMNode fmNode) {
            return false;
        }
    };

    /**
     * 去这里事件
     *
     * @param centerMapCoord
     */
    void goThere(FMMapCoord centerMapCoord) {
        if (Location.mapCoord.getGroupId() == 0) {
            //未进行定位
            Toast.makeText(MainActivity.this, "请先确定你的位置", Toast.LENGTH_SHORT).show();
            return;
        }

        //配置终点信息
        Destination.mapCoord = new MapCoord(mGroupId, centerMapCoord);

        //配置路线规划终点信息
        endCoord = Destination.mapCoord;

        //清除所有的线与图层
        clear();

        //添加定位标记
        locationMarker();

        //添加终点标记
        createEndImageMarker();

        //添加起点标记
        createStartImageMarker();

        //开始分析导航
        analyzeNavigation();
        mTotalDistance = mNaviAnalyser.getSceneRouteLength();

        Log.d("gohere", "goThere: " + Destination.mapCoord.getGroupId());

        // 画完置空
        stCoord = null;
        endCoord = null;
    }

    /**
     * 清理所有的线与图层
     */
    protected void clear() {
        clearLocationMarker();
        clearLineLayer();
        clearStartImageLayer();
        clearEndImageLayer();
    }

    /**
     * 清除线图层
     */
    protected void clearLineLayer() {
        if (mLineLayer != null) {
            mLineLayer.removeAll();
        }
    }

    /**
     * 清除起点图层
     */
    protected void clearStartImageLayer() {
        if (stImageLayer != null) {
            stImageLayer.removeAll();
            mFMMap.removeLayer(stImageLayer); // 移除图层
            stImageLayer = null;
        }
    }

    /**
     * 清除终点图层
     */
    protected void clearEndImageLayer() {
        if (endImageLayer != null) {
            endImageLayer.removeAll();
            mFMMap.removeLayer(endImageLayer); // 移除图层

            endImageLayer = null;
        }
    }

    /**
     * 清除定位标注
     */
    private void clearLocationMarker() {
        String TAG = "定位点";
        if (mLocationLayer != null) {
            Log.d(TAG, "clearLocationMarker: clear");
            mLocationLayer.removeAll();

            mLocationMarker = null;
        }
    }

    /**
     * 添加/更新定位点标注
     *
     * @return
     * @author jin
     * DATA:2017/7/31
     */
    private boolean locationMarker() {

        String TAG = "LocationMarker";

//        Location.groupId = 1;
//        Location.myLocation.x = 1.296164E7;
//        Location.myLocation.y = 4861845.0;

        if (Location.mapCoord.getGroupId() != 0 && Location.mapCoord.getMapCoord().x != 0 &&
                Location.mapCoord.getMapCoord().y != 0) {

            Log.d(TAG, "locationMarker: " + Location.mapCoord.getGroupId());
            //清空现有
            clear();
            //切换地图
            switchFloor(Location.mapCoord.getGroupId());
            //更新楼层控制组件
            updateFloorButton(Location.mapCoord.getGroupId());
            //刷新定位点
            updateLocationMarker();

            stCoord = Location.mapCoord;
            //createStartImageMarker();
        }
        return true;
    }

    /**
     * 更新定位点位置
     */
    private void updateLocationMarker() {

//        boolean visible = mLocationAPI.getGroupId() == mGroupId;
//        mHandledMarker.setVisible(visible);

        //FMMapCoord CENTER_COORD = new FMMapCoord(1.296164E7, 4861845.0);
        String TAG = "定位点";
        Log.d(TAG, "updateLocationMarker: begin");
        if (mLocationMarker == null) {
            Log.d(TAG, "updateLocationMarker: add");
            int groupId = mFMMap.getFocusGroupId();
            Log.d(TAG, "updateLocationMarker: groupId=" + groupId);
            mLocationMarker = new FMLocationMarker(groupId, Location.mapCoord.getMapCoord());
            //设置定位点图片
            mLocationMarker.setActiveImageFromAssets("active.png");
            //设置定位图片宽高
            mLocationMarker.setMarkerWidth(90);
            mLocationMarker.setMarkerHeight(90);
            mLocationLayer.addMarker(mLocationMarker);
            moveToCenter(Location.mapCoord.getMapCoord());
        } else {
            //更新定位点位置和方向
            Log.d(TAG, "updateLocationMarker: update");
            float angle = 0;
            mLocationMarker.updateAngleAndPosition(angle, Location.mapCoord.getMapCoord());
            moveToCenter(Location.mapCoord.getMapCoord());
        }
    }

    /**
     * 移动至中心点,如果中心与屏幕中心点距离大于20米，将移动
     *
     * @param mapCoord 坐标
     */
    protected void moveToCenter(final FMMapCoord mapCoord) {
        FMMapCoord centerCoord = mFMMap.getMapCenter();
        double length = FMMath.length(centerCoord, mapCoord);
        if (length > MAX_BETWEEN_LENGTH) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mFMMap.moveToCenter(mapCoord, true);
                }
            });
        }
    }

    /**
     * 异步消息处理机制，定义处理消息的对象
     */
    public Handler handler = new Handler() {
        /**
         * 处理消息
         * @param msg
         */
        public void handleMessage(Message msg) {
            switch (msg.what) {
            }
        }
    };

    /**
     * 修改楼层控件
     */
    void updateFloorButton(int groupId) {
        String TAG = "切换楼层控件";
        Log.d(TAG, "handleMessage: GroupId" + groupId);
        FMFloorControllerComponent.FloorData[] mFloorDatas = mFloorComponent.getFloorDatas();
        for (int i = 0; i < mFloorDatas.length; i++) {
            if (mFloorDatas[i].getGroupId() == groupId) {
                Log.d(TAG, "changeFloorButton: ====");
                if (i != mFloorComponent.getSelectedPosition()) {
                    mFloorComponent.setSelected(i);
                    invokeFloorComponentNotify(mFloorComponent);
                }
                break;
            }
        }
    }

    void invokeFloorComponentNotify(FMFloorControllerComponent floorComponent) {
        Class clazz = FMFloorControllerComponent.class;
        try {
            Method method = clazz.getDeclaredMethod("updateData");
            method.setAccessible(true);
            method.invoke(floorComponent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建起点
     */
    protected void createStartImageMarker() {
        clearStartImageLayer();
        // 添加起点图层
        stImageLayer = mFMMap.getFMLayerProxy().getFMImageLayer(mGroupId);
//        mFMMap.addLayer(stImageLayer);
        // 标注物样式
        FMImageMarker imageMarker = ViewHelper.buildImageMarker(getResources(), stCoord.getMapCoord(), R.drawable.start);
        stImageLayer.addMarker(imageMarker);
    }

    @Override
    public void onAnimationStart() {

    }

    @Override
    public void onAnimationUpdate(FMMapCoord mapCoord, double distance, double angle) {
        updateHandledMarker(mapCoord, angle);
        scheduleCalcWalkingRouteLine(mapCoord, distance);
    }

    @Override
    public void onAnimationEnd() {
//        // 已经行走过终点
//        if (isWalkComplete()) {
//            setStartAnimationEnable(true);
//            return;
//        }

        if (isWalkComplete()) {
            setStartAnimationEnable(true);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String info = getResources().getString(R.string.label_walk_format, 0f,
                            0, "到达目的地");
                    //Toast.makeText(MainActivity.this,"到达目的地",Toast.LENGTH_SHORT).show();
//                    mKqwSpeechCompound.speaking("到达目的地，导航结束");
                    textViewBottomMessage.setText("到达目的地，导航结束");
                    //Log.d("Message", "info: "+"到达目的地");
                    //ViewHelper.setViewText(FMNavigationApplication.this, R.id.txt_info, info);
                }
            });
            return;
        }

        int focusGroupId = getWillWalkingGroupId();
        //跳转至下一层
        setFocusGroupId(focusGroupId);
    }

    /**
     * 判断是否行走到终点
     *
     * @return
     */
    protected boolean isWalkComplete() {
        if (mCurrentIndex > mNaviGroupIds.size() - 1) {
            return true;
        }
        return false;
    }

    /**
     * 计算行走距离
     *
     * @param mapCoord 定位点
     * @param distance 已行走距离
     */
    private void scheduleCalcWalkingRouteLine(FMMapCoord mapCoord, double distance) {
        mLeftDistance -= distance;
        if (mLeftDistance <= 0) {
            mLeftDistance = 0;
        }

        Message message = Message.obtain();
        message.what = WHAT_WALKING_ROUTE_LINE;
        message.obj = mapCoord;
        handler.sendMessage(message);
    }

    /**
     * 更新处理过定位点
     *
     * @param coord 坐标
     * @param angle 角度
     */
    private void updateHandledMarker(FMMapCoord coord, double angle) {
        if (mHandledMarker == null) {
            mHandledMarker = ViewHelper.buildLocationMarker(mFMMap.getFocusGroupId(),
                    coord);
            mLocationLayer.addMarker(mHandledMarker);
        } else {
            FMMapCoord mapCoord = makeConstraint(coord);
            mHandledMarker.updateAngleAndPosition((float) angle, mapCoord);

            if (angle != 0) {
                animateRotate((float) -angle);
            }
        }

        //updateLocationMarker();

        //上次真实行走坐标
        mLastMoveCoord = coord.clone();
        moveToCenter(mLastMoveCoord);
    }

    /**
     * 动画旋转
     */
    protected void animateRotate(final float angle) {
        if (Math.abs(mFMMap.getRotateAngle() - angle) > 2) {
            mFMMap.setRotateAngle(angle);
        }
    }

    /**
     * 路径约束
     *
     * @param mapCoord 地图坐标点
     * @return
     */
    private FMMapCoord makeConstraint(FMMapCoord mapCoord) {
        FMMapCoord currentCoord = mapCoord.clone();
        int groupId = mLocationAPI.getGroupId();
        //获取当层绘制路径线点集合
        ArrayList<FMMapCoord> coords = mLocationAPI.getSimulateCoords();
        //路径约束
        mNaviAnalyser.naviConstraint(groupId, coords, mLastMoveCoord, currentCoord);
        return currentCoord;
    }

    /**
     * 创建终点图层
     */
    protected void createEndImageMarker() {
        clearEndImageLayer();
        // 添加起点图层
        endImageLayer = mFMMap.getFMLayerProxy().getFMImageLayer(mGroupId);
        mImageLayer.removeAll();
        // 标注物样式
        FMImageMarker imageMarker = ViewHelper.buildImageMarker(getResources(), endCoord.getMapCoord(), R.drawable.end);
        endImageLayer.addMarker(imageMarker);
    }

    /**
     * 开始分析导航
     */
    private void analyzeNavigation() {
        mNaviAnalyser.getNaviResults().clear();
        int type = mNaviAnalyser.analyzeNavi(stCoord.getGroupId(), stCoord.getMapCoord(),
                endCoord.getGroupId(), endCoord.getMapCoord(),
                FMNaviAnalyser.FMNaviModule.MODULE_SHORTEST);

        if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) {

            addLineMarker();

            fillWithPoints();

            //行走总距离
            double sceneRouteLength = mNaviAnalyser.getSceneRouteLength();
            setSceneRouteLength(sceneRouteLength);
        }
    }

    /**
     * 添加线标注
     */
    protected void addLineMarker() {
        ArrayList<FMNaviResult> results = mNaviAnalyser.getNaviResults();

        for(FMNaviResult r:results){
            Log.d("PathData", "addLineMarker: "+"GroupId: "+r.getGroupId()+"坐标: "+r.getPointList());
        }

        // 填充导航数据
        ArrayList<FMSegment> segments = new ArrayList<>();
        for (FMNaviResult r : results) {
            int groupId = r.getGroupId();
//            if(groupId==2){
//                continue;
//            }
            FMSegment s = new FMSegment(groupId, r.getPointList());
            segments.add(s);
        }
        //添加LineMarker
        FMLineMarker lineMarker = new FMLineMarker(segments);
        lineMarker.setLineWidth(3f);
        mLineLayer.addMarker(lineMarker);
    }

    /**
     * 填充导航线段点
     */
    protected void fillWithPoints() {
        clearWalkPoints();

        //获取路径规划上点集合数据
        ArrayList<FMNaviResult> results = mNaviAnalyser.getNaviResults();
        int focusGroupId = Integer.MIN_VALUE;
        for (FMNaviResult r : results) {
            int groupId = r.getGroupId();
            ArrayList<FMMapCoord> points = r.getPointList();
            //点数据小于2，则为单个数据集合
            if (points.size() < 2) {
                continue;
            }
            //判断是否为同层导航数据，非同层数据即其他层数据
            if (focusGroupId == Integer.MIN_VALUE || focusGroupId != groupId) {
                focusGroupId = groupId;
                //添加即将行走的楼层与点集合
                mNaviGroupIds.add(groupId);
                mNaviPoints.add(points);
            } else {
                mNaviPoints.get(mNaviPoints.size() - 1).addAll(points);
            }
        }
    }

    /**
     * 清空行走的点集合数据
     */
    private void clearWalkPoints() {
        mCurrentIndex = 0;
        mNaviPoints.clear();
        mNaviGroupIds.clear();
    }

    /**
     * 格式化距离
     *
     * @param sceneRouteLength 行走总距离
     */
    private void setSceneRouteLength(double sceneRouteLength) {
        int time = ConvertUtils.getTimeByWalk(sceneRouteLength);
        String loadMessage = "距离：" + (int) sceneRouteLength + "米  " + "大约需要" + time + "分钟";
        textViewBottomMessage.setText(loadMessage);

        String text = "前往：" + Destination.name;
//        TextView textView = ViewHelper.getView(FMNavigationDistance.this, R.id.txt_info);
//        textView.setText(text);

        //配置snackbar+
        CoordinatorLayout mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        snackbar = Snackbar.make(mCoordinatorLayout, text, Snackbar.LENGTH_INDEFINITE);
        SnackbarUtil.setBackgroundColor(snackbar, SnackbarUtil.blue);
        SnackbarUtil.SnackbarAddView(snackbar, R.layout.snackbar, 0);
        View view = snackbar.getView();
        final Button go_there = (Button) view.findViewById(R.id.go);
        go_there.setText("开始导航");

        //"开始导航"按钮的点击事件
        go_there.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "开始导航", Toast.LENGTH_SHORT).show();
//                mKqwSpeechCompound.speaking("导航开始！");
                startWalkingRouteLine();
                clearLocationMarker();
                snackbar.dismiss();
            }
        });

        //显示sanckbar
        snackbar.show();
    }

    /**
     * 开始点击导航
     */
    public void startWalkingRouteLine() {

        mLeftDistance = mTotalDistance;

        //行走索引初始为0
        mCurrentIndex = 0;
        setStartAnimationEnable(false);

        //缩放地图状态
        setZoomLevel();
        //开始进行模拟行走
        int groupId = getWillWalkingGroupId();
        setFocusGroupId(groupId);
    }

    /**
     * 设置动画按钮是否可以使用
     *
     * @param enable true 可以执行, false 不可以执行
     */
    protected void setStartAnimationEnable(final boolean enable) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //ViewHelper.setViewEnable(BaseActivity.this, R.id.btn_start_navigation, enable);
            }
        });
    }

    /**
     * 设置缩放动画
     *
     * @return
     */
    protected void setZoomLevel() {
        if (mFMMap.getZoomLevel() != MAP_NORMAL_LEVEL) {
            mFMMap.setZoomLevel(MAP_NORMAL_LEVEL, true);
        }
    }

    /**
     * 获取即将行走的下一层groupId
     *
     * @return
     */
    protected int getWillWalkingGroupId() {
        if (mCurrentIndex > mNaviGroupIds.size() - 1) {
            return mFMMap.getFocusGroupId();
        } else {
            return mNaviGroupIds.get(mCurrentIndex);
        }
    }

    /**
     * 切换楼层行走
     *
     * @param groupId 楼层id
     */
    protected void setFocusGroupId(int groupId) {
        if (groupId != mFMMap.getFocusGroupId()) {
            mFMMap.setFocusByGroupId(groupId, null);
            handler.sendEmptyMessage(WHAT_LOCATE_SWITCH_GROUP);

        }

        setupTargetLine(groupId);
    }

    /**
     * 开始模拟行走路线
     *
     * @param groupId 楼层id
     */
    protected void setupTargetLine(int groupId) {
        ArrayList<FMMapCoord> points = getWillWalkingPoints();
        mLocationAPI.setupTargetLine(points, groupId);
        mLocationAPI.start();
    }

    /**
     * 获取即将行走的下一层点集合
     *
     * @return
     */
    protected ArrayList<FMMapCoord> getWillWalkingPoints() {
        if (mCurrentIndex > mNaviGroupIds.size() - 1) {
            return null;
        }
        return mNaviPoints.get(mCurrentIndex++);
    }

    /**
     * 延时函数
     */
    @Override
    public void run() {
        try {
            Thread.sleep(4000);//睡一段时间
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handler.sendEmptyMessage(1);//睡醒来了，传送消息，扫描完成
    }
}