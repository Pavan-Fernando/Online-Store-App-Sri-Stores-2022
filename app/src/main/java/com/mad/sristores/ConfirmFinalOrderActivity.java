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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.sristores.prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText shipmentName, shipmentPhone, shipmentAddress, shipmentCity;
    private Button confirmOderBtn;
    private String totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Amount: " + totalAmount, Toast.LENGTH_SHORT).show();

        confirmOderBtn = (Button) findViewById(R.id.confirm_final_order_btn);
        shipmentName = (EditText) findViewById(R.id.shipment_name);
        shipmentPhone = (EditText) findViewById(R.id.shipment_phone);
        shipmentAddress = (EditText) findViewById(R.id.shipment_address);
        shipmentCity = (EditText) findViewById(R.id.shipment_city);

        confirmOderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateShipmentDetails();
            }
        });
    }

    private void validateShipmentDetails() {

        if (TextUtils.isEmpty(shipmentName.getText().toString())){
            Toast.makeText(this, "Please enter the name.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(shipmentPhone.getText().toString())){
            Toast.makeText(this, "Please enter the phone number.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(shipmentAddress.getText().toString())){
            Toast.makeText(this, "Please enter the address.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(shipmentCity.getText().toString())){
            Toast.makeText(this, "Please enter the city.", Toast.LENGTH_SHORT).show();
        }
        else {
            confirmOrder();
        }
    }

    private void confirmOrder() {

        final String saveCurrentDate, saveCurrentTime;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        final DatabaseReference orderReference = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("totalAmount", totalAmount);
        orderMap.put("name", shipmentName.getText().toString());
        orderMap.put("phone",  shipmentPhone.getText().toString());
        orderMap.put("address",  shipmentAddress.getText().toString());
        orderMap.put("city",  shipmentCity.getText().toString());
        orderMap.put("date",  saveCurrentDate);
        orderMap.put("time",  saveCurrentTime);
        orderMap.put("state", "not shipped");

        orderReference.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Your Order is Placed.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }
}