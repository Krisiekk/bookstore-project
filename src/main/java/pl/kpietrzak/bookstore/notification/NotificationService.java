package pl.kpietrzak.bookstore.notification;

import org.springframework.stereotype.Service;
import pl.kpietrzak.bookstore.entity.Reservation;
import pl.kpietrzak.bookstore.repository.UserRepository;

import javax.management.Notification;

/**
 * Service that sends reservation notifications using the configured notification strategy.
 */
@Service
public class NotificationService {

    private  final NotificationStrategy notificationStrategy;

    public NotificationService(NotificationStrategy notificationStrategy) {
        this.notificationStrategy = notificationStrategy;
    }

    public void notifyReservationCreated(Reservation reservation){

        notificationStrategy.sendNotificationStrategy(reservation);
    }
}
