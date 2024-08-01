package com.ak.apps.order.model;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class Item {
    private Integer id;
    private String itemName;
    private double price;

}
