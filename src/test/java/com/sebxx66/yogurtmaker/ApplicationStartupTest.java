package com.sebxx66.yogurtmaker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import com.jayway.jsonpath.JsonPath;
import com.sebxx66.yogurtmaker.support.ApiTestSupport;

/** Arranca la aplicación como en local: datos de ejemplo y simulador activo (con lecturas cada 10 ms). */
@SpringBootTest(properties = {
        "yogurt.demo-data=true",
        "yogurt.simulation.enabled=true",
        "yogurt.simulation.tick=10ms",
        "spring.datasource.url=jdbc:h2:mem:yogurtdb-startup;DB_CLOSE_DELAY=-1"
})
@AutoConfigureMockMvc
class ApplicationStartupTest extends ApiTestSupport {

    @Test
    void cargaLasRecetasDeEjemploYPublicaLaDocumentacion() throws Exception {
        mvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem("Yogurt natural")))
                .andExpect(jsonPath("$[*].name", hasItem("Yogurt griego")));
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Yogurt Maker API"));
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void elSimuladorCalientaYDejaElLoteListoParaInocular() throws Exception {
        long batchId = createBatch(createRecipe(uniqueName("Simulada")));
        mvc.perform(post("/api/batches/" + batchId + "/heating")).andExpect(status().isOk());

        String status = waitForStatus(batchId, "COOLING", 20_000);
        assertThat(status).isEqualTo("COOLING");

        // Espera a que el enfriamiento llegue a la temperatura de inoculación (45 °C ± 0,5).
        long deadline = System.currentTimeMillis() + 20_000;
        String logs;
        do {
            Thread.sleep(100);
            logs = mvc.perform(get("/api/monitoring/batches/" + batchId + "/temperature-logs"))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        } while (lastTemperature(logs) > 45.6 && System.currentTimeMillis() < deadline);

        java.util.List<String> types = JsonPath.read(logs, "$[*].type");
        assertThat(types).contains("HEATING", "COOLING");
        assertThat(lastTemperature(logs)).isBetween(44.0, 45.6);

        mvc.perform(post("/api/batches/" + batchId + "/inoculating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INOCULATING"));
    }

    private String waitForStatus(long batchId, String expected, long timeoutMillis) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        String status;
        do {
            Thread.sleep(100);
            String body = mvc.perform(get("/api/batches/" + batchId))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            status = JsonPath.read(body, "$.status");
        } while (!expected.equals(status) && System.currentTimeMillis() < deadline);
        return status;
    }

    private static double lastTemperature(String logsJson) {
        java.util.List<Number> temperatures = JsonPath.read(logsJson, "$[*].temperature");
        return temperatures.isEmpty() ? Double.MAX_VALUE : temperatures.getLast().doubleValue();
    }
}
