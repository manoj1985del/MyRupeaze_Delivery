package com.texpediscia.myrupeazedelivery.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Orders implements Serializable {

    public boolean COD;

    public String Status;

    public boolean cancelled;

    public String customer_id;

    public Date delivery_date;

    public Date order_date;

    public String order_id;

    public String payment_id;


    public String cancellation_reason;

    public boolean IsDelivered = false;

    public String seller_id;

    public int wallet_money_used;

    public Seller seller;

    public User user;



    public boolean prepaid_cancellation_processed = false;

    public String original_order_id;

    public boolean replacement_order;

}
