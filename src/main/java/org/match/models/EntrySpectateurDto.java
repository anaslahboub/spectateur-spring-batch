package org.match.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.*;
import org.match.utils.LocalDateTimeAdapter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@XmlRootElement(name = "spectatorEntry")
@XmlAccessorType(XmlAccessType.FIELD)
// Force l'ordre des champs pour le XML (JAXB)
@XmlType(propOrder = {
        "spectatorId",
        "matchId",
        "entryTime",
        "gate",
        "ticketNumber",
        "age",
        "nationality",
        "ticketType",
        "seatLocation"
})
// Force l'ordre des champs pour le JSON (Jackson) - Optionnel mais propre pour l'export
@JsonPropertyOrder({
        "spectatorId",
        "matchId",
        "entryTime",
        "gate",
        "ticketNumber",
        "age",
        "nationality",
        "ticketType",
        "seatLocation"
})
public class EntrySpectateurDto {

    private String spectatorId;

    private String matchId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime entryTime;

    private String gate;

    private String ticketNumber;

    private Integer age;

    private String nationality;

    private String ticketType;

    @JsonProperty("seatLocation")
    @XmlElement(name = "seatLocation")
    private SeatLocationDto seatLocation;
}