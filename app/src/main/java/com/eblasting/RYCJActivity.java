package com.eblasting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.eblasting.Function.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;
import com.eblasting.Function.Func_FingerPrint.mvp.View.IFingerPrintView2;
import com.eblasting.Function.Func_IDCard.mvp.presenter.IDCardPresenter;
import com.eblasting.Function.Func_IDCard.mvp.view.IIDCardView;
import com.jakewharton.rxbinding2.view.RxView;

import java.io.File;
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

public class RYCJActivity extends HeaderActivity implements IIDCardView, IFingerPrintView2 {

    public IDCardPresenter idp = IDCardPresenter.getInstance();

    public FingerprintPresenter fpp = FingerprintPresenter.getInstance();

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

    @BindView(R.id.iv_userPhoto)
    ImageView iv_userPhoto;

    @BindView(R.id.iv_fpPhoto)
    ImageView iv_fpPhoto;

    @BindView(R.id.tv_fpDescribe)
    TextView tv_fpDescribe;

    String fp_id;

    @OnClick(R.id.iv_userPhoto)
    void takePictures() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("autofocus", true);
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rycj);
        ButterKnife.bind(this);
        RxView.clicks(iv_fpPhoto).throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        iv_fpPhoto.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.img_fingerprint));
                        fpp.fpCancel();
                        Observable.timer(1,TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        fp_id = String.valueOf(fpp.fpGetEmptyID());
                                        fpp.fpEnroll(fp_id);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_fpDescribe.setText("点击指纹图片以获取指纹编号");
        idp.readCard();
        idp.IDCardPresenterSetView(this);
        fpp.FingerPrintPresenterSetView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        idp.stopReadCard();
        idp.IDCardPresenterSetView(null);
        fpp.fpCancel();
        fpp.FingerPrintPresenterSetView(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bundle b = data.getExtras();
            Bitmap bm = (Bitmap) b.get("data");
            iv_userPhoto.setImageBitmap(resizeImage(bm,320,310));
        }
    }

    @Override
    public void onsetCardImg(Bitmap bmp) {
        iv_headphoto.setImageBitmap(bmp);
    }

    @Override
    public void onSetText(String Msg) {

    }

    @Override
    public void onsetCardInfo(ICardInfo cardInfo) {
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
        iv_fpPhoto.setImageBitmap(resizeImage(bmp,261,261));

    }

    @Override
    public void onCapturing(Bitmap bmp) {

    }

    @Override
    public void onRegSuccess() {

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

}
