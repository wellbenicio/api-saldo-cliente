package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ConfiguracaoSeguranca {

    /**
     * Fase atual: filtro de segurança explicitamente permissivo para manter o
     * endpoint de saldo acessível durante a evolução da API.
     *
     * A autenticação real (identidade do usuário) será adicionada em etapa
     * posterior. Já a autorização por titularidade (se o solicitante pode ver
     * a conta) permanece como regra de domínio/aplicação no caso de uso de
     * consulta de saldo.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/v1/contas/**").permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
