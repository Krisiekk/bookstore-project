package pl.kpietrzak.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kpietrzak.bookstore.entity.Reservation;
import pl.kpietrzak.bookstore.entity.User;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {


    List<Reservation> findByUser(User user);
}
