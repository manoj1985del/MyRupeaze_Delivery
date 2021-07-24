package com.texpediscia.myrupeazedelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.texpediscia.myrupeazedelivery.model.Seller;

public class SellerListing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_listing);
    }

    private Seller mSeller;

    public SellerListing(Seller seller){
        this.mSeller = seller;
    }

    public Seller getSeller(){
        return this.mSeller;
    }
}
