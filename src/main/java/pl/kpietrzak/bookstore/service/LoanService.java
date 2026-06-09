package pl.kpietrzak.bookstore.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kpietrzak.bookstore.dto.loan.LoanResponse;
import pl.kpietrzak.bookstore.entity.Book;
import pl.kpietrzak.bookstore.entity.Loan;
import pl.kpietrzak.bookstore.entity.Reservation;
import pl.kpietrzak.bookstore.entity.User;
import pl.kpietrzak.bookstore.enums.LoanStatus;
import pl.kpietrzak.bookstore.enums.ReservationStatus;
import pl.kpietrzak.bookstore.exception.BookAlreadyLoanedException;
import pl.kpietrzak.bookstore.exception.LoanNotFoundException;
import pl.kpietrzak.bookstore.exception.ReservationNotFoundException;
import pl.kpietrzak.bookstore.mapper.LoanMapper;
import pl.kpietrzak.bookstore.repository.BookRepository;
import pl.kpietrzak.bookstore.repository.LoanRepository;
import pl.kpietrzak.bookstore.repository.ReservationRepository;
import pl.kpietrzak.bookstore.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanMapper loanMapper;

    public LoanService(
            LoanRepository loanRepository,
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            BookRepository bookRepository,
            LoanMapper loanMapper
    ) {
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.loanMapper = loanMapper;
    }

    @Transactional
    public LoanResponse createLoanFromReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        if (reservation.getStatus() != ReservationStatus.ACCEPTED) {
            throw new IllegalArgumentException("Only accepted reservations can be loaned");
        }

        Book book = reservation.getBook();
        if (loanRepository.existsByBookAndStatus(book, LoanStatus.ACTIVE)) {
            throw new BookAlreadyLoanedException(book.getId());
        }

        book.setAvailable(false);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setReservation(reservation);
        loan.setUser(reservation.getUser());
        loan.setBook(book);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setBorrowedAt(LocalDateTime.now());

        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Transactional
    public LoanResponse returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new IllegalArgumentException("Loan is already returned");
        }

        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnedAt(LocalDateTime.now());

        Book book = loan.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        return loanMapper.toResponse(loanRepository.save(loan));
    }

    public List<LoanResponse> getMyLoans(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return loanRepository.findByUser(user).stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("Current user not found"));
    }
}
