package com.digitalfutures.academy.spring_demo.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
// @Data needed for equals() and hashCode() to work properly for test assertions
@Data
public class WeightLogEntry {
    @Setter
    private Double weight;
    @Getter
    private LocalDate date;

    // @JsonIgnore tells Jackson not to create a key/value pair from this method
    // Without this, each weight log entry will have an "empty" key with a boolean value based on the isEmpty() method
    @JsonIgnore
    public boolean isEmpty() {
        return this.weight == null || this.date == null || this.weight == 0.0;
    }

}