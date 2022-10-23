package com.mad.sristores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private Button createBtn;
    private EditText usernameET, phoneNumberET, passwordET;
    //    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        createBtn = (Button) findViewById(R.id.sign_up_btn);
        usernameET = (EditText) findViewById(R.id.sign_up_username_input);
        phoneNumberET = (EditText) findViewById(R.id.sign_up_phone_number_input); //change the input
        passwordET = (EditText) findViewById(R.id.sign_up_password_input);
//        loadingBar = new ProgressDialog(this);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });
    }

    private void createUser() {

        String username = usernameET.getText().toString();
        String phone = phoneNumberET.getText().toString();
        String password = passwordET.getText().toString();

        if (TextUtils.isEmpty(username)){
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Please wait, while we are checking the credentials.", Toast.LENGTH_SHORT).show();
            validatePhoneNumber(username, phone, password);
        }
    }

    private void validatePhoneNumber(String username, String phone, String password) {

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!(snapshot.child("Users").child(phone).exists())){
                    System.out.println("user is not exists");
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone", phone);
                    userDataMap.put("username", username);
                    userDataMap.put("password", password);

                    rootRef.child("Users").child(phone).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "Your user account successfully created!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(SignUpActivity.this, "Something Wrong!! Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {

                    System.out.println("user is exists");
                    Toast.makeText(SignUpActivity.this, "This phone number: " + phone + " already exists.", Toast.LENGTH_SHORT).show();
//                    loadingBar.dismiss();
                    Toast.makeText(SignUpActivity.this, "Please try another phone number!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}