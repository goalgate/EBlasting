package com.eblasting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.eblasting.Bean.PersonBean;
import com.eblasting.Function.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;
import com.eblasting.Function.Func_FingerPrint.mvp.View.IFingerPrintView2;
import com.eblasting.greendao.DaoSession;

import butterknife.ButterKnife;

public class KQJLActivity extends HeaderActivity implements IFingerPrintView2 {

    FingerprintPresenter fpp = FingerprintPresenter.getInstance();

    DaoSession daoSession = AppInit.getInstance().getDaoSession();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rycj);
        ButterKnife.bind(this);
        if (getIntent().getExtras().getBoolean("network")) {
            iv_wifi.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.onlinedev));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fpp.FingerPrintPresenterSetView(this);
        fpp.fpIdentify();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fpp.FingerPrintPresenterSetView(null);

    }

    @Override
    public void onSetImg(Bitmap bmp) {
    }

    @Override
    public void onText(String msg) {

    }

    @Override
    public void onRegSuccess() {

    }

    @Override
    public void onFpSucc(String msg) {
        PersonBean bean = daoSession.queryRaw(PersonBean.class,"where fp_id="+msg.substring(3,msg.length())).get(0);
        ToastUtils.showLong(bean.getName());
    }

    @Override
    public void onCapturing(Bitmap bmp) {

    }
}
