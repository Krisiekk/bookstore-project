package pl.kpietrzak.bookstore.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.kpietrzak.bookstore.enums.LoanStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LoanResponse {

    private Long id;
    private Long reservationId;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;
    private LoanStatus status;
}
