package com.example.wessocketstockapp.enumeration;

public enum StockTicker
{
    BITCOIN("BTC-USD");
    private String ticker;

    StockTicker(String ticker)
    {
        this.ticker = ticker;
    }
    public String getTicker() {
        return this.ticker;
    }

}
