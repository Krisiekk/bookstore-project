package pl.kpietrzak.bookstore.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.kpietrzak.bookstore.dto.reservation.ReservationResponse;
import pl.kpietrzak.bookstore.dto.reservation.UpdateReservationStatusRequest;
import pl.kpietrzak.bookstore.service.ReservationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/books/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse reserveBook(
            @PathVariable Long bookId,
            Authentication authentication
    ) {
        return reservationService.reserveBook(bookId, authentication);
    }

    @GetMapping("/my")
    public List<ReservationResponse> getMyReservations(Authentication authentication) {
        return reservationService.getReservations(authentication);
    }

    @GetMapping
    public List<ReservationResponse> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @PatchMapping("/{reservationId}/status")
    public ReservationResponse updateReservationStatus(
            @PathVariable Long reservationId,
            @Valid @RequestBody UpdateReservationStatusRequest request
    ) {
        return reservationService.updateReservationStatus(reservationId, request);
    }
}