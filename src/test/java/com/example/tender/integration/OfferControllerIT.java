package com.example.tender.integration;

import com.example.tender.TenderApplication;
import com.example.tender.dto.OfferDto;
import com.example.tender.entity.Bidder;
import com.example.tender.entity.Issuer;
import com.example.tender.entity.Offer;
import com.example.tender.entity.Tender;
import com.example.tender.repository.BidderRepository;
import com.example.tender.repository.IssuerRepository;
import com.example.tender.repository.OfferRepository;
import com.example.tender.repository.TenderRepository;
import com.example.tender.service.TenderService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.example.tender.service.OfferService.mapOfferToDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.templates.TemplateFormats.asciidoctor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TenderApplication.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class OfferControllerIT {


    private static final Gson gson = new Gson();

    private static final Double AMOUNT = 100D;
    private static final Double AMOUNT2 = 200D;

    private static final FieldDescriptor[] OFFER_FIELDS = new FieldDescriptor[]{
            fieldWithPath("id").description("The id of created tender."),
            fieldWithPath("bidderId").description("The id of bidder who makes and offer."),
            fieldWithPath("tenderId").description("Tender id, this offer is meant for."),
            fieldWithPath("amount").description("The money amount offered for this tender.")};

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    private TenderRepository tenderRepository;
    @Autowired
    private TenderService tenderService;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private IssuerRepository issuerRepository;
    @Autowired
    private BidderRepository bidderRepository;
    @Autowired
    private OfferRepository offerRepository;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation).snippets()
                        .withTemplateFormat(asciidoctor())).build();
    }

    @Test
    public void submitOffer_thenCreateNewOfferAndReturnOfferDto() throws Exception {
        // given
        Tender tender = tenderRepository.save(new Tender().setDescription("Tender description")
                .setIssuer(issuerRepository.findById(1L).get()));
        OfferDto newOffer = new OfferDto().setAmount(100d)
                .setBidderId(2L)
                .setTenderId(tender.getId());

        // when
        MvcResult result = this.mockMvc.perform(post("/offers")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(newOffer)))
                .andExpect(status().isOk())
                .andDo(document("submit-offer",
                        responseFields(OFFER_FIELDS)))
                .andReturn();

        // then
        OfferDto returnedValue = gson.fromJson(result.getResponse().getContentAsString(), OfferDto.class);
        assertNotNull(returnedValue.getId());
        assertThat(returnedValue.getBidderId(), is(newOffer.getBidderId()));
        assertThat(returnedValue.getAmount(), is(newOffer.getAmount()));
        assertThat(returnedValue.getTenderId(), is(newOffer.getTenderId()));
    }

    @Test
    public void acceptOffer_thenAcceptAndReturnOffer() throws Exception {
        // given
        Bidder bidder = bidderRepository.findById(1l).get();
        Issuer issuer = issuerRepository.findById(1l).get();
        Tender tender = tenderRepository.save(new Tender().setDescription("Tender description")
                .setIssuer(issuer));
        Offer offerToBeAccepted = new Offer().setAmount(AMOUNT)
                .setBidder(bidder)
                .setTender(tender);
        offerRepository.save(offerToBeAccepted);
        OfferDto newOffer = new OfferDto().setAmount(AMOUNT)
                .setBidderId(bidder.getId())
                .setTenderId(tender.getId());

        // when
        MvcResult result = this.mockMvc.perform(RestDocumentationRequestBuilders.post("/offers/{id}/accept", offerToBeAccepted.getId()))
                .andExpect(status().isOk())
                .andDo(document("accept-offer",
                        responseFields(OFFER_FIELDS),
                        pathParameters(
                                parameterWithName("id").description("Id of offer to be accepted."))))
                .andReturn();

        // then
        OfferDto returnedValue = gson.fromJson(result.getResponse().getContentAsString(), OfferDto.class);
        assertNotNull(returnedValue.getId());
        assertThat(returnedValue.getBidderId(), is(newOffer.getBidderId()));
        assertThat(returnedValue.getAmount(), is(newOffer.getAmount()));
        assertThat(returnedValue.getTenderId(), is(newOffer.getTenderId()));
        tender = tenderRepository.findById(tender.getId()).get();
        assertFalse(tender.isActive());
        assertThat(tender.getBestOffer().getId(), is(offerToBeAccepted.getId()));
    }

    @Test
    public void getOffers_returnOffersFilteredByTenderAndBidder() throws Exception {
        // given
        offerRepository.deleteAll();
        tenderRepository.deleteAll();
        Bidder bidder1 = bidderRepository.findById(1l).get();
        Issuer issuer = issuerRepository.findById(1l).get();
        Tender tender1 = tenderRepository.save(new Tender().setDescription("tender 1 description")
                .setIssuer(issuer));
        Offer offer1 = offerRepository.save(new Offer().setTender(tender1).setAmount(AMOUNT).setBidder(bidder1));
        Offer offer2 = offerRepository.save(new Offer().setTender(tender1).setAmount(AMOUNT2).setBidder(bidder1));
        // when
        MvcResult result = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/offers?tenderId={%d}&bidderId={%d}", tender1.getId(), bidder1.getId()))
                .andExpect(status().isOk())
                .andDo(document("get-offers",
                        responseFields(
                                fieldWithPath("[]").description("An array of offers."))
                                .andWithPrefix("[].", OFFER_FIELDS),
                        requestParameters(
                                parameterWithName("tenderId").description("Tender id for offers to be filtered by."),
                                parameterWithName("bidderId").description("Bidder id for offers to be filtered by."))))
                .andReturn();

        // then
        List<OfferDto> returnedOffers = new Gson()
                .fromJson(result.getResponse().getContentAsString(), new TypeToken<List<OfferDto>>() {
                }.getType());
        // then
        assertNotNull(returnedOffers);
        assertThat(returnedOffers, containsInAnyOrder(mapOfferToDto(offer1), mapOfferToDto(offer2)));
    }
}