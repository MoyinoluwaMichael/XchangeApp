package org.example.Xchange.dtos.response.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;
import org.example.Xchange.models.FxRates;

import java.io.Serializable;

@Setter
@Getter
@ToString
//@XmlRootElement(name = "FxRates", namespace = "http://www.lb.lt/WebServices/FxRates")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetExchangeRatesResponse implements Serializable {

    @XmlElement(name = "FxRates")
    @JsonProperty("FxRates")
    private FxRates fxRates;
}
