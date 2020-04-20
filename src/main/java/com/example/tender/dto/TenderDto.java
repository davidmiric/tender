package com.example.tender.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TenderDto {

    private Long id;

    @NotNull
    private String description;

    @NotNull
    private Long issuerId;
}
