package com.mad.sristores;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, proName,proDescription, proQuantity, proPrice, saveCurrentDate, saveCurrentTime, productRandomKey, downloadImageUrl;
    private Button addProductBtn;
    private EditText productName, productDescription, productQuantity, productPrice;
    private ImageView productImage;
    private static final int Gallery_Picked = 1;
    private Uri imageUri;
    private StorageReference productImageRef;
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        categoryName = getIntent().getExtras().get("category").toString();
        productImageRef = FirebaseStorage.getInstance().getReference().child("Product Image");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productImage = (ImageView) findViewById(R.id.product_image);
        productName = (EditText) findViewById(R.id.product_name);
        productDescription = (EditText) findViewById(R.id.product_description);
        productQuantity = (EditText) findViewById(R.id.product_quantity);
        productPrice = (EditText) findViewById(R.id.product_price);
        addProductBtn = (Button) findViewById(R.id.add_product_btn);

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productValidate();
            }
        });
    }

    private void productValidate() {
        proName = productName.getText().toString();
        proDescription = productDescription.getText().toString();
        proQuantity = productQuantity.getText().toString();
        proPrice = productPrice.getText().toString();
        
        if (imageUri == null){
            Toast.makeText(this, "Product Image is Required!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(proName)){
            Toast.makeText(this, "Product Name is Required!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(proDescription)){
            Toast.makeText(this, "Product Description is Required!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(proQuantity)){
            Toast.makeText(this, "Product Quantity is Required!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(proPrice)){
            Toast.makeText(this, "Product Price is Required!", Toast.LENGTH_SHORT).show();
        }else {
            storeNewProductDetails();
        }
    }

    private void storeNewProductDetails() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;
        StorageReference filePath = productImageRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminAddNewProductActivity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "got the product image Url successfully", Toast.LENGTH_SHORT).show();
                            saveProductInfoToDB();
                        }
                    }
                });
            }
        });
    }

    private void saveProductInfoToDB() {

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("productId", productRandomKey);
        productMap.put("productName", proName);
        productMap.put("productDescription", proDescription);
        productMap.put("productImage", downloadImageUrl);
        productMap.put("productQuantity", proQuantity);
        productMap.put("productPrice", proPrice);
        productMap.put("productCategory", categoryName);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);

        productRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Intent intent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);

                            Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(AdminAddNewProductActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void setProductImage(View v){
        openGallery();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, Gallery_Picked);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_Picked && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            productImage.setImageURI(imageUri);
        }
    }
}