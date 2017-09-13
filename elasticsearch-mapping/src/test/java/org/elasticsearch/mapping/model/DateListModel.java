package org.elasticsearch.mapping.model;

import org.elasticsearch.annotation.DateField;
import org.elasticsearch.annotation.ESObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ESObject()
public class DateListModel {

    @DateField(includeInAll = false, ignoreMalformed = true)
    private List<Date> dates = new ArrayList<>();

    public List<Date> getDates() {
        return dates;
    }

    public void setDates(List<Date> dates) {
        this.dates = dates;
    }
}
