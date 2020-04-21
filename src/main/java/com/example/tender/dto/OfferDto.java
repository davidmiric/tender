package com.example.tender.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class OfferDto {

    private Long id;

    @NotNull
    private Long bidderId;

    @NotNull
    private Long tenderId;

    // current assumption is that offer is contained from an amount (e.g. money)
    @NotNull
    private Double amount;

}
