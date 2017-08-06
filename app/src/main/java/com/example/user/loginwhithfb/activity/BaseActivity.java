package com.example.user.loginwhithfb.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.user.loginwhithfb.event.UpdateCompanyUI;
import com.example.user.loginwhithfb.lisntener.MyValueEventListener;
import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.event.UpdateItem;
import com.example.user.loginwhithfb.eventbus.BusProvider;
import com.example.user.loginwhithfb.event.UpdateUIEvent;
import com.example.user.loginwhithfb.model.CompaniesInfoTable;
import com.example.user.loginwhithfb.model.UserLoginInfoTable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by POSTER on 17.07.2017.
 */

public class BaseActivity extends AppCompatActivity {

    protected final String USER_INFO_TABLE = "UserLoginInfoTable";
    protected final String COMP_INF_TABLE = "CompaniesInfoTable";
    protected final String TAG_HOME = "Products catalog";
    protected final String TAG_ACCOUNT = "My account";
    protected final String TAG_ORDER = "My orders";
    protected final String TAG_CHAT = "Company chat";
    protected final String TAG_FAVORITE = "Favorite";
    protected final String TAG_NEWS = "News";
    protected final String TAG_INFORMATION = "Information";
    protected String CURRENT_TAG = TAG_HOME;
    protected ProgressDialog progressDialog;

    protected FirebaseDatabase database = FirebaseDatabase.getInstance();
    protected FirebaseAuth auth = FirebaseAuth.getInstance();
    protected FirebaseUser user = auth.getCurrentUser();

    public static UserLoginInfoTable userModel;
    public static Query userRef;

    public static CompaniesInfoTable companiesInfoTable;
    public static Query refCompanyTable;

    private boolean isUserClickedBackButton = false;

    protected MyValueEventListener onUidUserDataListener = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot data: dataSnapshot.getChildren()){
                userModel = data.getValue(UserLoginInfoTable.class);
                BusProvider.getInstance().post(new UpdateUIEvent());
            }
        }
    };

    protected MyValueEventListener onCompanyInfoTableListener = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            companiesInfoTable = dataSnapshot.getValue(CompaniesInfoTable.class);
            BusProvider.getInstance().post(new UpdateCompanyUI());
        }
    };

    protected void setActivityForBinder(Activity activity){
        ButterKnife.bind(activity);
    }

    protected void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(msg);
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    protected void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected void showToast(Context context, String title){
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
    }

    protected void startCurActivity(Context packageContext, Class<?> cls){
        startActivity(new Intent(packageContext, cls));
    }

    private void setExitTimer(){
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                isUserClickedBackButton = false;
            }
        }.start();
    }

    protected void exitProgram(){
        if (!isUserClickedBackButton){
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            isUserClickedBackButton = true;
        }else {
            super.onBackPressed();
        }
        setExitTimer();
    }

    protected void showAlertDialog(String title, String message, int icon, boolean cancelable, String positiveBtnTitle, String negativeBtnTitle, DialogInterface.OnClickListener posBtnClickListener, DialogInterface.OnClickListener negBtnClickListener) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle(title);
        ab.setMessage(message);
        ab.setIcon(icon);
        ab.setCancelable(cancelable);
        ab.setPositiveButton(positiveBtnTitle, posBtnClickListener);
        ab.setNegativeButton(negativeBtnTitle, negBtnClickListener);
        ab.show();
    }

    protected void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }


    protected boolean isValidEmail(EditText eMail, TextInputLayout inputLayoutEmail) {
        String mail = eMail.getText().toString().trim();
        if (TextUtils.isEmpty(mail)){
            inputLayoutEmail.setError(getString(R.string.err_msg_empty_email));
            requestFocus(eMail);
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(eMail);
            return false;
        }
        inputLayoutEmail.setErrorEnabled(false);
        return true;
    }

    protected boolean isValidPassword(EditText password, TextInputLayout inputLayoutPassword, EditText repPassword, TextInputLayout inputLayoutRepPassword){
        if (checkLengthPass(password, inputLayoutPassword) && checkLengthPass(repPassword, inputLayoutRepPassword)){
            if (password.getText().toString().equals(repPassword.getText().toString())){
                return true;
            }
            password.setText("");
            repPassword.setText("");
            requestFocus(password);
            inputLayoutPassword.setError(getString(R.string.err_msg_check_pass));
        }
        return false;
    }

    protected boolean checkLengthPass(EditText password, TextInputLayout inputLayoutPassword) {
        if (password.getText().toString().trim().isEmpty()){
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(password);
            return false;
        }
        if (password.getText().length() < 6){
            inputLayoutPassword.setError(getString(R.string.err_msg_password_length));
            requestFocus(password);
            return false;
        }
        inputLayoutPassword.setErrorEnabled(false);
        return true;
    }
    protected boolean isValidNumber(EditText number, TextInputLayout inputLayoutMobNum){
        if (number.getText().length() == 0){
            inputLayoutMobNum.setError(getString(R.string.err_msg_mob_number));
            requestFocus(number);
            return false;
        }
        inputLayoutMobNum.setErrorEnabled(false);
        return true;
    }

    protected boolean isValidPersonalData(EditText data, TextInputLayout inputLayoutData){
        if (data.getText().toString().trim().isEmpty()){
            inputLayoutData.setError(getString(R.string.err_msg_data));
            requestFocus(data);
            return false;
        }
        inputLayoutData.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    protected void loadImage(Uri uri, ImageView view){
        Glide.with(this).load(uri).crossFade().thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(view);
    }

    protected String createStringText(Object o){
        ArrayList<String> keys = ((ArrayList<String>) o);
        StringBuilder builder = new StringBuilder();
        for (String value : keys) builder.append(value).append(", ");
        if (keys.size()> 0) builder.deleteCharAt(builder.length() - 2);
        return builder.toString();
    }
}
