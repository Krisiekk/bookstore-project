package pl.kpietrzak.bookstore.mapper;


import org.springframework.stereotype.Component;
import pl.kpietrzak.bookstore.dto.reservation.ReservationResponse;
import pl.kpietrzak.bookstore.entity.Reservation;

import java.util.Locale;

@Component
public class ReservationMapper {
    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getUser().getUsername(),
                reservation.getBook().getId(),
                reservation.getBook().getTitle(),
                reservation.getStatus(),
                reservation.getCreatedAt()

        );
    }

}
