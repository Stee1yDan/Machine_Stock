package com.example.wessocketstockapp.enumeration;

public enum URI
{
    crypto("wss://ws.eodhistoricaldata.com/ws/crypto?api_token=demo"),
    local("ws://localhost:8765");

    private String uri;

    URI(String uri)
    {
        this.uri = uri;
    }

    public String getUri()
    {
        return uri;
    }
}
