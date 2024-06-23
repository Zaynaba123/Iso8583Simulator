package com.example.isoserveurservice.iso;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestResponseProvider {
    private List<RequestResponseCase> cases;

    public RequestResponseProvider(String filePath) {
        cases = new ArrayList<>();
        loadCases("CardCfg/card.xml");
    }

    private void loadCases(String filePath) {
        try {
            File inputFile = new File("CardCfg/card.xml");
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputFile);
            Element rootElement = document.getRootElement();

            for (Element caseElement : rootElement.getChildren("case")) {
                String caseName = caseElement.getAttributeValue("name");
                Map<String, String> requestFields = new HashMap<>();
                Map<String, String> responseFields = new HashMap<>();

                Element requestElement = caseElement.getChild("request");
                for (Element fieldElement : requestElement.getChildren("field")) {
                    requestFields.put(fieldElement.getAttributeValue("id"), fieldElement.getAttributeValue("value"));
                }

                Element responseElement = caseElement.getChild("response");
                for (Element fieldElement : responseElement.getChildren("field")) {
                    responseFields.put(fieldElement.getAttributeValue("id"), fieldElement.getAttributeValue("value"));
                }

                cases.add(new RequestResponseCase(caseName, requestFields, responseFields));
            }
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getResponseForRequest(Map<String, String> requestFields) {
        for (RequestResponseCase rc : cases) {
            boolean allFieldsMatch = true;
            for (Map.Entry<String, String> entry : rc.getRequestFields().entrySet()) {
                if (!entry.getValue().equals(requestFields.get(entry.getKey()))) {
                    allFieldsMatch = false;
                    break;
                }
            }
            if (allFieldsMatch) {
                return rc.getResponseFields();
            }
        }
        return null;
    }
}
