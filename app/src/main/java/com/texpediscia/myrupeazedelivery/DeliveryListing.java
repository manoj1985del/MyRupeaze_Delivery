package com.texpediscia.myrupeazedelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.texpediscia.myrupeazedelivery.model.Seller;
import com.texpediscia.myrupeazedelivery.model.User;

public class DeliveryListing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_listing);
    }

    private User mUser;

    public DeliveryListing(User user){
        this.mUser = user;
    }

    public User getmUser(){
        return this.mUser;
    }
}
