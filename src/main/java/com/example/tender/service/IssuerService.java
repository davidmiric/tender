package com.example.tender.service;

import com.example.tender.entity.Issuer;
import com.example.tender.exception.BadRequestException;
import com.example.tender.repository.IssuerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IssuerService {

    @Autowired
    private IssuerRepository issuerRepository;

    public Issuer getIssuerById(Long issuerId) {
        return issuerRepository.findById(issuerId).orElseThrow(() ->
                new BadRequestException(String.format("Issuer with id{%d} does not exist.", issuerId))
        );
    }
}
