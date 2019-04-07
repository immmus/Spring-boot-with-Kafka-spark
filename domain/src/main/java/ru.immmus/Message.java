package ru.immmus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty("id_sample")
    private String idSample;
    @JsonProperty("num_id")
    private String numId;
    @JsonProperty("id_location")
    private String idLocation;
    @JsonProperty("id_signal_par")
    private String idSignalPar;
    @JsonProperty("id_detected")
    private String idDetected;
    @JsonProperty("id_class_det")
    private String idClassDet;

}
