package com.eblasting.Bean;

public class MsgBean {

    private String msgInfo;

    private String msgTime;

    public String getMsgTime() {
        return msgTime;
    }

    public String getMsgInfo() {
        return msgInfo;
    }

    public MsgBean(String msgInfo, String msgTime) {
        this.msgInfo = msgInfo;
        this.msgTime = msgTime;
    }

    public void setMsgInfo(String msgInfo) {

        this.msgInfo = msgInfo;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }
}
