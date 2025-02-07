package net.debreczenichis.remotedesktop.configs;

import net.debreczenichis.remotedesktop.security.PasswordHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

@Configuration
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
public class RSocketSecurityConfig {

    @Bean
    RSocketMessageHandler messageHandler(RSocketStrategies strategies) {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.getArgumentResolverConfigurer().addCustomResolver(new AuthenticationPrincipalArgumentResolver());
        handler.setRSocketStrategies(strategies);
        return handler;
    }

    @Bean
    MapReactiveUserDetailsService authentication() {
        PasswordHandler passwordHandler = new PasswordHandler();

        UserDetails view = User.withDefaultPasswordEncoder()
                .username("view")
                .password(passwordHandler.getEncryptedViewPin())
                .roles("VIEW")
                .build();

        UserDetails control = User.withDefaultPasswordEncoder()
                .username("control")
                .password(passwordHandler.getEncryptedControlPin())
                .roles("VIEW", "CONTROL")
                .build();

        return new MapReactiveUserDetailsService(view, control);
    }

    @Bean
    PayloadSocketAcceptorInterceptor authorization(RSocketSecurity security) {
        security
                .authorizePayload(authorize ->
                        authorize
                                .anyExchange()
                                .authenticated() // all connections, exchanges.
                )
                .simpleAuthentication(Customizer.withDefaults());
        return security.build();
    }
}