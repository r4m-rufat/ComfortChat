package com.example.comfortchat.Services;

import com.example.comfortchat.Models.Chats;

import java.util.List;

public interface OnReadChatInterface {

    void OnReadChatSuccess(List<Chats> chats);
    void OnReadChatFailed();

}
