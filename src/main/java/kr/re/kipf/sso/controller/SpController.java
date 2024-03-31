package kr.re.kipf.sso.controller;

import org.opensaml.common.SAMLException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

@Controller
@RequestMapping("/sp")
public class SpController {

    @GetMapping("/request")
    public void getSAMLRequest(@RequestParam String SAMLRequest, final HttpServletRequest request, final HttpServletResponse response) throws SAMLException {

        String requestXmlString;
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] xmlBytes = SAMLRequest.getBytes(StandardCharsets.UTF_8);
        byte[] base64DecodedByteArray = decoder.decode(xmlBytes);

        try {
            Inflater inflater = new Inflater(true);
            inflater.setInput(base64DecodedByteArray);
            byte[] xmlMessageBytes = new byte[5000];
            int length = inflater.inflate(xmlMessageBytes);

            if (!inflater.finished()) {
                throw new RuntimeException("didn't allocate enough space to hold decompressed data");
            }
            inflater.end();
            requestXmlString = new String(xmlMessageBytes, 0, length, StandardCharsets.UTF_8);
        } catch (DataFormatException e) {
            throw new RuntimeException(e);
        }

        Document requestDocument;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            requestDocument = documentBuilder.parse(requestXmlString);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }

        if (requestDocument == null) {
            throw new SAMLException("Error parsing AuthnRequest XML: Null document");
        }

        String issueInstant = requestDocument.getDocumentElement().getAttribute("IssueInstant");
        String providerName = requestDocument.getDocumentElement().getAttribute("ProviderName");
        String ACS_URL = requestDocument.getDocumentElement().getAttribute("AssertionConsumerServiceURL");
        String id = requestDocument.getDocumentElement().getAttribute("ID");


        /**
         * id를 이용하여 인증 진행
         */


//        Document doc = Util.createJdomDoc(requestXmlString);
//        if (doc != null) {
//            String[] samlRequestAttributes = new String[3];
//            samlRequestAttributes[0] = doc.getRootElement().getAttributeValue("IssueInstant");
//            samlRequestAttributes[1] = doc.getRootElement().getAttributeValue("ProviderName");
//            samlRequestAttributes[2] = doc.getRootElement().getAttributeValue("AssertionConsumerServiceURL");
//            return samlRequestAttributes;
//        } else {
//            throw new SamlException("Error parsing AuthnRequest XML: Null document");
//        }

//        AuthnRequest authRequest;
//        try {
//            ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getEncoder().encode(SAMLRequest.getBytes()));
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            factory.setNamespaceAware(true);
//            DocumentBuilder docBuilder = factory.newDocumentBuilder();
//            Document samlDocument = docBuilder.parse(stream);
//            Element samlElem = samlDocument.getDocumentElement();
//            UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
//            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(samlElem);
//            Object requestXmlObj = unmarshaller.unmarshall(samlElem);
//            authRequest = (AuthnRequest) requestXmlObj;
//        } catch (Exception ignored) {
//        }


//            byte[] samlToken = EidasStringUtil.decodeBytesFromBase64(encodedSAMLRequest);
//            //equal to base64.decode
//
//            ByteArrayInputStream stream = new ByteArrayInputStream(samlToken);
//            DefaultBootstrap.bootstrap();
//
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            factory.setNamespaceAware(true);
//            DocumentBuilder docBuilder = factory.newDocumentBuilder();
//            Document samlDocument = docBuilder.parse(stream);
//            Element samlElem = samlDocument.getDocumentElement();
//
//            UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
//            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(samlElem);
//            Object requestXmlObj = unmarshaller.unmarshall(samlElem);
//            AuthnRequest authRequest = (AuthnRequest) requestXmlObj;
    }
}
