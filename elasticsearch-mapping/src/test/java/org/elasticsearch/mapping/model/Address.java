package org.elasticsearch.mapping.model;

import org.elasticsearch.annotation.StringField;
import org.elasticsearch.annotation.query.MatchFilter;

public class Address {
    @MatchFilter
    @StringField(includeInAll = false, index = false)
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}