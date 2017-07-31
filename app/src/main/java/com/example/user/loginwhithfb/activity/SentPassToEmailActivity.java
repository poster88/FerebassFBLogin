package com.example.user.loginwhithfb.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.user.loginwhithfb.lisntener.MyValueEventListener;
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
        initUserData();
    }

    private void initUserData() {
        if (!super.user.isAnonymous()){
            setEmail.setText(userModel.getEmail());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accept_changes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.hideSoftKeyboard(this);
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        if (item.getItemId() == item.getItemId()){
            checkUserData();
        }
        return true;
    }

    private void checkUserData() {
        if (super.isValidEmail(setEmail, inputLayoutSentEmail)){
            if (super.user == null || super.user.isAnonymous() ) {
                findUserEmailInDB();
                return;
            }
            setPositiveAlertDialog(setEmail.getText().toString());
        }
    }

    private void findUserEmailInDB() {
        ref = super.database.getReference(USER_INFO_TABLE);
        super.showProgressDialog("Checkout user email in DB ...");
        Query query = ref.orderByChild("email").equalTo(setEmail.getText().toString());
        query.addListenerForSingleValueEvent(onSentEmailListener);
    }

    private void setPositiveAlertDialog(String email){
        super.showAlertDialog("Send email", "Send pass to the " + email + " ?", android.R.drawable.ic_menu_info_details,
                false, "Send", "Cancel", onClickListenerSentEmail, onClickListenerCancel
        );
    }

    private void sentPassword(String email){
        super.auth.sendPasswordResetEmail(email).addOnCompleteListener(onCompleteListenerSentPass);
    }
}
