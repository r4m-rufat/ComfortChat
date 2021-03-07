package com.example.comfortchat.Models;

public class Chats {

        private String sender;
        private String receiver;
        private String type;
        private String dateTime;
        private String message;
        private String imageUrl;

        public Chats(){

        }

    public Chats(String sender, String receiver, String type, String dateTime, String message, String imageUrl) {
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.dateTime = dateTime;
        this.message = message;
        this.imageUrl = imageUrl;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
