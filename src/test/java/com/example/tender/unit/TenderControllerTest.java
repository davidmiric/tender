package com.example.tender.unit;

import com.example.tender.controller.TenderController;
import com.example.tender.dto.TenderDto;
import com.example.tender.service.TenderService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TenderController.class)
@AutoConfigureRestDocs(outputDir = "target/snippets")
class TenderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TenderService tenderService;

    private Gson gson = new Gson();

    @Test
    void createTender_shouldCreateTenderAndReturnDto() throws Exception {
        // given
        TenderDto tenderToBeCreated = new TenderDto().setIssuerId(1L)
                .setDescription("Prv tender.");
        when(tenderService.createTender(tenderToBeCreated)).thenReturn(tenderToBeCreated.setId(1L));
        // when
        // then
        this.mockMvc.perform(post("/tenders")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(tenderToBeCreated)))
                .andExpect(status().isOk());
    }

}