package pl.kpietrzak.bookstore.notification;
import pl.kpietrzak.bookstore.entity.Reservation;

public class EmailNotificationStrategy implements NotificationStrategy {

    @Override
    public void sendNotificationStrategy(Reservation reservation) {
        System.out.println(
                "Email notification sent to : "
                +reservation.getUser().getEmail()
                +", about book reservation : "
                +reservation.getBook().getTitle()
        );
    }


}
