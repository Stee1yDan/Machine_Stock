package com.example.wessocketstockapp.enumeration;

public enum RequestType
{
    subscribe("subscribe"),
    unsubscribe("unsubscribe");

    private String action;

    RequestType(String action)
    {
        this.action = action;
    }
}
