package tqs.sparkflow.stationservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import tqs.sparkflow.stationservice.service.OpenChargeMapService;
import org.springframework.security.test.context.support.WithMockUser;
import static org.mockito.Mockito.when;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(OpenChargeMapController.class)
class OpenChargeMapControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpenChargeMapService openChargeMapService;

    @Test
    @WithMockUser
    void testPopulateStationsEndpoint() throws Exception {
        when(openChargeMapService.populateStations(40.0, -8.0, 10))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/api/openchargemap/populate")
                .param("latitude", "40.0")
                .param("longitude", "-8.0")
                .param("radius", "10")
                .with(csrf()))
                .andExpect(status().isOk());
    }
} 