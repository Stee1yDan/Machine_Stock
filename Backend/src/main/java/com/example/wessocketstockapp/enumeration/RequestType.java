package com.example.wessocketstockapp.enumeration;

public enum RequestType
{
    subscribe("subscribe"),
    unsubscribe("unsubscribe"),
    trainModel("trainModel"),
    giveInfoForPrediction("giveInfoForPrediction");


    private String action;

    RequestType(String action)
    {
        this.action = action;
    }
}
