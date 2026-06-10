package pl.kpietrzak.bookstore.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.kpietrzak.bookstore.dto.loan.LoanResponse;
import pl.kpietrzak.bookstore.enums.LoanStatus;
import pl.kpietrzak.bookstore.service.LoanService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoanService loanService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LoanController loanController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loanController).build();
    }

    @Test
    void shouldCreateLoanFromReservation() throws Exception {
        LoanResponse response = createLoanResponse(LoanStatus.ACTIVE);

        when(loanService.createLoanFromReservation(1L)).thenReturn(response);

        mockMvc.perform(post("/api/loans/reservations/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.reservationId").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.bookTitle").value("Clean Code"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(loanService).createLoanFromReservation(1L);
    }

    @Test
    void shouldReturnLoan() throws Exception {
        LoanResponse response = createLoanResponse(LoanStatus.RETURNED);

        when(loanService.returnLoan(5L)).thenReturn(response);

        mockMvc.perform(patch("/api/loans/5/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("RETURNED"));

        verify(loanService).returnLoan(5L);
    }

    @Test
    void shouldReturnMyLoans() throws Exception {
        LoanResponse response = createLoanResponse(LoanStatus.ACTIVE);

        when(loanService.getMyLoans(any(Authentication.class))).thenReturn(List.of(response));

        mockMvc.perform(get("/api/loans/my")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].reservationId").value(1))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].bookTitle").value("Clean Code"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(loanService).getMyLoans(any(Authentication.class));
    }

    @Test
    void shouldReturnAllLoans() throws Exception {
        LoanResponse response = createLoanResponse(LoanStatus.ACTIVE);

        when(loanService.getAllLoans()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].reservationId").value(1))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].bookTitle").value("Clean Code"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(loanService).getAllLoans();
    }

    private LoanResponse createLoanResponse(LoanStatus status) {
        return new LoanResponse(
                5L,
                1L,
                1L,
                "user1",
                1L,
                "Clean Code",
                LocalDateTime.of(2026, 6, 10, 10, 0),
                status == LoanStatus.RETURNED ? LocalDateTime.of(2026, 6, 11, 10, 0) : null,
                status
        );
    }
}
