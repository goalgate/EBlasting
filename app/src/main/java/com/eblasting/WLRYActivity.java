package com.eblasting;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import com.blankj.utilcode.util.ToastUtils;
import com.eblasting.Function.Func_IDCard.mvp.presenter.IDCardPresenter;
import com.eblasting.Function.Func_IDCard.mvp.view.IIDCardView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cbdi.drv.card.ICardInfo;

public class WLRYActivity extends HeaderActivity implements IIDCardView {

    public IDCardPresenter idp = IDCardPresenter.getInstance();

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wlry);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        idp.readCard();
        idp.IDCardPresenterSetView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        idp.stopReadCard();
        idp.IDCardPresenterSetView(null);
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
            tv_year.setText(cardInfo.birthday().substring(0,4));
            tv_month.setText(String.valueOf(Integer.valueOf(cardInfo.birthday().substring(4,6))));
            tv_day.setText(String.valueOf(Integer.valueOf(cardInfo.birthday().substring(6,8))));
            tv_address.setText(cardInfo.address());
            tv_cardId.setText(cardInfo.cardId());
        }catch (Exception e){
            ToastUtils.showLong(e.toString());
        }
    }

    @Override
    public void onsetCardImg(Bitmap bmp) {
        iv_headphoto.setImageBitmap(bmp);
    }
}
