package com.mad.sristores;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button joinBtn, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinBtn = (Button) findViewById(R.id.join_btn);
        loginBtn = (Button) findViewById(R.id.login_btn);

//        Paper.init(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

//        String userPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
//        String userPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
//
//        if (userPhoneKey != "" && userPasswordKey != ""){
//            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey)){
//                System.out.println(userPhoneKey + userPasswordKey);
//                Toast.makeText(this, "Please wait, while we are checking the credentials.", Toast.LENGTH_SHORT).show();
//                allowAccess(userPhoneKey, userPasswordKey);
//            }
//        }
//    }
//
//    private void allowAccess(String phone, String password) {
//
//        final DatabaseReference rootRef;
//        rootRef = FirebaseDatabase.getInstance().getReference();
//
//        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child("Users").child(phone).exists()){
//
//                    Users users = snapshot.child("Users").child(phone).getValue(Users.class);
//                    if (users.getPhone().equals(phone) && users.getPassword().equals(password)) {
//
//                        Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                        startActivity(intent);
//                    }
//                    else {
//                        Toast.makeText(MainActivity.this, "Please check the Credentials!", Toast.LENGTH_SHORT).show();
//                    }
//                }else {
//                    Toast.makeText(MainActivity.this, "Account with this " + phone + "number do not exists", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }
}