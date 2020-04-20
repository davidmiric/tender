package com.example.tender.controller;

import com.example.tender.dto.TenderDto;
import com.example.tender.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("tenders")
@Validated
public class TenderController {

    @Autowired
    private TenderService tenderService;

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TenderDto> createTender(@Valid @RequestBody TenderDto tenderDto) {
        return ResponseEntity.ok(tenderService.createTender(tenderDto));
    }

    // TODO: 4/20/2020 get all offers for tender

    // TODO: 4/20/2020 tenders of an issuer

    // TODO: 4/20/2020 offers of particular bidder

    // TODO: 4/20/2020 offers of particular bidder for particular tender

}
