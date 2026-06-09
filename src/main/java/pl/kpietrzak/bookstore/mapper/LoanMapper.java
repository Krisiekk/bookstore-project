package pl.kpietrzak.bookstore.mapper;

import org.springframework.stereotype.Component;
import pl.kpietrzak.bookstore.dto.loan.LoanResponse;
import pl.kpietrzak.bookstore.entity.Loan;

@Component
public class LoanMapper {

    public LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getReservation().getId(),
                loan.getUser().getId(),
                loan.getUser().getUsername(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getBorrowedAt(),
                loan.getReturnedAt(),
                loan.getStatus()
        );
    }
}
