package com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransmissionLine {
    private String id;             // Unique identifier (typically fromId-toId)
    private String from;           // Source district ID
    private String to;             // Destination district ID
    private int capacity;          // Maximum power that can flow
    private double resistance;     // Line resistance (affects losses)
    private double flow;           // Current power flow

    // Calculated properties
    private double utilization;    // Flow as a percentage of capacity
    private boolean overloaded;    // True if flow > capacity

    public TransmissionLine(String fromId, String toId, int capacity, double resistance) {
        this.id = fromId + "-" + toId;
        this.from = fromId;
        this.to = toId;
        this.capacity = capacity;
        this.resistance = resistance;
        this.flow = 0;
        this.utilization = 0;
        this.overloaded = false;
    }


    public void setFlow(double flow) {
        this.flow = flow;
        this.utilization = flow / capacity;
        this.overloaded = flow > capacity;
    }
}



