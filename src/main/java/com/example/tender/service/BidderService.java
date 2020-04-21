package com.example.tender.service;

import com.example.tender.entity.Bidder;
import com.example.tender.exception.BadRequestException;
import com.example.tender.repository.BidderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BidderService {

    @Autowired
    private BidderRepository bidderRepository;

    public Bidder getBidderById(Long bidderId) {
        return bidderRepository.findById(bidderId).orElseThrow(() ->
                new BadRequestException(String.format("Bidder with id{%d} does not exist.", bidderId))
        );
    }
}
