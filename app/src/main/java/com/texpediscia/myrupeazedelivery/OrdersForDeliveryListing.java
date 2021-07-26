package com.texpediscia.myrupeazedelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.texpediscia.myrupeazedelivery.model.Orders;
import com.texpediscia.myrupeazedelivery.model.PharmacistRequests;

public class OrdersForDeliveryListing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_for_delivery_listing);
    }

    private Orders mOrder;
    private PharmacistRequests pharmacistRequests;

    public OrdersForDeliveryListing(Orders order){
        mOrder = order;
    }

    public Orders getOrder(){
        return mOrder;
    }

    public OrdersForDeliveryListing(PharmacistRequests requests){
        pharmacistRequests = requests;
    }

    public PharmacistRequests getPharmacistRequests(){
        return pharmacistRequests;
    }
}
