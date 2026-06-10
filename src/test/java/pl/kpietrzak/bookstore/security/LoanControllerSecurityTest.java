package pl.kpietrzak.bookstore.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import pl.kpietrzak.bookstore.controller.LoanController;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class LoanControllerSecurityTest {

    @Test
    void shouldRestrictCreatingLoanToAdminRole() throws Exception {
        Method method = LoanController.class.getMethod("createLoanFromReservation", Long.class);

        assertThat(method.getAnnotation(PreAuthorize.class).value()).isEqualTo("hasRole('ADMIN')");
    }

    @Test
    void shouldRestrictReturningLoanToAdminRole() throws Exception {
        Method method = LoanController.class.getMethod("returnLoan", Long.class);

        assertThat(method.getAnnotation(PreAuthorize.class).value()).isEqualTo("hasRole('ADMIN')");
    }

    @Test
    void shouldAllowUserAndAdminToReadOwnLoans() throws Exception {
        Method method = LoanController.class.getMethod(
                "getMyLoans",
                org.springframework.security.core.Authentication.class
        );

        assertThat(method.getAnnotation(PreAuthorize.class).value()).isEqualTo("hasAnyRole('USER', 'ADMIN')");
    }

    @Test
    void shouldRestrictAllLoansToAdminRole() throws Exception {
        Method method = LoanController.class.getMethod("getAllLoans");

        assertThat(method.getAnnotation(PreAuthorize.class).value()).isEqualTo("hasRole('ADMIN')");
    }
}
