package com.example.tender.service;

import com.example.tender.dto.OfferDto;
import com.example.tender.entity.Bidder;
import com.example.tender.entity.Offer;
import com.example.tender.entity.Tender;
import com.example.tender.exception.BadRequestException;
import com.example.tender.repository.OfferRepository;
import org.assertj.core.util.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static com.example.tender.service.OfferService.mapOfferToDto;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OfferServiceTest {


    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private OfferService offerService;
    @Mock
    private OfferRepository offerRepository;
    @Mock
    private TenderService tenderService;
    @Mock
    private BidderService bidderService;

    private static final Long BIDDER_ID = 2L;
    private static final Long OFFER_ID_1 = 1L;
    private static final Long TENDER_ID = 2L;
    private static final Double AMOUNT = 100D;
    private static final Double AMOUNT2 = 200D;
    @Test
    public void submitOffer() {
        //given
        OfferDto offerDto = new OfferDto().setAmount(100d)
                .setBidderId(BIDDER_ID)
                .setTenderId(TENDER_ID);
        Bidder bidder = new Bidder().setId(BIDDER_ID);
        Tender tender = new Tender().setId(TENDER_ID);
        Offer offerSaved = new Offer().setId(321L)
                .setAmount(AMOUNT)
                .setBidder(bidder)
                .setTender(tender);
        when(bidderService.getBidderById(BIDDER_ID)).thenReturn(bidder);
        when(tenderService.getTenderById(TENDER_ID)).thenReturn(tender);
        when(offerRepository.save(any())).thenReturn(offerSaved);
        //when
        OfferDto result = offerService.submitOffer(offerDto);
        //then
        assertNotNull(result);
        assertThat(result.getId(), greaterThan(0l));
        assertThat(result.getTenderId(), is(TENDER_ID));
        assertThat(result.getBidderId(), is(BIDDER_ID));
        assertThat(result.getAmount(), is(AMOUNT));
    }

    @Test
    public void submitOffer_throwExceptionIfTenderIsClosed() {
        // given
        Tender tender = new Tender().setId(TENDER_ID)
                .setActive(false);
        String exceptionMessage =String.format("Offer cannot be accepted. Tender with id{%d} is closed.", TENDER_ID);
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage(exceptionMessage);

        OfferDto newOfferDto = new OfferDto().setTenderId(TENDER_ID)
                .setAmount(AMOUNT)
                .setBidderId(BIDDER_ID);
        when(tenderService.getTenderById(TENDER_ID)).thenReturn(tender);
        // when
        offerService.submitOffer(newOfferDto);
        // then
    }

    @Test
    public void acceptOffer() {
        //given
        Offer offer = new Offer().setId(1l)
                .setTender(new Tender().setId(TENDER_ID))
                .setBidder(new Bidder().setId(BIDDER_ID))
                .setAmount(AMOUNT);
        when(offerRepository.findById(OFFER_ID_1)).thenReturn(Optional.of(offer));
        when(offerRepository.findById(OFFER_ID_1)).thenReturn(Optional.of(offer));
        //when
        OfferDto  result = offerService.acceptOffer(OFFER_ID_1);
        //then
        verify(tenderService, times(1)).acceptOffer(offer);
        assertNotNull(result);
        assertThat(result.getId(), greaterThan(0l));
        assertThat(result.getTenderId(), is(TENDER_ID));
        assertThat(result.getBidderId(), is(BIDDER_ID));
        assertThat(result.getAmount(), is(AMOUNT));
    }

    @Test
    public void acceptOffer_throwExceptionIfNotExist() {
        //given
        String exceptionMessage =String.format("Offer with id{%d} does not exist", OFFER_ID_1);
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage(exceptionMessage);
        when(offerRepository.findById(OFFER_ID_1)).thenReturn(Optional.empty());
        //when
        OfferDto  result = offerService.acceptOffer(OFFER_ID_1);
        //then
    }

    @Test
    public void getOffers() {
        //given
        Long tenderId = 888l;
        Long bidderId = 999l;
        Offer offer1 = new Offer().setId(1l)
                .setTender(new Tender().setId(TENDER_ID))
                .setBidder(new Bidder().setId(BIDDER_ID))
                .setAmount(AMOUNT);
        Offer offer2 = new Offer().setId(1l)
                .setTender(new Tender().setId(TENDER_ID))
                .setBidder(new Bidder().setId(BIDDER_ID))
                .setAmount(AMOUNT2);

        when(offerRepository.filterAllByTenderAndBidder(tenderId, bidderId)).thenReturn(Lists.list(offer1, offer2));
        //when
        List<OfferDto> result = offerService.getOffers(tenderId, bidderId);
        //then
        assertNotNull(result);
        assertThat(result, containsInAnyOrder(mapOfferToDto(offer1), mapOfferToDto(offer2)));
    }

}