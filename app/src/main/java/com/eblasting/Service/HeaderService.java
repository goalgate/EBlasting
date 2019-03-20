package com.eblasting.Service;

import android.app.Service;
import android.content.Intent;
import android.nfc.Tag;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TableRow;

import com.blankj.utilcode.util.SPUtils;
import com.eblasting.AppInit;
import com.eblasting.Connect.DataType;
import com.eblasting.Connect.MyObserver;
import com.eblasting.Connect.RetrofitGenerator;
import com.eblasting.EventBus.TestNetEvent;
import com.eblasting.Function.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;
import com.eblasting.Function.Func_IDCard.mvp.presenter.IDCardPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import cbdi.log.Lg;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HeaderService extends Service {

    private static String TAG = "HeaderService";

    private SPUtils config = SPUtils.getInstance("config");

    public FingerprintPresenter fpp = FingerprintPresenter.getInstance();

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
        fpp.fpInit(AppInit.getContext());
        fpp.fpOpen();
        pollingReady();
        Lg.e(TAG,"onCreate");
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
                                    }
                                }));

                    }
                });


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Lg.e(TAG,"onDestroy");
        IDCardPresenter.getInstance().idCardClose();
        fpp.fpCancel();
        Observable.timer(1,TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        fpp.fpClose();
                    }
                });

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
    public void onGetTestNetEvent(TestNetEvent event) {

    }
}
