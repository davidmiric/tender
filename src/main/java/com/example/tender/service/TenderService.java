package com.example.tender.service;

import com.example.tender.dto.TenderDto;
import com.example.tender.entity.Issuer;
import com.example.tender.entity.Tender;
import com.example.tender.exception.BadRequestException;
import com.example.tender.repository.IssuerRepository;
import com.example.tender.repository.TenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TenderService {

    @Autowired
    private TenderRepository tenderRepository;

    @Autowired
    private IssuerRepository issuerRepository;

    public TenderDto createTender(TenderDto tenderDto) throws BadRequestException {
        Optional<Issuer> issuer = issuerRepository.findById(tenderDto.getIssuerId());
        if (!issuer.isPresent()) {
            throw new BadRequestException(String.format("Issuer with id{%d} does not exist.", tenderDto.getIssuerId()));
        }
        Tender newTender = new Tender().setDescription(tenderDto.getDescription())
                .setIssuer(issuer.get());
        return mapTenderToDto(tenderRepository.save(newTender));
    }

    TenderDto mapTenderToDto(Tender tender) {
        return new TenderDto().setId(tender.getId())
                .setDescription(tender.getDescription())
                .setIssuerId(tender.getIssuer().getId());
    }

}
