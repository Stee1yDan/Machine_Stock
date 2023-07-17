package com.example.wessocketstockapp.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FullStockInfo //This class is a template for JSON string, don't rename variables unless you are retarded
{
    private String s; //symbol
    private Double p; //price
    private Double q; //quantity
    private Double dc; //daily change percentage
    private Double dd; //daily difference price
    private Long t; // timestamp

}
