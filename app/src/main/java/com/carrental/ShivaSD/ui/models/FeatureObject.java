package com.carrental.ShivaSD.ui.models;


public class FeatureObject {

    private final String featureTitle;
    private final String featureValue;

    public FeatureObject(String featureTitle, String featureValue) {
        this.featureTitle = featureTitle;
        this.featureValue = featureValue;
    }

    public String getFeatureTitle() {
        return featureTitle;
    }

    public String getFeatureValue() {
        return featureValue;
    }
}
