package com.eblasting;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eblasting.Alerts.AlertExit;
import com.eblasting.Connect.ConnectTest;
import com.eblasting.EventBus.TestNetEvent;
import com.eblasting.Function.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;
import com.eblasting.Service.HeaderService;
import com.eblasting.Tool.ActivityCollector;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.RxActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cbdi.log.Lg;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class HeaderActivity extends RxActivity {

    private static String TAG = "HeaderActivity";

    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    private ConnectTest connectTest = new ConnectTest(this);

    public static Intent intent;

    @BindView(R.id.iv_OnOutlineIcon)
    ImageView iv_wifi;

    @BindView(R.id.tv_time)
    TextView tv_time;

    @BindView(R.id.iv_back)
    ImageView iv_back;

    @OnClick(R.id.iv_quit)
    void quit() {
        try{
            new AlertExit(this, new AlertExit.ConfirmListener() {
                @Override
                public void confirmBack() {
                    ActivityCollector.finishAll();
                    stopService(intent);
                }
            }).message("是否注销回到登录界面");
        }catch (Exception e){
            ToastUtils.showLong(e.toString());
            Lg.e(TAG,e.toString());
        }
    }

    @OnClick(R.id.iv_back)
    void back(){
        this.finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.setStatusBarVisibility(this, false);
        ActivityCollector.addActivity(this);
        EventBus.getDefault().register(this);
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        tv_time.setText(formatter.format(new Date(System.currentTimeMillis())));
                    }
                });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTestNetEvent(TestNetEvent event) {
        if (event.isTestStatus()) {
            iv_wifi.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.onlinedev));
        } else {
            iv_wifi.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.outlinedev));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ActivityCollector.removeActivity(this);
    }
}
