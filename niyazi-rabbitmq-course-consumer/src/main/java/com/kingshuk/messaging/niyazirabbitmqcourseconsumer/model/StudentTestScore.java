package com.kingshuk.messaging.niyazirabbitmqcourseconsumer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StudentTestScore implements Serializable{
    private String semester;
    private double testScore;
}