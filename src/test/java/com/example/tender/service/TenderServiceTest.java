package com.example.tender.service;

import com.example.tender.dto.TenderDto;
import com.example.tender.entity.Issuer;
import com.example.tender.entity.Tender;
import com.example.tender.exception.BadRequestException;
import com.example.tender.repository.IssuerRepository;
import com.example.tender.repository.TenderRepository;
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
    private IssuerRepository issuerRepository;

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
        when(issuerRepository.findById(1L)).thenReturn(Optional.of(issuer));
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
        when(issuerRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        tenderService.createTender(newTenderDto);
        // then
    }

    @Test
    public void getTendersForIssuer_returnListOfTenderDtos() {
        // given
        Issuer issuer = new Issuer().setName("Issuer").setId(1L);
        Tender tender1 = new Tender().setIssuer(issuer).setId(1L).setDescription("Tender1");
        Tender tender2 = new Tender().setIssuer(issuer).setId(2L).setDescription("Tender2");
        issuer.getTenders().add(tender1);
        issuer.getTenders().add(tender2);
        when(issuerRepository.findById(issuer.getId())).thenReturn(Optional.of(issuer));
        // when
        List<TenderDto> result = tenderService.getTendersForIssuer(issuer.getId());
        // then
        assertNotNull(result);
        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(mapTenderToDto(tender1), mapTenderToDto(tender2)));
    }

    @Test
    public void getTendersForIssuer_whenInvalidIssuer_throwException() {
        // given
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Issuer with id{1} does not exist.");

        Long notExistingIssuerId = 1L;
        when(issuerRepository.findById(notExistingIssuerId)).thenReturn(Optional.empty());
        // when
        tenderService.getTendersForIssuer(notExistingIssuerId);
        // then
    }

}