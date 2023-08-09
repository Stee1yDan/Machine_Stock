package com.example.wessocketstockapp.model;

import com.example.wessocketstockapp.enumeration.RequestType;
import com.example.wessocketstockapp.enumeration.StockTicker;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestMessage
{
    private RequestType action;
    @JsonIgnore
    private StockTicker stockTicker;
    private String symbols;
    public RequestMessage(RequestType action, StockTicker stockTicker)
    {
        this.action = action;
        this.symbols = stockTicker.getTicker();
    }
}
