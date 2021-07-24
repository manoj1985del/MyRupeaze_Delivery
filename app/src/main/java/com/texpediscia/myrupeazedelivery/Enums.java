package com.texpediscia.myrupeazedelivery;

public class Enums {

    public enum LoginMethod{
        PHONE,
        EMAIL,
        GOOGLE
    }

    public enum CartOperations{
        AddQuantity,
        RemoveQuantity,
        RemoveFromCart
    }

    public enum OrderOperations{
        NoOperation,
        CancelOrder
    }

    public enum ReplacementStatus{
        PENDING,
        COMPLETED,
        REJECTED
    }
}
