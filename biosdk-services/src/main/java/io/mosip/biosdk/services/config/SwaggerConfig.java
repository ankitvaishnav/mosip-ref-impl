package io.mosip.biosdk.services.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {
    /** The local env. */
    @Value("${application.env.local:false}")
    private Boolean localEnv;

    /** The swagger url. */
    @Value("${swagger.base-url:#{null}}")
    private String swaggerUrl;

    /** The server port. */
    @Value("${server.port:9099}")
    private int serverPort;

    /** The proto. */
    String proto = "http";

    /** The host. */
    String host = "localhost";

    /** The port. */
    int port = -1;

    /** The host with port. */
    String hostWithPort = "localhost:9099";

    /**
     * Gets the api info.
     *
     * @return the api info
     */
    ApiInfo getApiInfo() {
        return new ApiInfoBuilder().title("Id Repository Identity Service").description("Id Repository Identity Service").build();
    }

    /**
     * Api.
     *
     * @return the docket
     * @throws MalformedURLException the malformed URL exception
     */
    @Bean
    public Docket api() throws MalformedURLException {
        boolean targetSwagger = false;
        if (!localEnv && swaggerUrl != null && !swaggerUrl.isEmpty()) {
            try {
                proto = new URL(swaggerUrl).getProtocol();
                host = new URL(swaggerUrl).getHost();
                port = new URL(swaggerUrl).getPort();
                if (port == -1) {
                    hostWithPort = host;
                } else {
                    hostWithPort = host + ":" + port;
                }
                targetSwagger = true;
            } catch (MalformedURLException e) {
                throw e;
            }
        }
        Docket docket = new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any()).build().apiInfo(getApiInfo());

        if (targetSwagger) {
            docket.protocols(protocols()).host(hostWithPort);
        }

        return docket;
    }

    /**
     * Protocols.
     *
     * @return the sets the
     */
    private Set<String> protocols() {
        Set<String> protocols = new HashSet<>();
        protocols.add(proto);
        return protocols;
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
