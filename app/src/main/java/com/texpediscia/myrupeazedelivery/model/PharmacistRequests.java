package com.texpediscia.myrupeazedelivery.model;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PharmacistRequests implements Serializable {

    public String customer_id;
    public String customer_name;
    public String customer_phone;
    public String customer_address_line1;
    public String customer_address_line2;
    public String customer_address_line3;
    public String customer_pin;
    public String customer_city;
    public String customer_state;

    public User user;

    public String seller_id;
    public String invoice_id;
    public String company_name;
    public String seller_phone;
    public String seller_address_line1;
    public String seller_address_line2;
    public String seller_address_line3;
    public String seller_city;
    public String seller_state;
    public String seller_pin;
    public String fcm_medical;
    public String delivery_agent_id;
    public List<String> product_ids = new ArrayList<>();
    public List<String> product_names = new ArrayList<>();
    public List<Long> product_qty = new ArrayList<>();
    public List<Float> product_prices = new ArrayList<>();
    public List<Float> gst_list = new ArrayList<>();
    public List<Float> product_prices_total = new ArrayList<>();
    public List<String> available_status = new ArrayList<>();
    public String buyer_rejection_reason;
    public String seller_rejection_reason;
    public Timestamp timestamp;
    public String pickup_timestamp;
    public Date invoice_timestamp;
    public int status_code;
    public boolean pickup_from_store;
    public String doc_id;
    public boolean pay_by_cash;
    public String payment_id;
    public double paid_by_card;
    public boolean cod;
    public double wallet_money_used;
    public String prescription_type;
    public Boolean cancelled;
    public String note_to_seller;
    public String Status;
    public String  pickup_rejection_reason;
    public String pickup_status;
    public String prescription_url;
    public Date delivery_date;
    public boolean settlement_done = false;

    public PharmacistRequests() { }
}


