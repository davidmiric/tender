package com.example.tender.controller;

import com.example.tender.dto.TenderDto;
import com.example.tender.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/tenders")
@Validated
public class TenderController {

    @Autowired
    private TenderService tenderService;

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TenderDto> createTender(@Valid @RequestBody TenderDto tenderDto) {
        return ResponseEntity.ok(tenderService.createTender(tenderDto));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TenderDto>> getTendersForIssuer(@RequestParam("issuerId") Long issuerId) {
        return ResponseEntity.ok(tenderService.getTenders(issuerId));
    }

}
