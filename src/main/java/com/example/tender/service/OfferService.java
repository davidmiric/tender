package com.example.tender.service;

import com.example.tender.dto.OfferDto;
import com.example.tender.entity.Bidder;
import com.example.tender.entity.Offer;
import com.example.tender.entity.Tender;
import com.example.tender.exception.BadRequestException;
import com.example.tender.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
            return mapOfferToDto(offerRepository.save(offer));
        } else {
            throw new BadRequestException(String.format("Offer cannot be accepted. Tender with id{%d} is closed.", offerDto.getTenderId()));
        }
    }

    public static OfferDto mapOfferToDto(Offer offer) {
        return new OfferDto().setId(offer.getId())
                .setAmount(offer.getAmount())
                .setBidderId(offer.getBidder().getId())
                .setTenderId(offer.getTender().getId());
    }

    public OfferDto acceptOffer(Long offerId) {
        Offer offer = offerRepository.findById(offerId).orElseThrow(() ->
                new BadRequestException(String.format("Offer with id{%d} does not exist", offerId)));
        tenderService.acceptOffer(offer);
        return mapOfferToDto(offer);
    }


    public List<OfferDto> getOffers(Long tenderId, Long bidderId) {
        return offerRepository.filterAllByTenderAndBidder(tenderId, bidderId).stream()
                .map(OfferService::mapOfferToDto)
                .collect(Collectors.toList());
    }
}
