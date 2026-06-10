package pl.kpietrzak.bookstore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import pl.kpietrzak.bookstore.dto.loan.LoanResponse;
import pl.kpietrzak.bookstore.entity.Book;
import pl.kpietrzak.bookstore.entity.Loan;
import pl.kpietrzak.bookstore.entity.Reservation;
import pl.kpietrzak.bookstore.entity.User;
import pl.kpietrzak.bookstore.enums.LoanStatus;
import pl.kpietrzak.bookstore.enums.ReservationStatus;
import pl.kpietrzak.bookstore.enums.Role;
import pl.kpietrzak.bookstore.exception.BookAlreadyLoanedException;
import pl.kpietrzak.bookstore.exception.LoanNotFoundException;
import pl.kpietrzak.bookstore.exception.ReservationNotFoundException;
import pl.kpietrzak.bookstore.mapper.LoanMapper;
import pl.kpietrzak.bookstore.repository.BookRepository;
import pl.kpietrzak.bookstore.repository.LoanRepository;
import pl.kpietrzak.bookstore.repository.ReservationRepository;
import pl.kpietrzak.bookstore.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class LoanServiceTest {

    private LoanRepository loanRepository;
    private ReservationRepository reservationRepository;
    private UserRepository userRepository;
    private BookRepository bookRepository;
    private LoanService loanService;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        loanRepository = mock(LoanRepository.class);
        reservationRepository = mock(ReservationRepository.class);
        userRepository = mock(UserRepository.class);
        bookRepository = mock(BookRepository.class);
        authentication = mock(Authentication.class);

        loanService = new LoanService(
                loanRepository,
                reservationRepository,
                userRepository,
                bookRepository,
                new LoanMapper()
        );
    }

    @Test
    void shouldCreateLoanFromAcceptedReservation() {
        Reservation reservation = createReservation(ReservationStatus.ACCEPTED);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(loanRepository.existsByBookAndStatus(reservation.getBook(), LoanStatus.ACTIVE)).thenReturn(false);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan loan = invocation.getArgument(0);
            loan.setId(10L);
            return loan;
        });

        LoanResponse response = loanService.createLoanFromReservation(1L);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getReservationId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("user1");
        assertThat(response.getBookTitle()).isEqualTo("Clean Code");
        assertThat(response.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        assertThat(reservation.getBook().isAvailable()).isFalse();

        verify(bookRepository).save(reservation.getBook());
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void shouldThrowExceptionWhenReservationDoesNotExist() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.createLoanFromReservation(999L))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessage("Reservation not found with id: 999");

        verify(loanRepository, never()).save(any(Loan.class));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldRejectLoanWhenReservationIsNotAccepted() {
        Reservation reservation = createReservation(ReservationStatus.PENDING);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> loanService.createLoanFromReservation(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only accepted reservations can be loaned");

        verify(loanRepository, never()).save(any(Loan.class));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldRejectLoanWhenBookIsAlreadyLoaned() {
        Reservation reservation = createReservation(ReservationStatus.ACCEPTED);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(loanRepository.existsByBookAndStatus(reservation.getBook(), LoanStatus.ACTIVE)).thenReturn(true);

        assertThatThrownBy(() -> loanService.createLoanFromReservation(1L))
                .isInstanceOf(BookAlreadyLoanedException.class)
                .hasMessage("Book is already loaned with id: 1");

        verify(loanRepository, never()).save(any(Loan.class));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldReturnLoan() {
        Loan loan = createLoan(LoanStatus.ACTIVE);

        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);

        LoanResponse response = loanService.returnLoan(5L);

        assertThat(response.getStatus()).isEqualTo(LoanStatus.RETURNED);
        assertThat(response.getReturnedAt()).isNotNull();
        assertThat(loan.getBook().isAvailable()).isTrue();

        verify(bookRepository).save(loan.getBook());
        verify(loanRepository).save(loan);
    }

    @Test
    void shouldThrowExceptionWhenLoanDoesNotExist() {
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.returnLoan(999L))
                .isInstanceOf(LoanNotFoundException.class)
                .hasMessage("Loan not found with id: 999");

        verify(bookRepository, never()).save(any(Book.class));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void shouldRejectReturningAlreadyReturnedLoan() {
        Loan loan = createLoan(LoanStatus.RETURNED);

        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> loanService.returnLoan(5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Loan is already returned");

        verify(bookRepository, never()).save(any(Book.class));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void shouldReturnCurrentUserLoans() {
        User user = createUser();
        Loan loan = createLoan(LoanStatus.ACTIVE);

        when(authentication.getName()).thenReturn("user1");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(loanRepository.findByUser(user)).thenReturn(List.of(loan));

        List<LoanResponse> result = loanService.getMyLoans(authentication);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("user1");
        assertThat(result.get(0).getBookTitle()).isEqualTo("Clean Code");

        verify(loanRepository).findByUser(user);
    }

    @Test
    void shouldReturnAllLoans() {
        Loan loan = createLoan(LoanStatus.ACTIVE);

        when(loanRepository.findAll()).thenReturn(List.of(loan));

        List<LoanResponse> result = loanService.getAllLoans();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(LoanStatus.ACTIVE);

        verify(loanRepository).findAll();
    }

    private Loan createLoan(LoanStatus status) {
        Reservation reservation = createReservation(ReservationStatus.ACCEPTED);
        reservation.getBook().setAvailable(status != LoanStatus.ACTIVE);

        Loan loan = new Loan();
        loan.setId(5L);
        loan.setReservation(reservation);
        loan.setUser(reservation.getUser());
        loan.setBook(reservation.getBook());
        loan.setStatus(status);
        return loan;
    }

    private Reservation createReservation(ReservationStatus status) {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(createUser());
        reservation.setBook(createBook());
        reservation.setStatus(status);
        return reservation;
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
