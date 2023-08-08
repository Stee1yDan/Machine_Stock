package com.example.wessocketstockapp.controller;

import com.example.wessocketstockapp.interfaces.CustomSocketHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.net.Socket;
import java.util.Stack;

@Controller
@EnableScheduling
public class NeuralNetworkSocketController implements CustomSocketHandler
{
    private static String pythonHost = "0.0.0.0";
    private static int pythonPort = 12345;

    private final ServerOutputSocketController serverOutputSocketController;

    private Stack<String> messageLoad = new Stack<>();

    public NeuralNetworkSocketController(ServerOutputSocketController serverOutputSocketController)
    {
        this.serverOutputSocketController = serverOutputSocketController;
    }
    @Override
    public void addStockInfoToStack(String message)
    {
        messageLoad.push(message);
    }

    public static Socket stockSocket(String host, int port)
    {
        try
        {
            return new Socket(host, port);
        }
        catch (IOException e)
        {
            System.out.println("Unable to create new Socket");
            e.printStackTrace();
            return null;
        }
    }

    @Scheduled(fixedRate = 60000)
    @Async
    @Override
    public void sendPackage()
    {
        try (Socket socket = stockSocket(pythonHost, pythonPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        )
        {
            out.write(messageLoad.pop());
            out.newLine();
            out.flush();

            messageLoad.clear();

            String prediction = in.readLine();
            System.out.println("Server answer: " + prediction);
            serverOutputSocketController.sendPackage(prediction);
        }
        catch (Exception e)
        {
            System.out.println("Couldn't send the package");
            e.printStackTrace();
        }
    }

    @Override
    public void sendPackage(String message)
    {
        try (Socket socket = stockSocket(pythonHost, pythonPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        )
        {
            out.write(message);
            out.newLine();
            out.flush();

            messageLoad.clear();

            String prediction = in.readLine();
            System.out.println("Server answer: " + prediction);
            serverOutputSocketController.sendPackage(prediction);
        }
        catch (Exception e)
        {
            System.out.println("Couldn't send the package");
            e.printStackTrace();
        }
    }

}