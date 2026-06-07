package pl.kpietrzak.bookstore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import pl.kpietrzak.bookstore.dto.reservation.ReservationResponse;
import pl.kpietrzak.bookstore.dto.reservation.UpdateReservationStatusRequest;
import pl.kpietrzak.bookstore.entity.Book;
import pl.kpietrzak.bookstore.entity.Reservation;
import pl.kpietrzak.bookstore.entity.User;
import pl.kpietrzak.bookstore.enums.ReservationStatus;
import pl.kpietrzak.bookstore.enums.Role;
import pl.kpietrzak.bookstore.mapper.ReservationMapper;
import pl.kpietrzak.bookstore.notification.NotificationService;
import pl.kpietrzak.bookstore.repository.BookRepository;
import pl.kpietrzak.bookstore.repository.ReservationRepository;
import pl.kpietrzak.bookstore.repository.UserRepository;
import pl.kpietrzak.bookstore.exception.BookNotFoundException;
import pl.kpietrzak.bookstore.exception.ReservationNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationRepository reservationRepository;
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private ReservationMapper reservationMapper;
    private NotificationService notificationService;
    private ReservationService reservationService;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        bookRepository = mock(BookRepository.class);
        userRepository = mock(UserRepository.class);
        reservationMapper = new ReservationMapper();
        notificationService = mock(NotificationService.class);
        authentication = mock(Authentication.class);

        reservationService = new ReservationService(
                reservationRepository,
                bookRepository,
                userRepository,
                notificationService,
                reservationMapper
        );
    }

    @Test
    void shouldReserveBook() {
        User user = createUser();
        Book book = createBook();

        Reservation savedReservation = new Reservation();
        savedReservation.setId(1L);
        savedReservation.setUser(user);
        savedReservation.setBook(book);
        savedReservation.setStatus(ReservationStatus.PENDING);

        when(authentication.getName()).thenReturn("user1");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        ReservationResponse response = reservationService.reserveBook(1L, authentication);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("user1");
        assertThat(response.getBookId()).isEqualTo(1L);
        assertThat(response.getBookTitle()).isEqualTo("Clean Code");
        assertThat(response.getStatus()).isEqualTo(ReservationStatus.PENDING);

        verify(reservationRepository).save(any(Reservation.class));
        verify(notificationService).notifyReservationCreated(savedReservation);
    }

    @Test
    void shouldThrowExceptionWhenBookDoesNotExist() {
        User user = createUser();

        when(authentication.getName()).thenReturn("user1");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.reserveBook(999L, authentication))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found with id: 999");

        verify(reservationRepository, never()).save(any(Reservation.class));
        verify(notificationService, never()).notifyReservationCreated(any(Reservation.class));
    }

    @Test
    void shouldReturnCurrentUserReservations() {
        User user = createUser();
        Book book = createBook();

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus(ReservationStatus.PENDING);

        when(authentication.getName()).thenReturn("user1");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(reservationRepository.findByUser(user)).thenReturn(List.of(reservation));

        List<ReservationResponse> result = reservationService.getReservations(authentication);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("user1");
        assertThat(result.get(0).getBookTitle()).isEqualTo("Clean Code");

        verify(reservationRepository).findByUser(user);
    }

    @Test
    void shouldReturnAllReservations() {
        User user = createUser();
        Book book = createBook();

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus(ReservationStatus.PENDING);

        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        List<ReservationResponse> result = reservationService.getAllReservations();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getStatus()).isEqualTo(ReservationStatus.PENDING);

        verify(reservationRepository).findAll();
    }

    @Test
    void shouldUpdateReservationStatus() {
        User user = createUser();
        Book book = createBook();

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus(ReservationStatus.PENDING);

        UpdateReservationStatusRequest request = new UpdateReservationStatusRequest();
        request.setStatus(ReservationStatus.ACCEPTED);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        ReservationResponse response = reservationService.updateReservationStatus(1L, request);

        assertThat(response.getStatus()).isEqualTo(ReservationStatus.ACCEPTED);

        verify(reservationRepository).findById(1L);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void shouldThrowExceptionWhenReservationDoesNotExist() {
        UpdateReservationStatusRequest request = new UpdateReservationStatusRequest();
        request.setStatus(ReservationStatus.ACCEPTED);

        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.updateReservationStatus(999L, request))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessage("Reservation not found with id: 999");

        verify(reservationRepository).findById(999L);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setEmail("user1@test.pl");
        user.setPassword("password");
        user.setRole(Role.USER);
        return user;
    }

    private Book createBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setIsbn("9780132350884");
        book.setDescription("Book about clean code");
        book.setAvailable(true);
        return book;
    }
}
