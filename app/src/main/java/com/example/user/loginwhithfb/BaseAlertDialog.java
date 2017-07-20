package com.example.user.loginwhithfb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StyleRes;

/**
 * Created by User on 020 20.07.17.
 */

public class BaseAlertDialog extends AlertDialog implements DialogInterface.OnClickListener {
    private String title;
    private String message;
    private Context context;
    private int icon;
    private String positiveBtnTitle;
    private String negativeBtnTitle;

    private DialogInterface.OnClickListener posBtnListener;
    private DialogInterface.OnClickListener negBtnListener;
    private boolean cancelablealert;

    protected BaseAlertDialog(Context context) {
        super(context);
    }

    protected BaseAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected BaseAlertDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected void showAlert(){
        AlertDialog.Builder ab = new AlertDialog.Builder(context);
        ab.setTitle(title);
        ab.setMessage(message);
        ab.setIcon(icon);
        ab.setCancelable(cancelablealert);
        ab.setPositiveButton(positiveBtnTitle, posBtnListener);
        ab.setNegativeButton(negativeBtnTitle, negBtnListener);
        ab.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
