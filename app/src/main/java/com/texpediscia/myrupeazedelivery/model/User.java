package com.texpediscia.myrupeazedelivery.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    public String Name;

    public String Email;

    public String Phone;

    public String Gender;

    public String AddressLine1;

    public String AddressLine2;

    public String AddressLine3;

    public String Landmark;

    public String Pincode;

    public String buyer_area_pin;

    public String City;

    public String State;

    public String AddressId;

    public List<Address> AddressList = new ArrayList<>();

    public String fcm;

    public double points;

    public String customer_id;

    public boolean Active;

    public String status;

    public List<Orders> ordersList = new ArrayList<>();

}
