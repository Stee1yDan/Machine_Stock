package com.example.wessocketstockapp.model;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BaseStockInfo
{
    private String s; //symbol
    private Double p; //price
    private String t; // timestamp

}

