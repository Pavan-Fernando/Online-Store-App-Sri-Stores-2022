package com.mad.sristores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.sristores.model.Products;
import com.mad.sristores.prevalent.Prevalent;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private FloatingActionButton addCartBtn;
    private ImageView productImage;
    private EditText selectedQuantity;
    private TextView productName, productDescription, productAvailableQuantity, productPrice;
    private String productId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productId = getIntent().getStringExtra("productId");

        addCartBtn = (FloatingActionButton) findViewById(R.id.add_product_cart_btn);
        productImage = (ImageView) findViewById(R.id.profile_image_details);
        selectedQuantity = (EditText) findViewById(R.id.selected_product_quantity);
        productName = (TextView) findViewById(R.id.product_details_name);
        productDescription = (TextView) findViewById(R.id.product_details_description);
        productAvailableQuantity = (TextView) findViewById(R.id.product_details_available_quantity);
        productPrice = (TextView) findViewById(R.id.product_details_price);

        getProductDetails(productId);

        addCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingToCart();
            }
        });
     }

    private void addingToCart() {

        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        final DatabaseReference cartListReference = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("productId", productId);
        cartMap.put("productName", productName.getText().toString());
        cartMap.put("price",  productPrice.getText().toString());
        cartMap.put("date",  saveCurrentDate);
        cartMap.put("time",  saveCurrentTime);
        cartMap.put("quantity",  selectedQuantity.getText().toString());
        cartMap.put("discount",  "");

        cartListReference.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(productId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            cartListReference.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                    .child("Products").child(productId)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            
                                            if (task.isSuccessful()){
                                                Toast.makeText(ProductDetailsActivity.this, "Added to the Cart", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void getProductDetails(String productId) {
        DatabaseReference productReference = FirebaseDatabase.getInstance().getReference().child("Products");
        productReference.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Products products = snapshot.getValue(Products.class);

                    productName.setText(products.getProductName());
                    productDescription.setText(products.getProductDescription());
                    productAvailableQuantity.setText(products.getProductQuantity());
                    productPrice.setText(products.getProductPrice());
                    Picasso.get().load(products.getProductImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}