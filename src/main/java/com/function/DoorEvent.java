package com.function;

import lombok.Data;

@Data
public class DoorEvent {
    private String time;
    private String vin;

    public DoorEvent(String time, String vin) {
        this.time = time;
        this.vin = vin;
    }
}
