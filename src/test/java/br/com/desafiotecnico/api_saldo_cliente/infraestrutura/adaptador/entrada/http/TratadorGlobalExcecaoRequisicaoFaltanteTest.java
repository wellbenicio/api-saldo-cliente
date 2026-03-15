package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import br.com.desafiotecnico.api_saldo_cliente.compartilhado.web.TratadorGlobalExcecao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ControladorRequisicaoFaltanteTeste.class)
@Import(TratadorGlobalExcecao.class)
@AutoConfigureMockMvc(addFilters = false)
class TratadorGlobalExcecaoRequisicaoFaltanteTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornarErroPadronizadoQuandoCabecalhoObrigatorioNaoForInformado() throws Exception {
        mockMvc.perform(get("/teste/requisicao/12345").queryParam("pagina", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("REQUISICAO_INVALIDA"))
                .andExpect(jsonPath("$.mensagem").value("Cabeçalho 'X-Id-Titular' é obrigatório."))
                .andExpect(jsonPath("$.detalhes[0].campo").value("X-Id-Titular"))
                .andExpect(jsonPath("$.detalhes[0].mensagem").value("Cabeçalho 'X-Id-Titular' é obrigatório."));
    }

    @Test
    void deveRetornarErroPadronizadoQuandoParametroObrigatorioNaoForInformado() throws Exception {
        mockMvc.perform(get("/teste/requisicao/12345").header("X-Id-Titular", "titular-123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("REQUISICAO_INVALIDA"))
                .andExpect(jsonPath("$.mensagem").value("Parâmetro 'pagina' é obrigatório."))
                .andExpect(jsonPath("$.detalhes[0].campo").value("pagina"))
                .andExpect(jsonPath("$.detalhes[0].mensagem").value("Parâmetro 'pagina' é obrigatório."));
    }
}
