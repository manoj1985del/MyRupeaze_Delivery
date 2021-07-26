package com.texpediscia.myrupeazedelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.texpediscia.myrupeazedelivery.model.Orders;
import com.texpediscia.myrupeazedelivery.model.PharmacistRequests;

public class AwaitingOrderListing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awaiting_order_listing);
    }

    private Orders mOrder;
    private PharmacistRequests pharmacistRequests;

    public AwaitingOrderListing(Orders order){
        mOrder = order;
    }

    public AwaitingOrderListing(PharmacistRequests requests){
        pharmacistRequests = requests;
    }

    public Orders getOrder(){
        return mOrder;
    }

    public PharmacistRequests getPharmaRequest(){
        return pharmacistRequests;
    }
}
