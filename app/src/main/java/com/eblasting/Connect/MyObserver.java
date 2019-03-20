package com.eblasting.Connect;

import android.content.Context;
import android.util.Log;

import com.eblasting.Alerts.Alarm;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class MyObserver implements Observer<ResponseBody> {

    private Context context;

    private Callback callback;

    private String dataType;

    public MyObserver(Context context, String dataType, Callback callback) {
        this.context = context;
        this.callback = callback;
        this.dataType = dataType;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string().toString());
            if (jsonObject.getString("result").equals("true")) {
                returnTrue(jsonObject);
            } else if (jsonObject.getString("result").equals("false")) {
                returnFalse();
            } else if (jsonObject.getString("result").equals("dataErr")) {
                returnCommonErr("上传的参数有错");
            } else if (jsonObject.getString("result").equals("keyFalse")) {
                returnCommonErr("key参数不正确");
            } else if (jsonObject.getString("result").equals("dbErr")) {
                returnCommonErr("数据库操作失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(Throwable e) {
        if (dataType.equals(DataType.testNet)){
            returnFalse();
        }
        if(!(dataType.equals(DataType.online)||dataType.equals(DataType.testNet))){
            Alarm.getInstance(context).message("无法连接到网络，请检查设备联网情况");
        }

    }

    @Override
    public void onComplete() {

    }

    public interface Callback {
        void onResponseJsonObject(JSONObject jsonObject);

        void onResponseWiFiBitmap(boolean status);
    }

    private void returnTrue(JSONObject jsonObject) {
        if (dataType.equals(DataType.testNet)) {
            callback.onResponseWiFiBitmap(true);
        } else {
            callback.onResponseJsonObject(jsonObject);
        }
    }

    private void returnFalse() {
        if (dataType.equals(DataType.testNet)) {
            callback.onResponseWiFiBitmap(false);
            Log.e("callback","false");
        } else if (dataType.equals(DataType.online)) {

        } else if (dataType.equals(DataType.Login)){
            Alarm.getInstance(context).message("账号密码错误，请输入正确的账号密码");
        } else{
            Alarm.getInstance(context).message("操作失败");
        }
    }

    private void returnCommonErr(String msg) {
        if (dataType.equals(DataType.testNet)) {
            callback.onResponseWiFiBitmap(false);
        } else if (dataType.equals(DataType.online)) {

        } else {
            Alarm.getInstance(context).message(msg);
        }
    }
}
