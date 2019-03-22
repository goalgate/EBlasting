package com.eblasting.Connect;

import android.content.Context;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ConnectTest {

    private Context context;

    private SPUtils config = SPUtils.getInstance("config");

    public ConnectTest(Context context) {
        this.context = context;
    }

    public void queryPersonInfo() {
        JSONObject data = new JSONObject();
        try {
            data.put("id", "test12345");
            data.put("identity", "441302199308100538");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getConnectApi()
                .queryPersonInfo(config.getString("key"), DataType.queryPersonInfo, data.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(context, DataType.queryPersonInfo, new MyObserver.Callback() {
                    @Override
                    public void onResponseJsonObject(JSONObject jsonObject) {

                    }

                    @Override
                    public void onResponseWiFiBitmap(boolean status) {

                    }

                    @Override
                    public void onConnectError() {

                    }
                }));
    }


    public void personInfo(){
        JSONObject data = new JSONObject();
        try {
            data.put("id", "test12345");
            data.put("name", "王振文");
            data.put("identity", "441302199308100538");
            data.put("personType", "1");
            data.put("personHPhoto", "test12345");
            data.put("personPhoto", "test12345");
            data.put("fingerprint", "test12345");
            data.put("fingerprintPhoto", "test12345");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getConnectApi()
                .personInfo(config.getString("key"), DataType.personInfo, data.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(context, DataType.personInfo, new MyObserver.Callback() {
                    @Override
                    public void onResponseJsonObject(JSONObject jsonObject) {

                    }

                    @Override
                    public void onResponseWiFiBitmap(boolean status) {

                    }

                    @Override
                    public void onConnectError() {

                    }
                }));
    }

    public void word(){
        JSONObject data = new JSONObject();
        try {
            data.put("id", "test12345");
            data.put("name", "王振文");
            data.put("identity", "441302199308100538");
            data.put("workType", "1");
            data.put("time", "test12345");
            data.put("personPhoto", "test12345");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getConnectApi()
                .work(config.getString("key"), DataType.work, data.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(context, DataType.work, new MyObserver.Callback() {
                    @Override
                    public void onResponseJsonObject(JSONObject jsonObject) {

                    }

                    @Override
                    public void onResponseWiFiBitmap(boolean status) {

                    }

                    @Override
                    public void onConnectError() {

                    }
                }));
    }

    public void workRecord(){
        JSONObject data = new JSONObject();
        try {
            data.put("id", "test12345");
            data.put("name", "王振文");
            data.put("identity", "441302199308100538");
            data.put("state", "1");
            data.put("time", "test12345");
            data.put("content", "test12345");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getConnectApi()
                .work(config.getString("key"), DataType.workRecord, data.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(context, DataType.workRecord, new MyObserver.Callback() {
                    @Override
                    public void onResponseJsonObject(JSONObject jsonObject) {

                    }

                    @Override
                    public void onResponseWiFiBitmap(boolean status) {

                    }

                    @Override
                    public void onConnectError() {

                    }
                }));
    }

    public void checkDevice(){
        JSONObject data = new JSONObject();
        try {
            data.put("id", "test12345");
            data.put("name", "王振文");
            data.put("identity", "441302199308100538");
            data.put("state", "1");
            data.put("time", "test12345");
            data.put("content", "test12345");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getConnectApi()
                .work(config.getString("key"), DataType.checkDevice, data.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(context, DataType.checkDevice, new MyObserver.Callback() {
                    @Override
                    public void onResponseJsonObject(JSONObject jsonObject) {

                    }

                    @Override
                    public void onResponseWiFiBitmap(boolean status) {

                    }

                    @Override
                    public void onConnectError() {

                    }
                }));
    }

    public void outsidePerson(){
        JSONObject data = new JSONObject();
        try {
            data.put("id", "test12345");
            data.put("name", "王振文");
            data.put("identity", "441302199308100538");
            data.put("affair","送货上门");
            data.put("headPhoto", "test12345");
            data.put("photo", "test12345");
            data.put("time", "test12345");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitGenerator.getConnectApi()
                .outsidePerson(config.getString("key"), DataType.outsidePerson, data.toString())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(context, DataType.outsidePerson, new MyObserver.Callback() {
                    @Override
                    public void onResponseJsonObject(JSONObject jsonObject) {
                        try {
                            if (jsonObject.getString("result").equals("true")) {
                                ToastUtils.showLong("true");
                            }
                        }catch (JSONException e){
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
 }

