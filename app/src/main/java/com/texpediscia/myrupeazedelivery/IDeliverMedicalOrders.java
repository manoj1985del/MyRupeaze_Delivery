package com.texpediscia.myrupeazedelivery;

import com.texpediscia.myrupeazedelivery.model.Orders;
import com.texpediscia.myrupeazedelivery.model.PharmacistRequests;

public interface IDeliverMedicalOrders {

    public void DeliverOrder(PharmacistRequests requests);

    public void RejectOrder(PharmacistRequests requests, String reason);

}
