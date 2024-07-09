package com.example.isoserveurservice.iso;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RequestResponseProvider {
    private List<RequestResponseCase> cases;
    private Map<Integer, Integer> fieldEcodes;

    public RequestResponseProvider(String caseFilePath, String ecodeFilePath) {
        cases = new ArrayList<>();
        fieldEcodes = new HashMap<>();
        loadCases("CardCfg/card.xml");
        loadFieldEcodes("CardCfg/FieldEcodes.xml");
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


    private void loadFieldEcodes(String filePath) {
        try {
            File inputFile = new File("CardCfg/FieldEcodes.xml");
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputFile);
            Element rootElement = document.getRootElement();

            for (Element fieldElement : rootElement.getChildren("field")) {
                String idStr = fieldElement.getAttributeValue("id");
                String ecodeStr = fieldElement.getAttributeValue("ecode");

                if (idStr != null && ecodeStr != null && !idStr.isEmpty() && !ecodeStr.isEmpty()) {
                    try {
                        int id = Integer.parseInt(idStr);
                        int ecode = Integer.parseInt(ecodeStr);
                        fieldEcodes.put(id, ecode);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format in FieldEcodes.xml: " + e.getMessage());
                    }
                } else {
                    System.err.println("Missing or empty 'id' or 'ecode' attribute in FieldEcodes.xml");
                }
            }
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
    }

    public List<RequestResponseCase> getCases() {
        return cases;
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

    public Map<Integer, Integer> getFieldEcodes() {
        return fieldEcodes;
    }
}
