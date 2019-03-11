package com.eblasting;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.widget.EditText;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.eblasting.Alerts.Alarm;
import com.eblasting.Connect.DataType;
import com.eblasting.Connect.MyObserver;
import com.eblasting.Connect.RetrofitGenerator;
import com.eblasting.Function.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;
import com.eblasting.Tool.DESX;
import com.eblasting.Tool.NetInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class LoginActivity extends Activity {

    private SPUtils config = SPUtils.getInstance("config");

    @BindView(R.id.et_account)
    EditText et_account;

    @BindView(R.id.et_password)
    EditText et_password;

    @OnClick(R.id.btn_login)
    void login() {
        if (et_account.getText().toString().isEmpty() || et_password.getText().toString().isEmpty()) {
            //Alarm.getInstance(this).message("账号或密码为空，请输入信息");
            ActivityUtils.startActivity(getPackageName(),getPackageName()+".IndexActivity");
        } else {
            JSONObject data = new JSONObject();
            try {
                data.put("username", et_account.getText().toString());
                data.put("password", et_password.getText().toString());
                data.put("daid", config.getString("daid"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RetrofitGenerator.getConnectApi("http://192.168.12.69:7001/daServer/")
                    .login(config.getString("key"), DataType.Login, data.toString())
                    .subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver(LoginActivity.this, DataType.Login, new MyObserver.Callback() {
                        @Override
                        public void onResponseJsonObject(JSONObject jsonObject) {

                        }

                        @Override
                        public void onResponseWiFiBitmap(boolean status) {

                        }
                    }));
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.setStatusBarVisibility(this, false);
        setContentView(R.layout.acticity_login);
        firstStart();
        ButterKnife.bind(this);
    }

    private void firstStart(){
        if (config.getBoolean("firstStart", true)) {
            JSONObject jsonKey = new JSONObject();
            try {
                jsonKey.put("daid", new NetInfo().getMacId());
                jsonKey.put("check", DESX.encrypt(new NetInfo().getMacId()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            config.put("firstStart", false);
            config.put("daid", new NetInfo().getMacId());
            config.put("key", DESX.encrypt(jsonKey.toString()));
            config.put("ServerId", "http://192.168.12.69:7001/daServer/");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Alarm.getInstance(this).release();
    }
}
