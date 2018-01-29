package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.chat;

import java.util.Date;
import java.util.Random;

public class ChatMessage {

    private String body, sender, receiver, senderName;
    private Date dateTime;
    private String msgid;
    private boolean isMine;// Did I send the message.

    public ChatMessage(String sender, String receiver, String messageString,
                       String id, boolean isMine) {
        this.body = messageString;
        this.isMine = isMine;
        this.sender = sender;
        this.msgid = id;
        this.receiver = receiver;
        this.senderName = sender;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public void setMsgID() {

        msgid += "-" + String.format("%02d", new Random().nextInt(100));
        ;
    }
}
