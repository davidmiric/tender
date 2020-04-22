package com.example.tender.integration;

import com.example.tender.TenderApplication;
import com.example.tender.dto.TenderDto;
import com.example.tender.entity.Issuer;
import com.example.tender.entity.Tender;
import com.example.tender.repository.IssuerRepository;
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

import static com.example.tender.service.TenderService.mapTenderToDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.templates.TemplateFormats.asciidoctor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TenderApplication.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class TenderControllerIT {

    public static final String FIRST_TENDER_DESCRIPTION = "The first tender";

    private static final Gson gson = new Gson();

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    private static final FieldDescriptor[] TENDER_FIELDS = new FieldDescriptor[]{
            fieldWithPath("id").description("The id of created tender."),
            fieldWithPath("issuerId").description("The id of issuer that created tender."),
            fieldWithPath("description").description("The description of tender.")};
    @Autowired
    private TenderRepository tenderRepository;

    @Autowired
    private TenderService tenderService;

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private IssuerRepository issuerRepository;

    // Field descriptors
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation).snippets()
                        .withTemplateFormat(asciidoctor())).build();
    }

    @Test
    public void createTender_thenCreateNewTenderAndReturnItInResponse() throws Exception {
        // given
        TenderDto tenderToBeCreated = new TenderDto().setIssuerId(1L)
                .setDescription(FIRST_TENDER_DESCRIPTION);

        // when
        MvcResult result = this.mockMvc.perform(post("/tenders")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(tenderToBeCreated)))
                .andExpect(status().isOk())
                .andDo(document("create-tender",
                        responseFields(TENDER_FIELDS)))
                .andReturn();

        // then
        TenderDto returnedValue = gson.fromJson(result.getResponse().getContentAsString(), TenderDto.class);
        assertNotNull(returnedValue.getId());
        assertThat(returnedValue.getIssuerId(), is(tenderToBeCreated.getIssuerId()));
        assertThat(returnedValue.getDescription(), is(FIRST_TENDER_DESCRIPTION));
    }

    @Test
    public void getTendersForIssuer_thenReturnListOfTenders() throws Exception {
        // given
        Issuer issuer = issuerRepository.findById(1L).get();
        Tender firstTenderCreatedByIssuer = new Tender().setId(1L)
                .setDescription("Tender 1, created by issuer. Tender for building a new parking.")
                .setIssuer(issuer);
        Tender secondTenderCreatedByIssuer = new Tender().setId(2L)
                .setDescription("Tender 2, created by issuer. Tender for maintenance of bus stations.")
                .setIssuer(issuer);
        firstTenderCreatedByIssuer = tenderRepository.save(firstTenderCreatedByIssuer);
        secondTenderCreatedByIssuer = tenderRepository.save(secondTenderCreatedByIssuer);
        // when
        MvcResult result = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/tenders?issuerId=1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(document("issuer-tenders",
                        responseFields(
                                fieldWithPath("[]").description("An array of tenders."))
                                .andWithPrefix("[].", TENDER_FIELDS),
                        requestParameters(
                                parameterWithName("issuerId").description("Optional id of issuer for filtering targets by issuer that created them."))))
                .andReturn();
        List<TenderDto> returnedTenders = new Gson().fromJson(result.getResponse().getContentAsString(), new TypeToken<List<TenderDto>>() {
        }.getType());
        // then
        assertNotNull(returnedTenders);
        assertThat(returnedTenders, containsInRelativeOrder(mapTenderToDto(firstTenderCreatedByIssuer), mapTenderToDto(secondTenderCreatedByIssuer)));
    }


}