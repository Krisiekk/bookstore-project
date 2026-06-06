package pl.kpietrzak.bookstore.service;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;
import pl.kpietrzak.bookstore.dto.reservation.ReservationResponse;
import pl.kpietrzak.bookstore.dto.reservation.UpdateReservationStatusRequest;
import pl.kpietrzak.bookstore.entity.Book;
import pl.kpietrzak.bookstore.entity.Reservation;
import pl.kpietrzak.bookstore.entity.User;
import pl.kpietrzak.bookstore.enums.ReservationStatus;
import pl.kpietrzak.bookstore.exception.BookNotFoundException;
import pl.kpietrzak.bookstore.exception.ReservationNotFoundException;
import pl.kpietrzak.bookstore.mapper.ReservationMapper;
import pl.kpietrzak.bookstore.repository.BookRepository;
import pl.kpietrzak.bookstore.repository.ReservationRepository;
import pl.kpietrzak.bookstore.repository.UserRepository;
import pl.kpietrzak.bookstore.notification.NotificationService;

import java.util.List;

@Service
public class ReservationService {

    private ReservationRepository reservationRepository;
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private ReservationMapper reservationMapper;
    private  NotificationService notificationService;

    public ReservationService(ReservationRepository reservationRepository, BookRepository bookRepository, UserRepository userRepository, NotificationService notificationService,ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.reservationMapper = reservationMapper;
        this.notificationService = notificationService;
    }


  public ReservationResponse reserveBook(Long bookId, Authentication authentication) {
        User user = getCurrentUser(authentication);

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));

        Reservation reservation = new Reservation();

        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus(ReservationStatus.PENDING);

        Reservation savedReservation = reservationRepository.save(reservation);
        notificationService.notifyReservationCreated(savedReservation);

        return reservationMapper.toResponse(savedReservation);

  }
  public List<ReservationResponse> getReservations(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return reservationRepository.findByUser(user).stream()
                .map(reservationMapper::toResponse)
                .toList();
  }

  public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toResponse)
                .toList();
  }

  public ReservationResponse updateReservationStatus(Long reservationId, UpdateReservationStatusRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new ReservationNotFoundException(reservationId));

        reservation.setStatus(request.getStatus());

        Reservation updatedReservation = reservationRepository.save(reservation);
        return reservationMapper.toResponse(updatedReservation);


  }



  private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("Current user not found"));
  }
}
