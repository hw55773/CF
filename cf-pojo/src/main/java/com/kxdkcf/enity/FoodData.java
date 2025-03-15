package com.kxdkcf.enity;

import lombok.Data;

import java.util.List;

@Data
public class FoodData {
    private List<FoodItem> fruits;
    private List<FoodItem> vegetables;
    private List<FoodItem> cereals;


}