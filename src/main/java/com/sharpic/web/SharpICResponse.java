package com.sharpic.web;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by joey on 2017-01-11.
 */
public class SharpICResponse {
    private boolean successful = false;
    private String errorText;
    private String warningText;
    private Map<String, Object> model = new HashMap<String, Object>();

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public String getWarningText() {
        return warningText;
    }

    public void setWarningText(String warningText) {
        this.warningText = warningText;
    }

    public void addToModel(String key, Object obj) {
        model.put(key, obj);
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> data) {
        this.model = data;
    }
}
