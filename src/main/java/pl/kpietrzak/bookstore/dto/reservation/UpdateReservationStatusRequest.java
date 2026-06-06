package pl.kpietrzak.bookstore.dto.reservation;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pl.kpietrzak.bookstore.enums.ReservationStatus;

@Getter
@Setter
public class UpdateReservationStatusRequest {
    @NotNull(message="Reservation status is required")
    private ReservationStatus status;
}
