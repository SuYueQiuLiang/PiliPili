package org.suyueqiuliang.pilipili;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;


public class TransparentDialog {
        @SuppressLint("SetTextI18n")
        public static Dialog createLoadingDialog(Context context,Bitmap user_head,UserInformation userInformation,LevelWalletInfo levelWalletInfo) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.user_information_dialog, null);
            ConstraintLayout layout = v.findViewById(R.id.user_information_dialog_constraintLayout);
            ImageView userHead = v.findViewById(R.id.user_information_dialog_user_head);
            TextView userName = v.findViewById(R.id.user_information_dialog_user_name);
            CardView vip = v.findViewById(R.id.user_information_dialog_vip_card);
            TextView level = v.findViewById(R.id.user_information_dialog_level);
            TextView expText = v.findViewById(R.id.user_information_dialog_exp_text);
            ProgressBar expProgress = v.findViewById(R.id.user_information_dialog_exp_progress);
            TextView coins = v.findViewById(R.id.user_information_dialog_coins_text);
            TextView wallet = v.findViewById(R.id.user_information_dialog_wallet_text);
            TextView subscribe = v.findViewById(R.id.user_information_dialog_coins_constraint_layout_subscribe);
            TextView fans = v.findViewById(R.id.user_information_dialog_coins_constraint_layout_fans);
            TextView trends = v.findViewById(R.id.user_information_dialog_coins_constraint_layout_trends);
            userHead.setImageBitmap(user_head);
            userName.setText(userInformation.name);
            if(userInformation.vip)
                vip.setVisibility(View.VISIBLE);
            level.setText(context.getString(R.string.level) + String.valueOf(userInformation.level));
            expText.setText(levelWalletInfo.current_exp + "/" + levelWalletInfo.next_exp);
            expProgress.setMax(levelWalletInfo.next_exp);
            expProgress.setProgress(levelWalletInfo.current_exp);
            coins.setText(String.valueOf(userInformation.coins));
            wallet.setText(String.valueOf(levelWalletInfo.wallet));
            //userName.setTextColor(Color.parseColor(userInformation.nickname_color));
            Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
            loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            return loadingDialog;
        }

    }