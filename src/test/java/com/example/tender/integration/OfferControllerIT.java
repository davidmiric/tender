package com.example.tender.integration;

import com.example.tender.TenderApplication;
import com.example.tender.dto.OfferDto;
import com.example.tender.entity.Tender;
import com.example.tender.repository.IssuerRepository;
import com.example.tender.repository.TenderRepository;
import com.example.tender.service.TenderService;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.templates.TemplateFormats.asciidoctor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TenderApplication.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class OfferControllerIT {


    private static final Gson gson = new Gson();

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

}