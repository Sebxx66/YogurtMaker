package com.sebxx66.yogurtmaker;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import com.sebxx66.yogurtmaker.support.ApiTestSupport;
import com.sebxx66.yogurtmaker.support.MutableClock;
import com.sebxx66.yogurtmaker.support.TestClockConfig;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestClockConfig.class)
class BatchLifecycleTest extends ApiTestSupport {

    @Autowired
    private MutableClock clock;

    @Test
    void recorreTodoElProcesoDeFabricacion() throws Exception {
        long batchId = createBatch(createRecipe(uniqueName("Ciclo completo")));
        String batch = "/api/batches/" + batchId;

        mvc.perform(get(batch))
                .andExpect(jsonPath("$.status").value("PREPARING"))
                .andExpect(jsonPath("$.batchCode").value(org.hamcrest.Matchers.matchesPattern("YB-\\d{8}-[A-Z0-9]{4}")))
                .andExpect(jsonPath("$.milkVolume").value(2.0))
                .andExpect(jsonPath("$.incubationTime").value(6));

        mvc.perform(post(batch + "/heating")).andExpect(jsonPath("$.status").value("HEATING"));
        mvc.perform(post(batch + "/cooling")).andExpect(jsonPath("$.status").value("COOLING"));
        mvc.perform(post(batch + "/inoculating")).andExpect(jsonPath("$.status").value("INOCULATING"));
        mvc.perform(post(batch + "/incubation"))
                .andExpect(jsonPath("$.status").value("INCUBATING"))
                .andExpect(jsonPath("$.incubationEndTime").exists());

        // La incubación dura 6 horas: antes de eso no se puede refrigerar.
        mvc.perform(post(batch + "/refrigeration"))
                .andExpect(status().isConflict());

        clock.advance(Duration.ofHours(6));
        mvc.perform(post(batch + "/refrigeration")).andExpect(jsonPath("$.status").value("REFRIGERATING"));

        mvc.perform(post(batch + "/complete")).andExpect(status().isConflict());
        clock.advance(Duration.ofHours(4));
        mvc.perform(post(batch + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.completedAt").exists());

        mvc.perform(get("/api/monitoring/batches/active"))
                .andExpect(jsonPath("$[*].id", not(hasItem((int) batchId))));
        mvc.perform(get("/api/monitoring/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchCounts.COMPLETED").isNumber());
    }

    @Test
    void noPermiteSaltarseFases() throws Exception {
        long batchId = createBatch(createRecipe(uniqueName("Sin saltos")));
        mvc.perform(post("/api/batches/" + batchId + "/incubation"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("PREPARING")));
    }

    @Test
    void validaElTiempoDeIncubacionPersonalizado() throws Exception {
        long recipeId = createRecipe(uniqueName("Incubación"));
        mvc.perform(post("/api/batches").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recipeId\": " + recipeId + ", \"customIncubationHours\": 20}"))
                .andExpect(status().is(422));
        mvc.perform(post("/api/batches").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recipeId\": " + recipeId + ", \"customIncubationHours\": 8, \"customMilkVolume\": 1.5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.incubationTime").value(8))
                .andExpect(jsonPath("$.milkVolume").value(1.5));
    }

    @Test
    void descartaUnLoteYNoAdmiteMasCambios() throws Exception {
        long batchId = createBatch(createRecipe(uniqueName("Descartable")));
        String batch = "/api/batches/" + batchId;

        mvc.perform(post(batch + "/fail").contentType(MediaType.APPLICATION_JSON).content("{\"reason\": \"Leche cortada\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.notes").value("Descartado: Leche cortada"));

        mvc.perform(post(batch + "/heating")).andExpect(status().isConflict());
        mvc.perform(post(batch + "/temperature").contentType(MediaType.APPLICATION_JSON).content("{\"temperature\": 40}"))
                .andExpect(status().isConflict());
    }

    @Test
    void registraTemperaturasYCalculaElResumen() throws Exception {
        long batchId = createBatch(createRecipe(uniqueName("Temperaturas")));
        String batch = "/api/batches/" + batchId;

        for (double temperature : new double[] {42.0, 43.0, 44.0}) {
            mvc.perform(post(batch + "/temperature").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"temperature\": " + temperature + ", \"type\": \"INCUBATION\"}"))
                    .andExpect(status().isCreated());
            clock.advance(Duration.ofMinutes(5));
        }
        mvc.perform(post(batch + "/temperature").contentType(MediaType.APPLICATION_JSON).content("{\"temperature\": 41.5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("MANUAL"));

        mvc.perform(get("/api/monitoring/batches/" + batchId + "/temperature"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentTemperature").value(41.5))
                .andExpect(jsonPath("$.minimumIncubationTemperature").value(42.0))
                .andExpect(jsonPath("$.maximumIncubationTemperature").value(44.0))
                .andExpect(jsonPath("$.averageIncubationTemperature").value(43.0))
                .andExpect(jsonPath("$.incubationReadings").value(3));

        mvc.perform(get("/api/monitoring/batches/" + batchId + "/temperature-logs"))
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void validaLecturasYResponde404() throws Exception {
        mvc.perform(post("/api/batches/999999/heating")).andExpect(status().isNotFound());
        mvc.perform(get("/api/monitoring/batches/999999/temperature")).andExpect(status().isNotFound());

        long batchId = createBatch(createRecipe(uniqueName("Lecturas")));
        mvc.perform(post("/api/batches/" + batchId + "/temperature").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"temperature\": 500}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/monitoring/batches/" + batchId + "/temperature-logs").param("start", "2026-05-12T08:00:00"))
                .andExpect(status().is(422));
    }
}
