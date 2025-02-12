package com.gala.celebrations.rsvpbackend.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ServerConfig {

    private final Environment environment;

    public ServerConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();

        // Check if 'dev' profile is active using getActiveProfiles
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if (profile.equals("dev")) {
                tomcat.addAdditionalTomcatConnectors(createHttpConnector());
                break;
            }
        }

        return tomcat;
    }

    private Connector createHttpConnector() {
        Connector connector = new Connector(Http11NioProtocol.class.getName());
        connector.setScheme("http");
        connector.setPort(8080);         // HTTP port
        connector.setSecure(false);
        connector.setRedirectPort(8443); // Redirect HTTP to HTTPS
        return connector;
    }
}
