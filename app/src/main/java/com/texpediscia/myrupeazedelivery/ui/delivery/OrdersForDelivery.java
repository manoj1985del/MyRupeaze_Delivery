package com.texpediscia.myrupeazedelivery.ui.delivery;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pl.droidsonroids.gif.GifImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.texpediscia.myrupeazedelivery.AdapterAwaitingOrders;
import com.texpediscia.myrupeazedelivery.AdapterOrdersForDelivery;
import com.texpediscia.myrupeazedelivery.CommonVariables;
import com.texpediscia.myrupeazedelivery.DeliveryListing;
import com.texpediscia.myrupeazedelivery.IDeliverOrders;
import com.texpediscia.myrupeazedelivery.OrdersForDeliveryListing;
import com.texpediscia.myrupeazedelivery.R;
import com.texpediscia.myrupeazedelivery.model.Orders;
import com.texpediscia.myrupeazedelivery.model.Seller;
import com.texpediscia.myrupeazedelivery.model.User;

import java.util.ArrayList;
import java.util.List;

public class OrdersForDelivery extends Fragment implements IDeliverOrders {

    private OrdersForDeliveryViewModel mViewModel;
    private View mView;

    private List<OrdersForDeliveryListing> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GifImageView gifView;
    private User mCustomer;
    IDeliverOrders event;


    public static OrdersForDelivery newInstance() {
        return new OrdersForDelivery();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.orders_for_delivery_fragment, container, false);
        event = this;
        return  mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(OrdersForDeliveryViewModel.class);
        // TODO: Use the ViewModel

        listItems = new ArrayList<>();
        recyclerView = (RecyclerView) mView.findViewById(R.id.orderRecycler);
        gifView = mView.findViewById(R.id.gifView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));

        if(CommonVariables.selectedUser == null){
            return;
        }
        else{
            mCustomer = CommonVariables.selectedUser;
            loadOrders();
        }

//        Intent iin = getActivity().getIntent();
//        Bundle b = iin.getExtras();
//
//        if (b != null) {
//            mCustomer = (User) b.get("Customer");
//            loadOrders();
//        }

    }

    private void loadOrders(){
        listItems = new ArrayList<>();
        for (Orders ord : mCustomer.ordersList) {

            OrdersForDeliveryListing item = new OrdersForDeliveryListing(ord);
            listItems.add((item));
        }

        adapter = new AdapterOrdersForDelivery(listItems, mView.getContext(), event);
        recyclerView.setAdapter(adapter);

        gifView.setVisibility(View.GONE);

    }

    @Override
    public void DeliverOrder(final Orders order) {

        DocumentReference washingtonRef = db.collection("orders").document(order.order_id);

// Set the "isCapital" field of the city 'DC'
        washingtonRef
                .update(
                        "Status", "Delivered",
                        "delivery_date", FieldValue.serverTimestamp()
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        int index = 0;
                        for (Orders ord: mCustomer.ordersList) {
                            if(ord.order_id.equals(order.order_id)){
                                break;
                            }
                            index++;
                        }
                        mCustomer.ordersList.remove(index);
                        loadOrders();
                        Toast.makeText(mView.getContext(), "Order delivery successful", Toast.LENGTH_SHORT).show();

                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error updating document", e);
                    }
                });

    }

    @Override
    public void RejectOrder(final Orders order, final String reason) {

        DocumentReference washingtonRef = db.collection("orders").document(order.order_id);

// Set the "isCapital" field of the city 'DC'
        washingtonRef
                .update(
                        "Status", "Cancelled",
                        "cancellation_reason", reason,
                        "pickup_status", "attempted delivery failed"
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        int index = 0;
                        for (Orders ord: mCustomer.ordersList) {
                            if(ord.order_id.equals(order.order_id)){
                                break;
                            }
                            index++;
                        }
                        mCustomer.ordersList.remove(index);
                        loadOrders();
                        Toast.makeText(mView.getContext(), "Order Cancellation successful. Reason : " + reason, Toast.LENGTH_SHORT).show();

                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }
}
