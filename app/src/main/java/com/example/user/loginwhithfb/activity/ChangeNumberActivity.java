package com.example.user.loginwhithfb.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.model.UsersInfoTable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by POSTER on 05.07.2017.
 */

public class ChangeNumberActivity extends AppCompatActivity {
    @BindView(R.id.set_new_number) EditText numberEdit;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private String userUid;
    private ProgressDialog progressDialog;
    private String userKey;
    private UsersInfoTable userModel;
    private boolean flag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_number);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("UserLoginInfoTable");
        userUid = user.getUid();
        showProgressDialog();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    if (userUid.equals(data.getValue(UsersInfoTable.class).getuID())){
                        userKey = data.getKey();
                        userModel = data.getValue(UsersInfoTable.class);
                        numberEdit.setText(String.valueOf(userModel.getMobileNumber()));
                        hideProgressDialog();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.accept_changes_menu_btn){
            changeNumber(Integer.valueOf(numberEdit.getText().toString()), userKey, userModel);
        }
        onBackPressed();
        return true;
    }

    private void changeNumber(int number, String key, UsersInfoTable model){
        model.setMobileNumber(number);
        reference.child(key).setValue(model);
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_change_pass, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
