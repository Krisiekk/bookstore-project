package pl.kpietrzak.bookstore.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.kpietrzak.bookstore.dto.loan.LoanResponse;
import pl.kpietrzak.bookstore.service.LoanService;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@SecurityRequirement(name = "bearerAuth")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/reservations/{reservationId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public LoanResponse createLoanFromReservation(@PathVariable Long reservationId) {
        return loanService.createLoanFromReservation(reservationId);
    }

    @PatchMapping("/{loanId}/return")
    @PreAuthorize("hasRole('ADMIN')")
    public LoanResponse returnLoan(@PathVariable Long loanId) {
        return loanService.returnLoan(loanId);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<LoanResponse> getMyLoans(Authentication authentication) {
        return loanService.getMyLoans(authentication);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<LoanResponse> getAllLoans() {
        return loanService.getAllLoans();
    }
}
