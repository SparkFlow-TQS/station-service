package tqs.sparkflow.stationservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import tqs.sparkflow.stationservice.service.OpenChargeMapService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OpenChargeMapController.class)
class OpenChargeMapControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpenChargeMapService openChargeMapService;

    @Test
    void testGetNearbyStationsEndpoint() throws Exception {
        mockMvc.perform(get("/openchargemap/nearby")
                .param("latitude", "40.0")
                .param("longitude", "-8.0")
                .param("radius", "10"))
                .andExpect(status().isOk()); // Adjust as needed
    }
} 