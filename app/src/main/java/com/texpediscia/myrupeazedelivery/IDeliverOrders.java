package com.texpediscia.myrupeazedelivery;

import com.texpediscia.myrupeazedelivery.model.Orders;

public interface IDeliverOrders {

    public void DeliverOrder(Orders order);

    public void RejectOrder(Orders order, String reason);

}
