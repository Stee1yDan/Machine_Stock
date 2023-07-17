package com.example.wessocketstockapp.controller;

import com.example.wessocketstockapp.utility.MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.Stack;

@Controller
@EnableScheduling
public class ServerOutputSocketController
{
    private final SimpMessagingTemplate simpMessagingTemplate; //TODO: set socket heartbeat

    private Stack<String> messageLoad = new Stack<>();

    public ServerOutputSocketController(SimpMessagingTemplate simpMessagingTemplate)
    {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void addStockInfoToStack(String message)
    {
        messageLoad.push(message);
    }
    @Scheduled(fixedRate = 1000)
    @Async
    public void sendStockInfo()
    {
        if (!messageLoad.isEmpty())
        {
            simpMessagingTemplate.convertAndSend("/topic/stock-info",  MessageConverter.convertBaseMessageTime(messageLoad.pop()));
            messageLoad.clear();
        }
    }

    public void sendStockInfo(String message)
    {
        simpMessagingTemplate.convertAndSend("/topic/stock-info", MessageConverter.convertBaseMessageTime(message));
    }
}
