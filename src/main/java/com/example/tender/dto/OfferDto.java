package com.example.tender.dto;

import lombok.Data;

@Data
public class OfferDto {

    private Long bidderId;

    private Long tenderId;

    // current assumption is that offer is contained from an amount (e.g. money)
    private Double amount;

}
