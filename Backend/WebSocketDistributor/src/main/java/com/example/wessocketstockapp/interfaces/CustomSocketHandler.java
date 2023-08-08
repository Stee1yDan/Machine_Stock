package com.example.wessocketstockapp.interfaces;

import java.util.Stack;

public interface CustomSocketHandler
{
    void addStockInfoToStack(String message);
    void sendPackage();
    void sendPackage(String message);
}
