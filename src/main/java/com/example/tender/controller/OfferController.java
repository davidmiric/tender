package com.example.tender.controller;

import com.example.tender.dto.OfferDto;
import com.example.tender.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("offers")
@Validated
public class OfferController {

    @Autowired
    private OfferService offerService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferDto> submitOffer(@Valid @RequestBody OfferDto offerDto) {
        return ResponseEntity.ok(offerService.submitOffer(offerDto));
    }

}
