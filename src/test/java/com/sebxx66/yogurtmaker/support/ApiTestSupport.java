package com.sebxx66.yogurtmaker.support;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

/** Utilidades compartidas por las pruebas de la API. */
public abstract class ApiTestSupport {

    @Autowired
    protected MockMvc mvc;

    protected static String recipeJson(String name) {
        return """
                {
                  "name": "%s",
                  "description": "Receta de prueba",
                  "defaultMilkVolume": 2.0,
                  "defaultStarterAmount": 2.0,
                  "heatingTemperature": 85,
                  "heatingDuration": 10,
                  "inoculationTemperature": 45,
                  "incubationTemperature": 43,
                  "minIncubationTime": 6,
                  "maxIncubationTime": 10,
                  "refrigerationTime": 4,
                  "difficulty": "BEGINNER",
                  "ingredients": [
                    { "name": "Leche entera", "quantity": 2.0, "unit": "L", "optional": false },
                    { "name": "Yogurt natural", "quantity": 2.0, "unit": "cucharadas", "optional": false }
                  ]
                }
                """.formatted(name);
    }

    protected static String uniqueName(String prefix) {
        return prefix + " " + UUID.randomUUID().toString().substring(0, 8);
    }

    protected long createRecipe(String name) throws Exception {
        String body = mvc.perform(post("/api/recipes").contentType(MediaType.APPLICATION_JSON).content(recipeJson(name)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    protected long createBatch(long recipeId) throws Exception {
        String body = mvc.perform(post("/api/batches").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recipeId\": " + recipeId + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }
}
