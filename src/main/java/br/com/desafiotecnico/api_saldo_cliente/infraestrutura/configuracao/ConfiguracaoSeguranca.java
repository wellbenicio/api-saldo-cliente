package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao;

import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.ConversorJwtAutenticacao;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.ManipuladorAcessoNegado;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.ManipuladorAutenticacaoNaoAutenticado;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ConfiguracaoSeguranca {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ConversorJwtAutenticacao conversorJwtAutenticacao,
            ManipuladorAutenticacaoNaoAutenticado manipuladorAutenticacaoNaoAutenticado,
            ManipuladorAcessoNegado manipuladorAcessoNegado
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/v1/contas/*/saldo").authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(manipuladorAutenticacaoNaoAutenticado)
                        .accessDeniedHandler(manipuladorAcessoNegado)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(conversorJwtAutenticacao))
                        .authenticationEntryPoint(manipuladorAutenticacaoNaoAutenticado)
                        .accessDeniedHandler(manipuladorAcessoNegado)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
