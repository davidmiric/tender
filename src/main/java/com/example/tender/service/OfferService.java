package com.example.tender.service;

import com.example.tender.dto.OfferDto;
import com.example.tender.entity.Bidder;
import com.example.tender.entity.Offer;
import com.example.tender.entity.Tender;
import com.example.tender.exception.BadRequestException;
import com.example.tender.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private TenderService tenderService;
    @Autowired
    private BidderService bidderService;

    public OfferDto submitOffer(OfferDto offerDto) {
        Bidder bidder = bidderService.getBidderById(offerDto.getBidderId());
        Tender tender = tenderService.getTenderById(offerDto.getTenderId());
        if (tender.isActive()) {
            Offer offer = new Offer().setAmount(offerDto.getAmount())
                    .setBidder(bidder)
                    .setTender(tender);
            return mapToOfferDto(offerRepository.save(offer));
        } else {
            throw new BadRequestException(String.format("Offer cannot be accepted. Tender with id{%d} is closed.", offerDto.getTenderId()));
        }
    }

    private static OfferDto mapToOfferDto(Offer offer) {
        return new OfferDto().setId(offer.getId())
                .setAmount(offer.getAmount())
                .setBidderId(offer.getBidder().getId())
                .setTenderId(offer.getTender().getId());
    }
}
