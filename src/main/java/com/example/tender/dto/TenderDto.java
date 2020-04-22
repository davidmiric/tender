package com.example.tender.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TenderDto {

    private Long id;

    @NotNull
    @Size(min = 10, max = 1000)
    private String description;

    @NotNull
    private Long issuerId;
}
