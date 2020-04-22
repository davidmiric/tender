package com.example.tender.service;

import com.example.tender.dto.TenderDto;
import com.example.tender.entity.Issuer;
import com.example.tender.entity.Offer;
import com.example.tender.entity.Tender;
import com.example.tender.exception.BadRequestException;
import com.example.tender.repository.TenderRepository;
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

import static com.example.tender.service.TenderService.mapTenderToDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TenderServiceTest {

    private final static String DESCRIPTION = "description";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @InjectMocks
    private TenderService tenderService;
    @Mock
    private TenderRepository tenderRepository;
    @Mock
    private IssuerService issuerService;

    @Test
    public void createTender_whenValidDtoIsPassed_thenReturnDtoOfCreatedDto() {
        // given
        TenderDto newTenderDto = new TenderDto().setIssuerId(1L).setDescription(DESCRIPTION);
        Issuer issuer = new Issuer().setId(1L);
        Tender correctTenderEntity = new Tender().setIssuer(issuer)
                .setId(1L)
                .setDescription(DESCRIPTION)
                .setActive(true)
                .setBestOffer(null);
        Tender tenderWithoutId = new Tender().setDescription(DESCRIPTION)
                .setIssuer(issuer);
        when(issuerService.getIssuerById(1L)).thenReturn(issuer);
        when(tenderRepository.save(tenderWithoutId)).thenReturn(correctTenderEntity);

        // when
        TenderDto resultTender = tenderService.createTender(newTenderDto);

        // then
        verify(tenderRepository, times(1)).save(tenderWithoutId);
        assertNotNull(resultTender);
        assertThat(resultTender.getId(), equalTo(correctTenderEntity.getId()));
        assertThat(resultTender.getIssuerId(), equalTo(issuer.getId()));
        assertThat(resultTender.getDescription(), equalTo(DESCRIPTION));
    }

    @Test
    public void createTender_whenInvalidIssuer_throwException() {
        // given
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Issuer with id{1} does not exist.");

        TenderDto newTenderDto = new TenderDto().setIssuerId(1L).setDescription(DESCRIPTION);
        when(issuerService.getIssuerById(1L)).thenThrow(new BadRequestException("Issuer with id{1} does not exist."));
        // when
        tenderService.createTender(newTenderDto);
        // then
    }

    @Test
    public void getTendersAllAndByIssuer_returnListOfTenderDtos() {
        // given
        Issuer issuer = new Issuer().setName("Issuer").setId(1L);
        Tender tender1 = new Tender().setIssuer(issuer).setId(1L).setDescription("Tender1");
        Tender tender2 = new Tender().setIssuer(issuer).setId(2L).setDescription("Tender2");
        Tender tender3 = new Tender().setIssuer(issuer).setId(3L).setDescription("Tender3");
        issuer.getTenders().add(tender1);
        issuer.getTenders().add(tender2);
        when(issuerService.getIssuerById(1L)).thenReturn(issuer);
        when(tenderRepository.findAllByIssuer(issuer)).thenReturn(Lists.list(tender1, tender2));
        when(tenderRepository.findAll()).thenReturn(Lists.list(tender1, tender2, tender3));
        // when
        List<TenderDto> resultAll = tenderService.getTenders(null);
        List<TenderDto> resultByIssue = tenderService.getTenders(issuer.getId());
        // then
        assertNotNull(resultAll);
        assertThat(resultAll, containsInRelativeOrder(mapTenderToDto(tender1), mapTenderToDto(tender2), mapTenderToDto(tender3)));
        assertNotNull(resultByIssue);
        assertThat(resultByIssue, containsInRelativeOrder(mapTenderToDto(tender1), mapTenderToDto(tender2)));
    }

    @Test
    public void getTendersForIssuer_whenInvalidIssuer_throwException() {
        // given
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Issuer with id{1} does not exist.");

        Long notExistingIssuerId = 1L;
        when(issuerService.getIssuerById(notExistingIssuerId)).thenThrow(new BadRequestException("Issuer with id{1} does not exist."));
        // when
        tenderService.getTenders(notExistingIssuerId);
        // then
    }

    @Test
    public void acceptOffer() {
        // given
        Tender tender = new Tender().setId(1l)
                .setDescription(DESCRIPTION);
        Offer offer = new Offer().setId(2L)
                .setTender(tender);
        when(tenderRepository.save(tender)).thenReturn(tender);
        when(tenderRepository.findById(tender.getId())).thenReturn(Optional.of(tender));
        // when
        Tender result = tenderService.acceptOffer(offer);
        // then
        assertNotNull(result);
        assertFalse(result.isActive());
        assertThat(result.getBestOffer().getId(), is(offer.getId()));
    }

    @Test
    public void acceptOffer_throwExceptionIfTenderIsClosed() {
        // given
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage(String.format("Cannot accept the offer. Tender with id{%d} is already closed.", 1L));
        Tender closedTender = new Tender().setId(1l)
                .setDescription(DESCRIPTION)
                .setActive(false);
        when(tenderRepository.findById(closedTender.getId())).thenReturn(Optional.of(closedTender));
        Offer offer = new Offer().setId(2L)
                .setTender(closedTender);
        // when
        tenderService.acceptOffer(offer);
        // then
    }
}