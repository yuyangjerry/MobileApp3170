package com.FIT3170.HealthMonitor;

/**
 * Each attribute in the user profile menu has its own model, used to store the attribute name
 * and its value
 */
public class ProfileAttributeModel {
    private String attribute_name;
    private String attribute_value;

    public ProfileAttributeModel(String attribute_name, String attribute_value) {
        this.attribute_name = attribute_name;
        this.attribute_value = attribute_value;
    }

    public String getAttribute_name() {
        return attribute_name;
    }

    public String getAttribute_value() {
        return attribute_value;
    }

    public void setAttribute_name(String attribute_name) {
        this.attribute_name = attribute_name;
    }

}
