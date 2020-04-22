package com.example.tender.repository;

import com.example.tender.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    @Query("SELECT o FROM offers o WHERE (:tender_id is null or o.tender.id = :tender_id) and (:bidder_id is null"
            + " or o.bidder.id = :bidder_id)")
    List<Offer> filterAllByTenderAndBidder(@Param("tender_id") Long tenderId, @Param("bidder_id") Long bidderId);
}
