package com.example.tender.service;

import com.example.tender.dto.TenderDto;
import com.example.tender.entity.Issuer;
import com.example.tender.entity.Offer;
import com.example.tender.entity.Tender;
import com.example.tender.exception.BadRequestException;
import com.example.tender.repository.TenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TenderService {

    @Autowired
    private TenderRepository tenderRepository;

    @Autowired
    private IssuerService issuerService;

    public TenderDto createTender(TenderDto tenderDto) throws BadRequestException {
        Issuer issuer = issuerService.getIssuerById(tenderDto.getIssuerId());
        Tender newTender = new Tender().setDescription(tenderDto.getDescription())
                .setIssuer(issuer);
        return mapTenderToDto(tenderRepository.save(newTender));
    }

    public static TenderDto mapTenderToDto(Tender tender) {
        return new TenderDto().setId(tender.getId())
                .setDescription(tender.getDescription())
                .setIssuerId(tender.getIssuer().getId());
    }

    public List<TenderDto> getTenders(Long issuerId) {
        List<Tender> tenders;
        if (issuerId == null) {
            tenders = tenderRepository.findAll();
        } else {
            tenders = tenderRepository.findAllByIssuer(issuerService.getIssuerById(issuerId));
        }
        return tenders.stream()
                .map(TenderService::mapTenderToDto)
                .collect(Collectors.toList());
    }

    public Tender getTenderById(Long tenderId) {
        return tenderRepository.findById(tenderId).orElseThrow(() ->
                new BadRequestException(String.format("Tender with id{%d} does not exist.", tenderId))
        );
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Tender acceptOffer(Offer offer) {
        Tender tender = getTenderById(offer.getTender().getId());
        if (tender.isActive()) {
            return tenderRepository.save(tender.setBestOffer(offer)
                    .setActive(false));
        } else {
            throw new BadRequestException(
                    String.format("Cannot accept the offer. Tender with id{%d} is already closed.", tender.getId()));
        }
    }
}
