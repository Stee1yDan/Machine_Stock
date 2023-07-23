package com.example.wessocketstockapp.controller;

import com.example.wessocketstockapp.interfaces.CustomSocketHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.Stack;

@Controller
@EnableScheduling
public class ServerOutputSocketController implements CustomSocketHandler
{
    private final SimpMessagingTemplate simpMessagingTemplate;

    private Stack<String> messageLoad = new Stack<>();

    public ServerOutputSocketController(SimpMessagingTemplate simpMessagingTemplate)
    {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
    @Override
    public void addStockInfoToStack(String message)
    {
        messageLoad.push(message);
    }
    @Scheduled(fixedRate = 1000)
    @Async
    @Override
    public void sendPackage()
    {
        if (!messageLoad.isEmpty())
        {
            simpMessagingTemplate.convertAndSend("/topic/stock-info",  messageLoad.pop());
            messageLoad.clear();
        }
    }

    @Async
    @Override
    public void sendPackage(String message) //TODO: Make it Async
    {
        simpMessagingTemplate.convertAndSend("/topic/stock-info", message);
    }
}
