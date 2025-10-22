package com.example.stubapi.soap;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.xml.transform.StringSource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@Endpoint
public class StubSoapEndpoint {

    static final String NAMESPACE_URI = "http://example.com/stub/soap";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SimpleRequest")
    @ResponsePayload
    public StringSource handleSimpleRequest(@RequestPayload Source request) throws TransformerException {
        String code = extractValue(request, "code");
        String message = (code == null || code.isBlank())
            ? "Default SOAP stub response"
            : "Response for code " + code.trim();

        String payload = """
            <ns2:SimpleResponse xmlns:ns2=\"http://example.com/stub/soap\">
                <message>%s</message>
            </ns2:SimpleResponse>
            """.formatted(escapeXml(message));
        return new StringSource(payload);
    }

    private String extractValue(Source request, String localPart) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMResult result = new DOMResult();
        transformer.transform(request, result);
        if (result.getNode() instanceof Document document) {
            NodeList nodes = document.getElementsByTagNameNS("*", localPart);
            if (nodes.getLength() == 0) {
                nodes = document.getElementsByTagName(localPart);
            }
            if (nodes.getLength() > 0) {
                return nodes.item(0).getTextContent();
            }
        }
        return null;
    }

    private String escapeXml(String input) {
        String value = input;
        value = value.replace("&", "&amp;");
        value = value.replace("<", "&lt;");
        value = value.replace(">", "&gt;");
        value = value.replace("\"", "&quot;");
        value = value.replace("'", "&apos;");
        return value;
    }
}
