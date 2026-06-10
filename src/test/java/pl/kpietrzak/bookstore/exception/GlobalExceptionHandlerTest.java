package pl.kpietrzak.bookstore.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleBookNotFoundException() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleBookNotFound(new BookNotFoundException(1L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(404);
        assertThat(response.getBody().get("error")).isEqualTo("Not Found");
        assertThat(response.getBody().get("message")).isEqualTo("Book not found with id: 1");
    }

    @Test
    void shouldHandleReservationNotFoundException() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleReservationNotFound(new ReservationNotFoundException(2L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(404);
        assertThat(response.getBody().get("error")).isEqualTo("Not Found");
        assertThat(response.getBody().get("message")).isEqualTo("Reservation not found with id: 2");
    }

    @Test
    void shouldHandleLoanNotFoundException() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleLoanNotFound(new LoanNotFoundException(3L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(404);
        assertThat(response.getBody().get("error")).isEqualTo("Not Found");
        assertThat(response.getBody().get("message")).isEqualTo("Loan not found with id: 3");
    }

    @Test
    void shouldHandleBookAlreadyLoanedException() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleBookAlreadyLoaned(new BookAlreadyLoanedException(4L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("error")).isEqualTo("Bad Request");
        assertThat(response.getBody().get("message")).isEqualTo("Book is already loaned with id: 4");
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleIllegalArgument(new IllegalArgumentException("Bad request message"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("error")).isEqualTo("Bad Request");
        assertThat(response.getBody().get("message")).isEqualTo("Bad request message");
    }
}
