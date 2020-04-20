package com.example.tender.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class OfferDto {

    @NotNull
    private Long bidderId;

    @NotNull
    private Long tenderId;

    // current assumption is that offer is contained from an amount (e.g. money)
    @NotNull
    private Double amount;

}
