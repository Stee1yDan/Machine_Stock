package com.example.wessocketstockapp.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FullStockInfo extends BaseStockInfo //This class is a template for JSON string, don't rename variables
{
    private Double q; //quantity
    private Double dc; //daily change percentage
    private Double dd; //daily difference price
}
