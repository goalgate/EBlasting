package com.eblasting.Service;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.SPUtils;
import com.eblasting.Connect.DataType;
import com.eblasting.Connect.MyObserver;
import com.eblasting.Connect.RetrofitGenerator;
import com.eblasting.EventBus.TestNetEvent;
import com.eblasting.EventBus.TimeChangeEvent;
import com.eblasting.Function.Func_IDCard.mvp.presenter.IDCardPresenter;
import com.eblasting.HeaderActivity;
import com.eblasting.R;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HeaderService extends Service {


    private SPUtils config = SPUtils.getInstance("config");

    Disposable dis_online;

    Disposable dis_testNet;

    Disposable dis_time;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IDCardPresenter.getInstance().idCardOpen();
        EventBus.getDefault().register(this);
        pollingReady();
    }

    private void pollingReady() {
        dis_online = Observable.interval(10, 3600, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        RetrofitGenerator.getConnectApi().online(config.getString("key"), DataType.online)
                                .subscribeOn(Schedulers.io())
                                .unsubscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new MyObserver(HeaderService.this, DataType.online, new MyObserver.Callback() {
                                    @Override
                                    public void onResponseJsonObject(JSONObject jsonObject) {

                                    }

                                    @Override
                                    public void onResponseWiFiBitmap(boolean status) {

                                    }
                                }));
                    }
                });

        dis_testNet = Observable.interval(5, 30, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        RetrofitGenerator.getConnectApi().testNet(config.getString("key"), DataType.testNet)
                                .subscribeOn(Schedulers.io())
                                .unsubscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new MyObserver(HeaderService.this, DataType.testNet, new MyObserver.Callback() {
                                    @Override
                                    public void onResponseJsonObject(JSONObject jsonObject) {

                                    }

                                    @Override
                                    public void onResponseWiFiBitmap(boolean status) {
                                        EventBus.getDefault().post(new TestNetEvent(status));
//                                        if (status) {
//                                            iv_wifi.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.onlinedev));
//                                        } else {
//                                            iv_wifi.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.outlinedev));
//                                        }
                                    }
                                }));

                    }
                });


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        IDCardPresenter.getInstance().idCardClose();
        if (dis_testNet != null) {
            dis_testNet.dispose();
        }
        if (dis_online != null) {
            dis_online.dispose();
        }
        if (dis_time != null) {
            dis_time.dispose();
        }
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTimeChangeEvent(TimeChangeEvent event) {

    }
}
