package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.jwt;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propriedades de segurança JWT desacopladas da implementação para evitar
 * valores sensíveis hardcoded em código-fonte.
 */
@Component
@ConfigurationProperties(prefix = "seguranca.jwt")
public class PropriedadesSegurancaJwt {

    /**
     * Habilita ou desabilita a validação JWT no ambiente corrente.
     */
    private boolean habilitado;

    /**
     * Segredo compartilhado para cenários com assinatura simétrica (HS256).
     *
     * Para ambientes reais, externalize via variável de ambiente/secret manager.
     */
    private String segredoAssinatura;

    /**
     * Issuer esperado para os tokens recebidos.
     *
     * Em produção, configure o issuer real via application.properties,
     * variável de ambiente ou secret manager (ex.: HashiCorp Vault, AWS Secrets
     * Manager, Azure Key Vault).
     */
    private String issuer;

    /**
     * Endpoint do JWK Set para validação de assinatura.
     *
     * Em produção, aponte para o endpoint oficial do provedor de identidade
     * (ex.: https://idp.exemplo.com/.well-known/jwks.json).
     */
    private String jwkSetUri;

    /**
     * Chave pública PEM alternativa quando o ambiente não usa JWK Set remoto.
     *
     * Não versionar chaves reais no repositório; externalize por variável de
     * ambiente/secret manager.
     */
    private String chavePublicaPem;

    /**
     * Audience esperada do token (opcional na fase atual).
     */
    private String audience;

    /**
     * Tolerância para pequenas diferenças de relógio entre serviços.
     */
    private Duration toleranciaClockSkew = Duration.ofSeconds(30);

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public String getSegredoAssinatura() {
        return segredoAssinatura;
    }

    public void setSegredoAssinatura(String segredoAssinatura) {
        this.segredoAssinatura = segredoAssinatura;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getJwkSetUri() {
        return jwkSetUri;
    }

    public void setJwkSetUri(String jwkSetUri) {
        this.jwkSetUri = jwkSetUri;
    }

    public String getChavePublicaPem() {
        return chavePublicaPem;
    }

    public void setChavePublicaPem(String chavePublicaPem) {
        this.chavePublicaPem = chavePublicaPem;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public Duration getToleranciaClockSkew() {
        return toleranciaClockSkew;
    }

    public void setToleranciaClockSkew(Duration toleranciaClockSkew) {
        this.toleranciaClockSkew = toleranciaClockSkew;
    }
}
