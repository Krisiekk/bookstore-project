package pl.kpietrzak.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.kpietrzak.bookstore.dto.reservation.ReservationResponse;
import pl.kpietrzak.bookstore.dto.reservation.UpdateReservationStatusRequest;
import pl.kpietrzak.bookstore.enums.ReservationStatus;
import pl.kpietrzak.bookstore.service.ReservationService;

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
class ReservationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ReservationService reservationService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void shouldReserveBook() throws Exception {
        ReservationResponse response = createReservationResponse(1L, ReservationStatus.PENDING);

        when(reservationService.reserveBook(eq(1L), any(Authentication.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/reservations/books/1")
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.bookTitle").value("Clean Code"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(reservationService).reserveBook(eq(1L), any(Authentication.class));
    }

    @Test
    void shouldReturnMyReservations() throws Exception {
        ReservationResponse response = createReservationResponse(1L, ReservationStatus.PENDING);

        when(reservationService.getReservations(any(Authentication.class)))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/reservations/my")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].bookId").value(1))
                .andExpect(jsonPath("$[0].bookTitle").value("Clean Code"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(reservationService).getReservations(any(Authentication.class));
    }

    @Test
    void shouldReturnAllReservations() throws Exception {
        ReservationResponse response = createReservationResponse(1L, ReservationStatus.PENDING);

        when(reservationService.getAllReservations())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].bookId").value(1))
                .andExpect(jsonPath("$[0].bookTitle").value("Clean Code"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(reservationService).getAllReservations();
    }

    @Test
    void shouldUpdateReservationStatus() throws Exception {
        UpdateReservationStatusRequest request = new UpdateReservationStatusRequest();
        request.setStatus(ReservationStatus.ACCEPTED);

        ReservationResponse response = createReservationResponse(1L, ReservationStatus.ACCEPTED);

        when(reservationService.updateReservationStatus(eq(1L), any(UpdateReservationStatusRequest.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/reservations/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        verify(reservationService).updateReservationStatus(eq(1L), any(UpdateReservationStatusRequest.class));
    }

    private ReservationResponse createReservationResponse(Long id, ReservationStatus status) {
        return new ReservationResponse(
                id,
                1L,
                "user1",
                1L,
                "Clean Code",
                status,
                LocalDateTime.of(2026, 6, 7, 10, 0)
        );
    }
}