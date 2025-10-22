package com.example.stubapi.soap;

import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.xpath;

import javax.xml.transform.Source;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;

@SpringBootTest
class StubSoapEndpointTest {

    @Autowired
    private ApplicationContext applicationContext;

    private MockWebServiceClient mockClient;

    @BeforeEach
    void setUp() {
        mockClient = MockWebServiceClient.createClient(applicationContext);
    }

    @Test
    void returnsDefaultMessageWhenCodeMissing() {
        Source request = new StringSource("""
            <ns2:SimpleRequest xmlns:ns2=\"http://example.com/stub/soap\"/>
        """);

        mockClient.sendRequest(withPayload(request))
            .andExpect(xpath("/*[local-name()='SimpleResponse']/*[local-name()='message']")
                .evaluatesTo("Default SOAP stub response"));
    }

    @Test
    void echoesCodeValueInResponse() {
        Source request = new StringSource("""
            <ns2:SimpleRequest xmlns:ns2=\"http://example.com/stub/soap\">
                <ns2:code>ABC-123</ns2:code>
            </ns2:SimpleRequest>
        """);
        mockClient.sendRequest(withPayload(request))
            .andExpect(xpath("/*[local-name()='SimpleResponse']/*[local-name()='message']")
                .evaluatesTo("Response for code ABC-123"));
    }
}
