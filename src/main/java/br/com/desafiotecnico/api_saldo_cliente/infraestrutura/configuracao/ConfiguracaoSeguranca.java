package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao;

import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.FiltroAutenticacaoCabecalho;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class ConfiguracaoSeguranca {

    private final FiltroAutenticacaoCabecalho filtroAutenticacaoCabecalho;

    public ConfiguracaoSeguranca(FiltroAutenticacaoCabecalho filtroAutenticacaoCabecalho) {
        this.filtroAutenticacaoCabecalho = filtroAutenticacaoCabecalho;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/v1/contas/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(filtroAutenticacaoCabecalho, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
