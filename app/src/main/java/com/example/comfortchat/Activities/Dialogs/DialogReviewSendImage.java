package com.example.comfortchat.Activities.Dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.comfortchat.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jsibbold.zoomage.ZoomageView;

public class DialogReviewSendImage {

    private Context context;
    private Dialog dialog;
    private Bitmap bitmap;
    private ZoomageView image;
    private FloatingActionButton send;


    public DialogReviewSendImage(Context context, Bitmap bitmap) {

        this.context = context;
        this.bitmap = bitmap;
        this.dialog = new Dialog(context);
        initialize();

    }


    public void initialize(){

        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.activity_review_send_image);

        (dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);

        image = dialog.findViewById(R.id.zoomSendPhoto);
        send = dialog.findViewById(R.id.ic_sendFloating);

    }

    public void show(OnCallBack onCallBack){

        dialog.show();
        image.setImageBitmap(bitmap);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallBack.onClickedSendButton();
                dialog.dismiss();
            }
        });

    }

    public interface OnCallBack{

        void onClickedSendButton();

    }



}
