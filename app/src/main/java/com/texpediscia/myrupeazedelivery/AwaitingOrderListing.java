package com.texpediscia.myrupeazedelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.texpediscia.myrupeazedelivery.model.Orders;

public class AwaitingOrderListing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awaiting_order_listing);
    }

    private Orders mOrder;

    public AwaitingOrderListing(Orders order){
        mOrder = order;
    }

    public Orders getOrder(){
        return mOrder;
    }
}
