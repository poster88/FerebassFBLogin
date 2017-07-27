package com.example.user.loginwhithfb.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.user.loginwhithfb.MyValueEventListener;
import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.model.UserLoginInfoTable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import butterknife.BindView;

/**
 * Created by User on 027 27.07.17.
 */

public class SentPassToEmailActivity extends BaseActivity{
    @BindView(R.id.layout_sent_pass_email) TextInputLayout inputLayoutSentEmail;
    @BindView(R.id.sent_pass_email) EditText setEmail;

    private DatabaseReference ref;
    private OnCompleteListener onCompleteListenerSentPass = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                SentPassToEmailActivity.super.showToast(SentPassToEmailActivity.this, "Password sent to your email.");
                finish();
            }else {
                SentPassToEmailActivity.super.showToast(SentPassToEmailActivity.this, "Fail to sent : " + task.getException().getMessage());
            }
        }
    };

    private DialogInterface.OnClickListener onClickListenerSentEmail = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            sentPassword(setEmail.getText().toString());

        }
    };
    private DialogInterface.OnClickListener onClickListenerCancel = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };
    private MyValueEventListener onSentEmailListener = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            SentPassToEmailActivity.this.hideProgressDialog();
            if (dataSnapshot.exists()){
                for (DataSnapshot result: dataSnapshot.getChildren()) {
                    setPositiveAlertDialog(result.getValue(UserLoginInfoTable.class).getEmail());
                    break;
                }
            }else {
                SentPassToEmailActivity.super.showToast(SentPassToEmailActivity.this, "Where is no " + setEmail.getText().toString() + " in DB.");
                inputLayoutSentEmail.setError(getString(R.string.err_msg_check_email));
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_pass_to_email);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.setActivityForBinder(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accept_changes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        super.hideSoftKeyboard(this);
        if (id == android.R.id.home){
            finish();
            return true;
        }
        if (id == item.getItemId()){
            checkUserData();
        }
        return true;
    }

    private void checkUserData() {
        if (super.user == null || super.user.isAnonymous()){
            findUserEmailInDB();
        }else {
            sentPassword(super.user.getEmail());
        }
    }

    private void findUserEmailInDB() {
        ref = super.database.getReference(USER_INFO_TABLE);
        super.showProgressDialog("Checkout user email in DB ...");
        Query query = ref.orderByChild("email").equalTo(setEmail.getText().toString());
        query.addListenerForSingleValueEvent(onSentEmailListener);
    }

    private void setPositiveAlertDialog(String email){
        SentPassToEmailActivity.super.showAlertDialog(
                "Send email", "Sent pass to the " + email + "?", android.R.drawable.ic_menu_info_details,
                false, "OK", "Cancel", onClickListenerSentEmail, onClickListenerCancel
        );
    }

    private void sentPassword(String email){
        super.auth.sendPasswordResetEmail(email).addOnCompleteListener(onCompleteListenerSentPass);
    }
}
