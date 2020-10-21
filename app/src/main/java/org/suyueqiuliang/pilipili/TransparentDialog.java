package org.suyueqiuliang.pilipili;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;


public class TransparentDialog {
        public static Dialog createLoadingDialog(Context context,Bitmap user_head,UserInformation userInformation) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.user_information_dialog, null);
            ConstraintLayout layout = v.findViewById(R.id.user_information_dialog_constraintLayout);
            ImageView userHead = v.findViewById(R.id.user_information_dialog_user_head);
            TextView userName = v.findViewById(R.id.user_information_dialog_user_name);
            userHead.setImageBitmap(user_head);
            userName.setText(userInformation.name);
            //userName.setTextColor(Color.parseColor(userInformation.nickname_color));
            Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
            loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            return loadingDialog;
        }

    }