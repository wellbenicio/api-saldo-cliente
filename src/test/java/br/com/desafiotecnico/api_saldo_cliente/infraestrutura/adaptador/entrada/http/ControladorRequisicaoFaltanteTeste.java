package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teste/requisicao")
class ControladorRequisicaoFaltanteTeste {

    @GetMapping("/{idConta}")
    public String consultar(
            @PathVariable String idConta,
            @RequestHeader("X-Id-Titular") String idTitular,
            @RequestParam("pagina") Integer pagina
    ) {
        return idConta + idTitular + pagina;
    }
}
