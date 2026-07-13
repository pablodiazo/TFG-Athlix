package es.udc.fi.dc.fd.model.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IntensityZone {
    SUAVE("Suave"), AER1("AER1"), AER2("AER2"), AER3("AER3"), FUERTE("Fuerte"),
    
    R0("R0"), R1("R1"), R1_PLUS("R1+"), R2("R2"), R3("R3"), R3_PLUS("R3+"), R4("R4"), R5("R5"), R6("R6"),
    
    Z1("Z1"), Z2("Z2"), Z3("Z3"), Z4("Z4"), Z5("Z5"), Z6("Z6"), Z7("Z7"),
    
    NONE("-");

    private final String label;

    IntensityZone(String label) {
        this.label = label;
    }

    @JsonValue 
    public String getLabel() {
        return label;
    }

    @JsonCreator 
    public static IntensityZone fromLabel(String label) {
        if (label == null || label.trim().isEmpty() || label.equals("0")) {
            return NONE;
        }
        for (IntensityZone zone : IntensityZone.values()) {
            if (zone.label.equalsIgnoreCase(label)) {
                return zone;
            }
        }
        return NONE; 
    }
}