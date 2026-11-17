package com.sebxx66.yogurtmaker;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import com.sebxx66.yogurtmaker.support.ApiTestSupport;

@SpringBootTest
@AutoConfigureMockMvc
class RecipeApiTest extends ApiTestSupport {

    @Test
    void creaYConsultaUnaReceta() throws Exception {
        String name = uniqueName("Natural");
        mvc.perform(post("/api/recipes").contentType(MediaType.APPLICATION_JSON).content(recipeJson(name)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.ingredients.length()").value(2))
                .andExpect(jsonPath("$.ingredients[0].name").value("Leche entera"));

        mvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem(name)));
    }

    @Test
    void rechazaCamposInvalidosConDetalle() throws Exception {
        mvc.perform(post("/api/recipes").contentType(MediaType.APPLICATION_JSON).content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Datos inválidos"))
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.heatingTemperature").exists());
    }

    @Test
    void rechazaParametrosIncoherentes() throws Exception {
        String json = recipeJson(uniqueName("Incoherente")).replace("\"minIncubationTime\": 6", "\"minIncubationTime\": 12");
        mvc.perform(post("/api/recipes").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().is(422));
    }

    @Test
    void noPermiteNombresDuplicados() throws Exception {
        String name = uniqueName("Duplicada");
        createRecipe(name);
        mvc.perform(post("/api/recipes").contentType(MediaType.APPLICATION_JSON).content(recipeJson(name.toUpperCase())))
                .andExpect(status().isConflict());
    }

    @Test
    void actualizaYReemplazaIngredientes() throws Exception {
        long id = createRecipe(uniqueName("Editable"));
        String json = recipeJson(uniqueName("Editada")).replace(
                "{ \"name\": \"Yogurt natural\", \"quantity\": 2.0, \"unit\": \"cucharadas\", \"optional\": false }",
                "{ \"name\": \"Miel\", \"quantity\": 50, \"unit\": \"g\", \"optional\": true }");
        mvc.perform(put("/api/recipes/" + id).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredients.length()").value(2))
                .andExpect(jsonPath("$.ingredients[1].name").value("Miel"))
                .andExpect(jsonPath("$.ingredients[1].id").isNumber());
    }

    @Test
    void desactivaYReactivaUnaReceta() throws Exception {
        long id = createRecipe(uniqueName("Temporal"));
        mvc.perform(patch("/api/recipes/" + id + "/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
        mvc.perform(post("/api/batches").contentType(MediaType.APPLICATION_JSON).content("{\"recipeId\": " + id + "}"))
                .andExpect(status().is(422));
        mvc.perform(patch("/api/recipes/" + id + "/activate"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void buscaPorPalabraClave() throws Exception {
        String name = uniqueName("Griego especial");
        createRecipe(name);
        mvc.perform(get("/api/recipes/search").param("keyword", "griego ESPECIAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem(name)));
    }

    @Test
    void responde404SiLaRecetaNoExiste() throws Exception {
        mvc.perform(get("/api/recipes/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("La receta con id 999999 no existe"));
    }
}
