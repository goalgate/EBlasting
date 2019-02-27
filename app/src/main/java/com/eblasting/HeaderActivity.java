package com.eblasting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.eblasting.Alerts.AlertExit;
import com.eblasting.Connect.ConnectTest;
import com.eblasting.Connect.DataType;
import com.eblasting.Connect.MyObserver;
import com.eblasting.Connect.RetrofitGenerator;
import com.eblasting.EventBus.TestNetEvent;
import com.eblasting.EventBus.TimeChangeEvent;
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
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class HeaderActivity extends RxActivity {

    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    private ConnectTest connectTest = new ConnectTest(this);

    Intent intent;

    @BindView(R.id.iv_OnOutlineIcon)
    ImageView iv_wifi;

    @BindView(R.id.tv_time)
    TextView tv_time;

    @OnClick(R.id.iv_quit)
    void quit() {
        new AlertExit(this, new AlertExit.ConfirmListener() {
            @Override
            public void confirmBack() {
                ActivityCollector.finishAll();
                stopService(intent);
            }
        }).message("是否注销回到登录界面");
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
        intent = new Intent(this, HeaderService.class);
        startService(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTestNetEvent(TestNetEvent event) {
        if (event.isTestStatus()) {
            iv_wifi.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.onlinedev));
        } else {
            iv_wifi.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.outlinedev));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTimeChangeEvent(TimeChangeEvent event) {
        tv_time.setText(event.getTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ActivityCollector.removeActivity(this);
    }
}
