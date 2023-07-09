package com.example.codtwt;

public class ChatMessage {
    private String name;
    private String message;
    private String url;
    private String msgId;
    private int likeCount;

    public String getUrl() {
        return url;
    }

    public String getMsgId() {
        return msgId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    ChatMessage (String name, String message, String url, String msgId, int likeCount){
        this.name=name;
        this.message=message;
        this.url=url;
        this.msgId=msgId;
        this.likeCount=likeCount;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
