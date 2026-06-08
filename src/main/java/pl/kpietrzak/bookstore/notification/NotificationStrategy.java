package pl.kpietrzak.bookstore.notification;
import pl.kpietrzak.bookstore.entity.Reservation;

/**
 * Strategy interface for sending reservation notifications.
 */
public interface NotificationStrategy {
    void sendNotificationStrategy(Reservation reservation);

}
