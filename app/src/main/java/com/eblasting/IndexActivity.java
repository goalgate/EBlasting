package com.eblasting;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eblasting.Bean.MsgBean;
import com.eblasting.Connect.ConnectTest;
import com.eblasting.Connect.DataType;
import com.eblasting.Connect.MyObserver;
import com.eblasting.Connect.RetrofitGenerator;
import com.eblasting.Tool.ActivityCollector;
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

public class IndexActivity extends HeaderActivity {


    @BindView(R.id.rv_recyclerview)
    AutoRunRecycleView rc_view;

    @OnClick(R.id.btn_wailairenyuan)
    void test(){
        ActivityUtils.startActivity(getPackageName(),getPackageName()+".WLRYActivity");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);
        initDatas();
        recycleViewInit();
    }

    @Override
    protected void onStop() {
        super.onStop();
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
