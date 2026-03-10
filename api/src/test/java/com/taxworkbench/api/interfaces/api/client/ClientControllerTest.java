package com.taxworkbench.api.interfaces.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxworkbench.api.application.client.CreateClientUseCase;
import com.taxworkbench.api.application.client.QueryClientUseCase;
import com.taxworkbench.api.domain.client.Client;
import com.taxworkbench.api.domain.client.ClientTier;
import com.taxworkbench.api.domain.client.ClientType;
import com.taxworkbench.api.domain.workitem.WorkItemRepository;
import com.taxworkbench.api.interfaces.api.common.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
@Import(GlobalExceptionHandler.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateClientUseCase createClientUseCase;

    @MockBean
    private QueryClientUseCase queryClientUseCase;

    @MockBean
    private WorkItemRepository workItemRepository;

    @Test
    void findAllReturnsListEnvelope() throws Exception {
        Client client = Client.create("Acme Tax", "123-45-67890", ClientType.CORPORATE, ClientTier.VIP);
        client.setId(1L);
        given(queryClientUseCase.findAll()).willReturn(List.of(client));

        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].id").value(1))
                .andExpect(jsonPath("$.data.items[0].name").value("Acme Tax"))
                .andExpect(jsonPath("$.data.count").value(1));
    }

    @Test
    void createReturnsValidationErrorEnvelope() throws Exception {
        ClientRequest request = new ClientRequest("", "invalid", null, null);

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
                .andExpect(jsonPath("$.data.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.data.details.fieldErrors.name").exists())
                .andExpect(jsonPath("$.data.details.fieldErrors.bizNo").exists())
                .andExpect(jsonPath("$.data.details.fieldErrors.type").exists())
                .andExpect(jsonPath("$.data.details.fieldErrors.tier").exists());
    }

    @Test
    void createReturnsCreatedClient() throws Exception {
        Client client = Client.create("Beta Tax", "123-45-67890", ClientType.CORPORATE, ClientTier.BASIC);
        client.setId(2L);
        given(createClientUseCase.execute(any())).willReturn(client);

        ClientRequest request = new ClientRequest("Beta Tax", "123-45-67890", ClientType.CORPORATE, ClientTier.BASIC);

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.name").value("Beta Tax"))
                .andExpect(jsonPath("$.data.bizNo").value("123-45-67890"));
    }
}
