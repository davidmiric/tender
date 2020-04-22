package com.example.tender.controller;

import com.example.tender.dto.OfferDto;
import com.example.tender.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping(value = "/{id}/accept",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferDto> acceptOffer(@Valid @PathVariable("id") Long offerId) {
        return ResponseEntity.ok(offerService.acceptOffer(offerId));
    }

    // TODO: 4/20/2020 get all offers for tender

    // TODO: 4/20/2020 offers of particular bidder

    // TODO: 4/20/2020 offers of particular bidder for particular tender

}
