package com.mad.sristores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.sristores.model.Users;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumberET, passwordET;
    private Button loginBtn;
    private String parentDbName = "Users";
    //    private CheckBox isRememberMe;
    private TextView adminLink, notAdminLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = (Button) findViewById(R.id.login_btn);
        phoneNumberET = (EditText) findViewById(R.id.login_phone_number_input);
        passwordET = (EditText) findViewById(R.id.login_password_input);
        adminLink = (TextView) findViewById(R.id.admin_panel_link);
        notAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);
//        isRememberMe = (CheckBox) findViewById(R.id.remember_checkbox);
//        Paper.init(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setText("Login Admin");
                adminLink.setVisibility(view.INVISIBLE);
                notAdminLink.setVisibility(view.VISIBLE);
                parentDbName = "Admins";
            }
        });
        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setText("Login");
                adminLink.setVisibility(view.VISIBLE);
                notAdminLink.setVisibility(view.INVISIBLE);
                parentDbName = "Users";
            }
        });
    }


    private void loginUser() {

        String phone = phoneNumberET.getText().toString();
        String password = passwordET.getText().toString();

        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
        }else {


            allowAccessToAccount(phone, password);
        }
    }

    private void allowAccessToAccount(String phone, String password) {

//        if (isRememberMe.isChecked()){
//            Paper.book().write(Prevalent.UserPhoneKey, phone);
//            Paper.book().write(Prevalent.UserPasswordKey, password);
//
//        }

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(phone).exists()){

                    Users users = snapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if (users.getPhone().equals(phone) && users.getPassword().equals(password)) {

                        if (parentDbName.equals("Admins")){
                            Toast.makeText(LoginActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);
                        }else if (parentDbName.equals("Users")){
                            Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Please check the Credentials!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this, "Account with this " + phone + "number do not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}