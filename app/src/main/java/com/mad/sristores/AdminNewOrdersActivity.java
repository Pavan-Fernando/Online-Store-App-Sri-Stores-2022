package com.mad.sristores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.sristores.model.Orders;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference orderReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        orderReference = FirebaseDatabase.getInstance().getReference().child("Orders");
        orderList = findViewById(R.id.orders_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Orders> options = new FirebaseRecyclerOptions.Builder<Orders>()
                .setQuery(orderReference, Orders.class)
                .build();

        FirebaseRecyclerAdapter<Orders, OrdersViewHolder> adapter = new FirebaseRecyclerAdapter<Orders, OrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrdersViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Orders model) {

                holder.username.setText("Name: " + model.getName());
                holder.userPhone.setText("Phone: " + model.getPhone());
                holder.userTotalAmount.setText("Total: " + model.getTotalAmount() + "$");
                holder.userDateTime.setText("Date: " + model.getDate() + ", " + model.getTime());
                holder.userAddressCity.setText("Shipping Address: " + model.getAddress() + ", " + model.getCity());

                String userId = getRef(position).getKey();

                holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserProductsActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence options[] = new CharSequence[]{
                          "Yes",
                          "No"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                        builder.setTitle("Have you shipped the Order? ");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (i==0){
                                    removeOrder(userId);
                                }
                                else {
                                    finish();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
                return new OrdersViewHolder(view);
            }
        };
        orderList.setAdapter(adapter);
        adapter.startListening();
    }

    private void removeOrder(String userId) {
        orderReference.child(userId).removeValue();
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder{

        public TextView username, userPhone, userTotalAmount, userDateTime, userAddressCity;
        public Button showOrdersBtn;

        public OrdersViewHolder(View view){
            super(view);

            showOrdersBtn = view.findViewById(R.id.show_all_orders);
            username = view.findViewById(R.id.user_name);
            userPhone = view.findViewById(R.id.order_phone_number);
            userTotalAmount = view.findViewById(R.id.order_total_price);
            userDateTime = view.findViewById(R.id.order_date_time);
            userAddressCity = view.findViewById(R.id.order_address_city);
        }
    }
}