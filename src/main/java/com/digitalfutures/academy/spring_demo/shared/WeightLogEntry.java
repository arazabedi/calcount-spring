package com.digitalfutures.academy.spring_demo.shared;

import java.time.LocalDate;

public class WeightLogEntry {
    private Double weight;
    private LocalDate date;

    public WeightLogEntry() {}

    public WeightLogEntry(Double weight, LocalDate date) {
        this.weight = weight;
        this.date = date;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}