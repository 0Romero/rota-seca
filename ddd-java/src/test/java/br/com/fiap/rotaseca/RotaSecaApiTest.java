package br.com.fiap.rotaseca;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Testes de ponta a ponta da API, rodando no H2 (perfil dev) com os dados de exemplo. */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:${random.uuid};MODE=Oracle;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RotaSecaApiTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void listaOsDadosDeExemplo() throws Exception {
        mvc.perform(get("/api/bairros")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(5)));
        mvc.perform(get("/api/pontos-risco?bairroId=1")).andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/api/pontos-apoio?bairroId=4")).andExpect(jsonPath("$", hasSize(0)));
        mvc.perform(get("/api/moradores/1")).andExpect(jsonPath("$.prioritario").value(true));
        mvc.perform(get("/api/ocorrencias?status=ABERTA")).andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void crudCompletoDeBairro() throws Exception {
        String json = """
                {"nome": "Itaim Paulista", "regiao": "Leste", "cidade": "São Paulo", "populacao": 1000}
                """;
        mvc.perform(post("/api/bairros").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/bairros/6")))
                .andExpect(jsonPath("$.id").value(6));

        mvc.perform(put("/api/bairros/6").contentType(MediaType.APPLICATION_JSON)
                        .content(json.replace("1000", "2000")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.populacao").value(2000));

        mvc.perform(delete("/api/bairros/6")).andExpect(status().isNoContent());
        mvc.perform(get("/api/bairros/6")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Bairro com id 6 não encontrado(a)"));
    }

    @Test
    void chuvaForteGeraAlertaAtivoParaOMorador() throws Exception {
        String leitura = """
                {"bairroId": 1, "precipitacaoMm": 55.0, "umidade": 97, "fonte": "Sensor simulado"}
                """;
        mvc.perform(post("/api/leituras").contentType(MediaType.APPLICATION_JSON).content(leitura))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nivelRisco").value("MUITO_ALTO"))
                .andExpect(jsonPath("$.alertaGerado.nivel").value("MUITO_ALTO"))
                .andExpect(jsonPath("$.alertaGerado.mensagem", containsString("Escola Municipal do Bairro")));

        mvc.perform(get("/api/moradores/1/alertas"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void chuvaFracaNaoGeraAlerta() throws Exception {
        String leitura = """
                {"bairroId": 2, "precipitacaoMm": 3.0, "fonte": "Sensor simulado"}
                """;
        mvc.perform(post("/api/leituras").contentType(MediaType.APPLICATION_JSON).content(leitura))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nivelRisco").value("BAIXO"))
                .andExpect(jsonPath("$.alertaGerado").value(nullValue()));
    }

    @Test
    void ocorrenciaAtualizaLaminaMaximaERespeitaCicloDeStatus() throws Exception {
        String ocorrencia = """
                {"pontoRiscoId": 4, "moradorId": 4, "laminaCm": 60.0, "observacao": "Água acima do joelho"}
                """;
        mvc.perform(post("/api/ocorrencias").contentType(MediaType.APPLICATION_JSON).content(ocorrencia))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("ABERTA"));
        mvc.perform(get("/api/pontos-risco/4")).andExpect(jsonPath("$.laminaMaximaCm").value(60.0));

        // Ocorrência 1 já está ENCERRADA: não pode voltar a ABERTA
        String reabrir = """
                {"pontoRiscoId": 1, "laminaCm": 75.0, "status": "ABERTA"}
                """;
        mvc.perform(put("/api/ocorrencias/1").contentType(MediaType.APPLICATION_JSON).content(reabrir))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void errosRetornamCorpoPadronizado() throws Exception {
        mvc.perform(post("/api/moradores").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bairroId\": 1, \"nome\": \"\", \"telefone\": \"123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos", hasSize(3)));

        mvc.perform(post("/api/pontos-apoio").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bairroId\": 99, \"nome\": \"X\", \"tipo\": \"ABRIGO\", \"endereco\": \"Rua\","
                                + " \"latitude\": -23.5, \"longitude\": -46.4, \"capacidade\": 10}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem").value("O bairro 99 não existe"));

        mvc.perform(post("/api/pontos-apoio").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\": \"HOSPITAL\"}"))
                .andExpect(status().isBadRequest());

        // Bairro 1 tem pontos de risco, leituras e moradores ligados a ele
        mvc.perform(delete("/api/bairros/1")).andExpect(status().isConflict());
    }
}
