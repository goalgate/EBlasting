package com.eblasting;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.eblasting.Alerts.Alarm;
import com.eblasting.Bean.PersonBean;
import com.eblasting.Connect.DataType;
import com.eblasting.Connect.MyObserver;
import com.eblasting.Connect.RetrofitGenerator;
import com.eblasting.Function.Func_Camera.mvp.presenter.PhotoPresenter;
import com.eblasting.Function.Func_Camera.mvp.view.IPhotoView;
import com.eblasting.Function.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;
import com.eblasting.Function.Func_FingerPrint.mvp.View.IFingerPrintView2;
import com.eblasting.Function.Func_IDCard.mvp.presenter.IDCardPresenter;
import com.eblasting.Function.Func_IDCard.mvp.view.IIDCardView;
import com.eblasting.Tool.FileUtils;
import com.eblasting.greendao.DaoSession;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cbdi.drv.card.ICardInfo;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RYCJActivity extends HeaderActivity implements IIDCardView, IFingerPrintView2, IPhotoView {

    private IDCardPresenter idp = IDCardPresenter.getInstance();

    private FingerprintPresenter fpp = FingerprintPresenter.getInstance();

    private PhotoPresenter pp = PhotoPresenter.getInstance();

    private SPUtils config = SPUtils.getInstance("config");

    private DaoSession daoSession = AppInit.getInstance().getDaoSession();

    @BindView(R.id.tv_IDName)
    TextView tv_name;

    @BindView(R.id.tv_IDGender)
    TextView tv_gender;

    @BindView(R.id.tv_IDNation)
    TextView tv_nation;

    @BindView(R.id.tv_IDYear)
    TextView tv_year;

    @BindView(R.id.tv_IDMonth)
    TextView tv_month;

    @BindView(R.id.tv_IDDay)
    TextView tv_day;

    @BindView(R.id.tv_IDAddress)
    TextView tv_address;

    @BindView(R.id.tv_IDCardID)
    TextView tv_cardId;

    @BindView(R.id.iv_IDHeadphoto)
    ImageView iv_headphoto;

    @BindView(R.id.iv_fpPhoto)
    ImageView iv_fpPhoto;

    @BindView(R.id.tv_fpDescribe)
    TextView tv_fpDescribe;

    @BindView(R.id.sview_Camera)
    SurfaceView surfaceView;

    @OnClick(R.id.btn_reset)
    void reset() {
        messageClear(true);
    }

    @OnClick(R.id.btn_upload)
    void upload() {
        try {
            if (!fpSucc) {
                throw new NullPointerException();
            } else {
                JSONObject data = new JSONObject();
                try {
                    data.put("id", "test12345");
                    data.put("name", info_card.name());
                    data.put("identity", info_card.cardId());
                    data.put("personType", personType);
                    data.put("personHPhoto", FileUtils.bitmapToBase64(bmp_headphoto));
                    data.put("personPhoto", FileUtils.bitmapToBase64(bmp_photo));
                    data.put("fingerprint", fpp.fpUpTemlate(fp_id));
                    data.put("fingerprintPhoto", FileUtils.bitmapToBase64(bmp_fpphoto));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RetrofitGenerator.getConnectApi()
                        .personInfo(config.getString("key"), DataType.personInfo, data.toString())
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MyObserver(this, DataType.personInfo, new MyObserver.Callback() {
                            @Override
                            public void onResponseJsonObject(JSONObject jsonObject) {
                                try {
                                    if (jsonObject.getString("result").equals("true")) {
                                        PersonBean bean = new PersonBean();
                                        bean.setName(info_card.name());
                                        bean.setCardID(info_card.cardId());
                                        bean.setFp_id(fp_id);
                                        bean.setPerson_type(personType);
                                        daoSession.insert(bean);
                                        Alarm.getInstance(RYCJActivity.this).message("信息上传成功");

                                        messageClear(false);

                                    } else {
                                        Alarm.getInstance(RYCJActivity.this).message("信息上传失败");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onResponseWiFiBitmap(boolean status) {

                            }

                            @Override
                            public void onConnectError() {

                            }
                        }));
            }
        } catch (NullPointerException e) {
            Alarm.getInstance(this).message("信息录入不全，无法上传信息");
        }

    }

    @OnClick(R.id.sview_Camera)
    void Display() {
        pp.setDisplay(surfaceView.getHolder());
        fpp.fpRemoveAll();
    }

    Disposable disposableTips;

    String fp_id;

    Bitmap bmp_headphoto;

    Bitmap bmp_photo;

    Bitmap bmp_fpphoto;

    ICardInfo info_card;

    String personType;

    Boolean fpSucc = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rycj);
        ButterKnife.bind(this);
        pp.initCamera();
        if (getIntent().getExtras().getBoolean("network")) {
            iv_wifi.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.onlinedev));
        }
        RxView.clicks(iv_fpPhoto).throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        iv_fpPhoto.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.img_fingerprint));
                        fpp.fpCancel();
                        Observable.timer(1, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        if(!fpSucc){
                                            fp_id = String.valueOf(fpp.fpGetEmptyID());
                                            fpp.fpEnroll(fp_id);
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        disposableTips = RxTextView.textChanges(tv_name)
                .debounce(300, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        messageClear(true);
                    }
                });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        pp.initCamera();
    }

    @Override
    protected void onStart() {
        super.onStart();
        pp.setParameter(surfaceView.getHolder());
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_fpDescribe.setText("点击指纹图片以获取指纹编号");
        idp.readCard();
        idp.IDCardPresenterSetView(this);
        fpp.FingerPrintPresenterSetView(this);
        pp.PhotoPresenterSetView(this);
        pp.setDisplay(surfaceView.getHolder());
    }

    @Override
    protected void onPause() {
        super.onPause();
        idp.stopReadCard();
        idp.IDCardPresenterSetView(null);
        fpp.fpCancel();
        fpp.FingerPrintPresenterSetView(null);
        pp.PhotoPresenterSetView(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pp.close_Camera();
        disposableTips.dispose();
        Alarm.getInstance(this).release();
        messageClear(true);
    }


    @Override
    public void onGetPhoto(Bitmap bmp) {
        bmp_photo = bmp;
    }

    @Override
    public void onCaremaText(String s) {

    }

    @Override
    public void onsetCardImg(Bitmap bmp) {
        bmp_headphoto = bmp;
    }

    @Override
    public void onSetText(String Msg) {

    }


    @Override
    public void onsetCardInfo(ICardInfo cardInfo) {
        info_card = cardInfo;
        queryPersonInfo(cardInfo);


    }

    @Override
    public void onText(String msg) {
        tv_fpDescribe.setText(msg);

    }

    @Override
    public void onFpSucc(String msg) {


    }

    @Override
    public void onSetImg(Bitmap bmp) {
        iv_fpPhoto.setImageBitmap(resizeImage(bmp, 261, 261));
        bmp_fpphoto = bmp;
    }

    @Override
    public void onCapturing(Bitmap bmp) {

    }

    @Override
    public void onRegSuccess() {
        fpSucc = true;
    }

    public void queryPersonInfo(final ICardInfo cardInfo) {
        JSONObject data = new JSONObject();
        try {
            data.put("id", "test12345");
            data.put("identity", cardInfo.cardId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getConnectApi()
                .queryPersonInfo(config.getString("key"), DataType.queryPersonInfo, data.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(this, DataType.queryPersonInfo, new MyObserver.Callback() {
                    @Override
                    public void onResponseJsonObject(JSONObject jsonObject) {
                        try {
                            if (jsonObject.getString("personType").equals("1")) {
                                personType = "1";
                                tv_name.setText(cardInfo.name());
                                tv_gender.setText(cardInfo.sex());
                                tv_nation.setText(cardInfo.nation());
                                tv_year.setText(cardInfo.birthday().substring(0, 4));
                                tv_month.setText(String.valueOf(Integer.valueOf(cardInfo.birthday().substring(4, 6))));
                                tv_day.setText(String.valueOf(Integer.valueOf(cardInfo.birthday().substring(6, 8))));
                                tv_address.setText(cardInfo.address());
                                tv_cardId.setText(cardInfo.cardId());
                                iv_headphoto.setImageBitmap(bmp_headphoto);
                                pp.capture();
                            } else {
                                personType = "0";
                                Alarm.getInstance(RYCJActivity.this).message("人员身份类型服务器验证不通过");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onResponseWiFiBitmap(boolean status) {

                    }

                    @Override
                    public void onConnectError() {
                        personType = "0";
                    }
                }));
    }

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    private void messageClear(Boolean del) {
        fpp.fpCancel();
        tv_name.setText(null);
        tv_gender.setText(null);
        tv_nation.setText(null);
        tv_year.setText(null);
        tv_month.setText(null);
        tv_day.setText(null);
        tv_address.setText(null);
        tv_cardId.setText(null);
        iv_headphoto.setImageBitmap(null);
        pp.setDisplay(surfaceView.getHolder());
        personType = "0";
        if (del&&fpSucc){
            fpp.fpRemoveTmpl(fp_id);
        }
        fpSucc = false;
        fp_id = null;
        tv_fpDescribe.setText("点击指纹图片以获取指纹编号");
        iv_fpPhoto.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.img_fingerprint));

    }
}
