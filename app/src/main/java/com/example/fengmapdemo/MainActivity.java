package com.example.fengmapdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.fengmap.android.FMMapSDK;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapView;

public class MainActivity extends AppCompatActivity {

    FMMap mFMMap;
    private String bid = "1495596552216612865";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //初始化蜂鸟地图 SDK
        FMMapSDK.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    private void openMapById() {
        FMMapView mapView = (FMMapView) findViewById(R.id.mapview);
        mFMMap = mapView.getFMMap();
        mFMMap.openMapById(bid, true);
    }
}