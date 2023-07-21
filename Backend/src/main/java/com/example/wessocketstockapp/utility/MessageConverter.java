package com.example.wessocketstockapp.utility;

import com.example.wessocketstockapp.model.BaseStockInfo;
import com.example.wessocketstockapp.model.RequestMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

public class MessageConverter
{
    public static String convertToJsonBaseMessage(String jsonMessage)
    {
        BaseStockInfo baseStockInfo = (BaseStockInfo) JsonMapper.convertFromJsonString(jsonMessage, BaseStockInfo.class);
        return JsonMapper.convertToJsonString(baseStockInfo);
    }

    public static WebSocketMessage<String> convertToWebSocketMessage(RequestMessage requestMessage)
    {
        return new TextMessage(JsonMapper.convertToJsonString(requestMessage));
    }

    public static WebSocketMessage<String> convertToWebSocketMessage(String requestMessage)
    {
        return new TextMessage(JsonMapper.convertToJsonString(requestMessage));
    }
}
