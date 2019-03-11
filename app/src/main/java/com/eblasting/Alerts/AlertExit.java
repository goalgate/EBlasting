package com.eblasting.Alerts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.eblasting.R;


public class AlertExit {

    private TextView alarmText;

    private AlertView alert;

    private ViewGroup alarmView;

    private Context context;

    public AlertExit(Context context, final ConfirmListener listener) {
        this.context = context;
        alarmView = (ViewGroup) LayoutInflater.from(this.context).inflate(R.layout.alarm_text, null);
        alarmText = (TextView) alarmView.findViewById(R.id.alarmText);
        alert = new AlertView(null, null, "取消", new String[]{"确定"}, null, context, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if(position == 0) {
                    listener.confirmBack();
                }
            }
        });
        alert.addExtView(alarmView);
    }

    public void message(String msg) {
        alarmText.setText(msg);
        alert.show();
    }

    public interface ConfirmListener{
        void confirmBack();
    }
}
