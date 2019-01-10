package com.eblasting;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eblasting.Alerts.Alarm;
import com.eblasting.Bean.MsgBean;
import com.eblasting.Connect.DataType;
import com.eblasting.Connect.MyObserver;
import com.eblasting.Connect.RetrofitGenerator;
import com.eblasting.UI.AutoRunRecycleView;
import com.eblasting.UI.MsgRecycleAdapter;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.RxActivity;

import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class IndexActivity extends RxActivity {

    private SPUtils config = SPUtils.getInstance("config");

    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    @BindView(R.id.iv_OnOutlineIcon)
    ImageView iv_wifi;

    @BindView(R.id.tv_time)
    TextView tv_time;

    @BindView(R.id.rv_recyclerview)
    AutoRunRecycleView rc_view;

    @OnClick(R.id.iv_quit) void quit(){
        ActivityUtils.startActivity(getPackageName(),getPackageName()+".LoginActivity");

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.setStatusBarVisibility(this, false);
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);
        initDatas();
        recycleViewInit();
        pollingReady();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       AutoRunRecycleView.release();
    }

    MsgRecycleAdapter adapter;

    private void recycleViewInit() {
        adapter = new MsgRecycleAdapter(this, msgBeanList);
        adapter.setOnItemClickListener(new MsgRecycleAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                ToastUtils.showLong(position);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //((MyLinearLayoutManager) linearLayoutManager).setScrollEnabled(false);
        rc_view.setLayoutManager(linearLayoutManager);
        rc_view.setAdapter(adapter);
        rc_view.start();
    }


    List<MsgBean> msgBeanList;

    private void initDatas() {
        msgBeanList = new ArrayList<MsgBean>();
        msgBeanList.add(new MsgBean("湖北雪飞化工有限公司购买100千克碳酸钙", "2017-06-03 17:00:00"));
        msgBeanList.add(new MsgBean("湖北雪飞化工有限公司购买100千克碳酸钙", "2017-07-03 17:00:00"));
        msgBeanList.add(new MsgBean("湖北雪飞化工有限公司购买100千克碳酸钙", "2017-08-03 17:00:00"));
        msgBeanList.add(new MsgBean("湖北雪飞化工有限公司购买100千克碳酸钙", "2017-09-03 17:00:00"));
    }


    private void pollingReady() {
        Observable.interval(10, 3600, TimeUnit.SECONDS)
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        RetrofitGenerator.getConnectApi().online(config.getString("key"),DataType.online)
                                .subscribeOn(Schedulers.io())
                                .unsubscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new MyObserver(IndexActivity.this, DataType.online, new MyObserver.Callback() {
                                    @Override
                                    public void onResponseJsonObject(JSONObject jsonObject) {

                                    }

                                    @Override
                                    public void onResponseWiFiBitmap(boolean status) {

                                    }
                                }));


                    }
                });

        Observable.interval(5, 30, TimeUnit.SECONDS)
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        RetrofitGenerator.getConnectApi().testNet(config.getString("key"),DataType.testNet)
                                .subscribeOn(Schedulers.io())
                                .unsubscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new MyObserver(IndexActivity.this, DataType.testNet, new MyObserver.Callback() {
                                    @Override
                                    public void onResponseJsonObject(JSONObject jsonObject) {

                                    }

                                    @Override
                                    public void onResponseWiFiBitmap(boolean status) {
                                        if(status){
                                                iv_wifi.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.onlinedev));
                                            }else{
                                                iv_wifi.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.outlinedev));
                                            }
                                    }
                                }));

                    }
                });

        Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        tv_time.setText(formatter.format(new Date(System.currentTimeMillis())));
                    }
                });
    }



}
