package com.example.fengmapdemo.utils;

import com.google.android.material.snackbar.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Zhaoguo Wang on 2017/7/28.
 */
public class SnackbarUtil {

    public static  int red = 0xfff44336;
    public static  int green = 0xff4caf50;
    public static  int blue = 0xff0097ef;
    public static  int orange = 0xffffc107;

    /**
     * 设置Snackbar背景颜色
     * @param snackbar
     * @param backgroundColor
     */
    public static void setBackgroundColor(Snackbar snackbar, int backgroundColor) {
        View view = snackbar.getView();
        if(view!=null){
            view.setBackgroundColor(backgroundColor);
        }
    }


    /**
     * 向Snackbar中添加view
     * @param snackbar
     * @param layoutId
     * @param index 新加布局在Snackbar中的位置
     */
    public static void SnackbarAddView( Snackbar snackbar,int layoutId,int index) {
        //获取snackbar的view
        View snackbarview = snackbar.getView();
        Snackbar.SnackbarLayout snackbarLayout=(Snackbar.SnackbarLayout)snackbarview;
        View add_view = LayoutInflater.from(snackbarview.getContext()).inflate(layoutId,null);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        p.gravity= Gravity.NO_GRAVITY;
        //添加布局
        snackbarLayout.addView(add_view,index,p);
    }

}

