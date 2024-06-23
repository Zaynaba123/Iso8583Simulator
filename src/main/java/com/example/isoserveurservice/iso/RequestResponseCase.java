package com.example.isoserveurservice.iso;

import java.util.Map;

public class RequestResponseCase {
    private String caseName;
    private Map<String, String> requestFields;
    private Map<String, String> responseFields;

    public RequestResponseCase(String caseName, Map<String, String> requestFields, Map<String, String> responseFields) {
        this.caseName = caseName;
        this.requestFields = requestFields;
        this.responseFields = responseFields;
    }

    public String getCaseName() {
        return caseName;
    }

    public Map<String, String> getRequestFields() {
        return requestFields;
    }

    public Map<String, String> getResponseFields() {
        return responseFields;
    }
}
