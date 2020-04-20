package com.example.tender.integration;

import com.example.tender.TenderApplication;
import com.example.tender.dto.TenderDto;
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
public class TenderControllerIT {

    public static final String FIRST_TENDER_DESCRIPTION = "The first tender";

    private static final Gson gson = new Gson();
    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
    private MockMvc mockMvc;
    @Autowired
    private TenderRepository repository;
    @Autowired
    private TenderService tenderService;
    @Autowired
    private WebApplicationContext context;

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
                        responseFields(
                                fieldWithPath("id").description("The id of created tender."),
                                fieldWithPath("issuerId").description("The id of issuer that created tender."),
                                fieldWithPath("description").description("The description of tender."))))
                .andReturn();

        // then
        TenderDto returnedValue = gson.fromJson(result.getResponse().getContentAsString(), TenderDto.class);
        assertNotNull(returnedValue.getId());
        assertThat(returnedValue.getIssuerId(), is(tenderToBeCreated.getIssuerId()));
        assertThat(returnedValue.getDescription(), is(FIRST_TENDER_DESCRIPTION));
    }

    @Test
    public void createTender_WithInvalidData_thenReturnErrorResponse() throws Exception {
        // given
        TenderDto tenderWithoutDescription = new TenderDto().setIssuerId(1L);
        TenderDto tenderWithInvalidIssuerId = new TenderDto().setIssuerId(999L)
                .setDescription(FIRST_TENDER_DESCRIPTION);

        // when
        MvcResult resultNullDescription = this.mockMvc.perform(post("/tenders")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(tenderWithoutDescription)))
                .andExpect(status().isBadRequest())
                .andReturn();

        MvcResult resultInvalidIssuerId = this.mockMvc.perform(post("/tenders")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(tenderWithInvalidIssuerId)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        TenderDto tenderDto = new TenderDto();
    }


}