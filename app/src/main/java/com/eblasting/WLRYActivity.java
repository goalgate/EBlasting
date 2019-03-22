package com.eblasting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eblasting.Alerts.Alarm;
import com.eblasting.Connect.DataType;
import com.eblasting.Connect.MyObserver;
import com.eblasting.Connect.RetrofitGenerator;
import com.eblasting.Function.Func_Camera.mvp.presenter.PhotoPresenter;
import com.eblasting.Function.Func_Camera.mvp.view.IPhotoView;
import com.eblasting.Function.Func_IDCard.mvp.presenter.IDCardPresenter;
import com.eblasting.Function.Func_IDCard.mvp.view.IIDCardView;
import com.eblasting.Tool.FileUtils;
import com.google.gson.JsonArray;
import com.jakewharton.rxbinding2.widget.RxTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cbdi.drv.card.ICardInfo;
import cbdi.log.Lg;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class WLRYActivity extends HeaderActivity implements IIDCardView, IPhotoView {

    private SPUtils reason_sp = SPUtils.getInstance("reasonlist");

    private SPUtils config = SPUtils.getInstance("config");

    public IDCardPresenter idp = IDCardPresenter.getInstance();

    public PhotoPresenter pp = PhotoPresenter.getInstance();

    String photo;

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

    @BindView(R.id.et_visit)
    EditText et_visit;

    @BindView(R.id.sview_Camera)
    SurfaceView surfaceView;

    Disposable disposableTips;

    @OnClick(R.id.iv_list)
    void listReason() {
        try {
            final String[] reasonArray = reason_sp.getString("reason").split(",");
            Lg.e("reason长度", String.valueOf(reasonArray.length));
            if (reasonArray.length < 3) {
                new AlertView("请选择来访事由", null, "取消",
                        reasonArray, null,
                        this, AlertView.Style.ActionSheet, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        try {
                            et_visit.setText(reasonArray[position]);
                        }catch (ArrayIndexOutOfBoundsException e){
                            e.printStackTrace();
                        }

                    }
                }).show();
            } else {
                final String[] new_array = {reasonArray[reasonArray.length - 1], reasonArray[reasonArray.length - 2], reasonArray[reasonArray.length - 3]};
                new AlertView("请选择来访事由", null, "取消",
                        new_array, null,
                        this, AlertView.Style.ActionSheet, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        et_visit.setText(new_array[position]);
                    }
                }).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wlry);
        ButterKnife.bind(this);
        pp.initCamera();
        if (getIntent().getExtras().getBoolean("network")){
            iv_wifi.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.onlinedev));
        }
        disposableTips = RxTextView.textChanges(tv_name)
                .debounce(30, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        messageClear();
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
        idp.readCard();
        idp.IDCardPresenterSetView(this);
        pp.PhotoPresenterSetView(this);
        pp.setDisplay(surfaceView.getHolder());

    }

    @Override
    protected void onPause() {
        super.onPause();
        idp.stopReadCard();
        idp.IDCardPresenterSetView(null);
        pp.PhotoPresenterSetView(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pp.close_Camera();
        Alarm.getInstance(this).release();
        disposableTips.dispose();
    }

    @Override
    public void onCaremaText(String s) {

    }

    @Override
    public void onGetPhoto(Bitmap bmp) {
        photo = FileUtils.bitmapToBase64(bmp);
        outsidePerson();
    }

    @Override
    public void onSetText(String Msg) {

    }

    @Override
    public void onsetCardInfo(ICardInfo cardInfo) {
        if (TextUtils.isEmpty(et_visit.getText().toString())) {
            Alarm.getInstance(this).message("请先输入来访事由再进行身份证信息读取操作");
        } else {
            try {
                tv_name.setText(cardInfo.name());
                tv_gender.setText(cardInfo.sex());
                tv_nation.setText(cardInfo.nation());
                tv_year.setText(cardInfo.birthday().substring(0, 4));
                tv_month.setText(String.valueOf(Integer.valueOf(cardInfo.birthday().substring(4, 6))));
                tv_day.setText(String.valueOf(Integer.valueOf(cardInfo.birthday().substring(6, 8))));
                tv_address.setText(cardInfo.address());
                tv_cardId.setText(cardInfo.cardId());
            } catch (Exception e) {
                ToastUtils.showLong(e.toString());
            }
            pp.getOneShut();
        }
    }

    public void outsidePerson() {
        JSONObject data = new JSONObject();
        try {
            data.put("id", "test12345");
            data.put("name", tv_name.getText().toString());
            data.put("identity", tv_cardId.getText().toString());
            data.put("affair", et_visit.getText().toString());
            data.put("headPhoto", FileUtils.bitmapToBase64(((BitmapDrawable) ((ImageView) iv_headphoto).getDrawable()).getBitmap()));
            data.put("photo", photo);
            data.put("time", tv_time.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getConnectApi()
                .outsidePerson(config.getString("key"), DataType.outsidePerson, data.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(this, DataType.outsidePerson, new MyObserver.Callback() {
                    @Override
                    public void onResponseJsonObject(JSONObject jsonObject) {
                        try {
                            if (jsonObject.getString("result").equals("true")) {
                                Alarm.getInstance(WLRYActivity.this).message("来访人员信息已上传");
                                String reason = reason_sp.getString("reason");
                                Set set = new HashSet();
                                String[] reasonArray = reason.split(",");
                                for (String re : reasonArray) {
                                    set.add(re);
                                }
                                if (set.add(et_visit.getText().toString())) {
                                    reason += et_visit.getText().toString() + ",";
                                }
                                reason_sp.put("reason", reason);
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

    @Override
    public void onsetCardImg(Bitmap bmp) {
        if (!TextUtils.isEmpty(et_visit.getText().toString())) {
            iv_headphoto.setImageBitmap(bmp);
        }
    }
    private void messageClear(){
        tv_name.setText(null);
        tv_gender.setText(null);
        tv_nation.setText(null);
        tv_year.setText(null);
        tv_month.setText(null);
        tv_day.setText(null);
        tv_address.setText(null);
        tv_cardId.setText(null);
        iv_headphoto.setImageBitmap(null);

    }
}
