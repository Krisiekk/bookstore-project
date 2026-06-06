package pl.kpietrzak.bookstore.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.kpietrzak.bookstore.enums.ReservationStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationResponse {

    private Long id;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitle;
    private ReservationStatus status;
    private LocalDateTime createdAt;
}
