package com.example.stubapi.soap;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/stub/soap/*");
    }

    @Bean(name = "simple")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema simpleSchema) {
        DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
        definition.setPortTypeName("SimplePort");
        definition.setLocationUri("/stub/soap");
        definition.setTargetNamespace(StubSoapEndpoint.NAMESPACE_URI);
        definition.setSchema(simpleSchema);
        return definition;
    }

    @Bean
    public XsdSchema simpleSchema() {
        return new SimpleXsdSchema(new ClassPathResource("ws/schemas/simple.xsd"));
    }
}
