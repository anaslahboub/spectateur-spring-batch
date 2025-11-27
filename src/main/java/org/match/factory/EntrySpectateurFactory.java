package org.match.factory;

import org.match.models.*;

public class EntrySpectateurFactory {


    public static EntrySpectateur  createEntrySpectateur(EntrySpectateurDto item) {

        TicketType ticketType = TicketType.valueOf(item.getTicketType().trim().toUpperCase());

        Spectateur  spectateur = SpectateurFactory.createSpectateur(item.getSpectatorId(), item.getAge(),  item.getNationality());
        SeatLocation seatLocation = SeatLocation.builder()
                .bloc(item.getSeatLocation().getBloc())
                .rang(item.getSeatLocation().getRang())
                .tribune(item.getSeatLocation().getTribune())
                .siege(item.getSeatLocation().getSiege())
                .build();

        return EntrySpectateur.builder()
                .matchId(item.getMatchId())
                .entryTime(item.getEntryTime())
                .gate(item.getGate())
                .ticketNumber(item.getTicketNumber())
                .ticketType(ticketType)
                .seatLocation(seatLocation)
                .spectateur(spectateur)
                .build();
    }
}
