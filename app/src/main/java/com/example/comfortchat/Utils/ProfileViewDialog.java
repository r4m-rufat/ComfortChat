package com.example.comfortchat.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.media.Image;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.comfortchat.Models.ChatList;
import com.example.comfortchat.R;
import com.jsibbold.zoomage.ZoomageView;

public class ProfileViewDialog {

    public Context context;

    public ProfileViewDialog(Context context, ChatList chatList){
        this.context = context;
        showProfileOnPopupDialog(chatList);
    }

    public void showProfileOnPopupDialog(ChatList chatList){

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_view_profile);

        dialog.setCancelable(true);

        TextView name = dialog.findViewById(R.id.pofileDialogName);
        ImageView image = dialog.findViewById(R.id.imageForPopupDialog);
        ImageView close = dialog.findViewById(R.id.closePopupDialog);

        name.setText(chatList.getUsername());

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Glide.with(context).load(chatList.getUrlProfile()).into(image);

        Dialog showProfile = new Dialog(context, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        showProfile.requestWindowFeature(Window.FEATURE_NO_TITLE);
        showProfile.setContentView(R.layout.layout_dialog_view_full_profile);

        TextView profileName = showProfile.findViewById(R.id.profileNamePopupDialog);
        ZoomageView zoomImage = showProfile.findViewById(R.id.fullImageForPopup);
        ImageView backArrow = showProfile.findViewById(R.id.ic_backFullImage);


        profileName.setText(chatList.getUsername());

        Glide.with(context).load(chatList.getUrlProfile()).into(zoomImage);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                showProfile.show();

            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfile.dismiss();
            }
        });

        dialog.show();

    }

}
