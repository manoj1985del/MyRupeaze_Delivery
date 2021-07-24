package com.texpediscia.myrupeazedelivery;

import com.texpediscia.myrupeazedelivery.model.Seller;

public interface ISellerOperations {

    public void SelectSeller(Seller seller);

    public void RejectOrders (Seller seller, String reason);

}
