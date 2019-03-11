package com.eblasting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eblasting.Alerts.AlertExit;
import com.eblasting.Bean.MsgBean;
import com.eblasting.Service.HeaderService;
import com.eblasting.Tool.ActivityCollector;
import com.eblasting.UI.AutoRunRecycleView;
import com.eblasting.UI.MsgRecycleAdapter;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cbdi.log.Lg;


public class IndexActivity extends HeaderActivity {

    private static String TAG = "IndexActivity";

    @BindView(R.id.rv_recyclerview)
    AutoRunRecycleView rc_view;

    @OnClick(R.id.btn_wailairenyuan)
    void wailairenyuan(){
        ActivityUtils.startActivity(getPackageName(),getPackageName()+".WLRYActivity");
    }

    @OnClick(R.id.btn_renyuancaiji)
    void renyuancaiji(){
        ActivityUtils.startActivity(getPackageName(),getPackageName()+".RYCJActivity");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);
        Lg.e(TAG,"onCreate");
        initDatas();
        recycleViewInit();
        intent = new Intent(this, HeaderService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        iv_back.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AutoRunRecycleView.release();
        stopService(intent);

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
}
