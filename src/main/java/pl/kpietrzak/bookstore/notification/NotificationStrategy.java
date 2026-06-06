package pl.kpietrzak.bookstore.notification;
import pl.kpietrzak.bookstore.entity.Reservation;

public interface NotificationStrategy {
    void sendNotificationStrategy(Reservation reservation);

}
