package com.example.wessocketstockapp.controller;

import com.example.wessocketstockapp.enumeration.RequestType;
import com.example.wessocketstockapp.enumeration.StockTicker;
import com.example.wessocketstockapp.model.RequestMessage;
import com.example.wessocketstockapp.utility.MessageConverter;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;



import java.io.IOException;


@Service
@Getter
public class ServerInputSocketController extends TextWebSocketHandler
{
    private final ServerOutputSocketController serverOutputSocketController;
    private final NeuralNetworkSocketController neuralNetworkSocketController;
    private final RequestMessage subscribeMessage = new RequestMessage(RequestType.subscribe, StockTicker.BITCOIN);
    private final RequestMessage unsubscribeMessage = new RequestMessage(RequestType.unsubscribe,StockTicker.BITCOIN);

    public ServerInputSocketController(ServerOutputSocketController serverOutputSocketController, NeuralNetworkSocketController neuralNetworkSocketController)
    {
        this.serverOutputSocketController = serverOutputSocketController;
        this.neuralNetworkSocketController = neuralNetworkSocketController;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException
    {
        System.out.println("Connected to WebSocket!");
        session.sendMessage(MessageConverter.convertToWebSocketMessage(subscribeMessage));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
    {
        String buf = MessageConverter.convertToJsonBaseMessage(message.getPayload());

        if (!buf.contains("status_code"))
        {
            try
            {
                serverOutputSocketController.addStockInfoToStack(buf);
                neuralNetworkSocketController.addStockInfoToStack(buf);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

//        System.out.println("Received message: " + buf);
    }
}