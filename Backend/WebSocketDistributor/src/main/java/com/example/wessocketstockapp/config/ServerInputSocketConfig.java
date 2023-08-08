package com.example.wessocketstockapp.config;

import com.example.wessocketstockapp.controller.NeuralNetworkSocketController;
import com.example.wessocketstockapp.controller.ServerOutputSocketController;
import com.example.wessocketstockapp.enumeration.URI;
import com.example.wessocketstockapp.controller.ServerInputSocketController;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
public class ServerInputSocketConfig
{
    private final ServerOutputSocketController serverOutputSocketController;
    private final NeuralNetworkSocketController neuralNetworkSocketController;


    public ServerInputSocketConfig(ServerOutputSocketController serverOutputSocketController, NeuralNetworkSocketController neuralNetworkSocketController)
    {
        this.serverOutputSocketController = serverOutputSocketController;
        this.neuralNetworkSocketController = neuralNetworkSocketController;
    }

    @PostConstruct
    public void startInputWebSocketConnection()
    {
        try
        {
            StandardWebSocketClient client = new StandardWebSocketClient();
            WebSocketSession session = client.doHandshake(
                            new ServerInputSocketController(serverOutputSocketController, neuralNetworkSocketController),
                            URI.crypto.getUri())
                    .get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Couldn't create connection with server");
        }
    }

}
