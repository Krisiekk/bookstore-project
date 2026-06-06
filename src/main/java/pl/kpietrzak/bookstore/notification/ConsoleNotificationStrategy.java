package pl.kpietrzak.bookstore.notification;

import org.springframework.stereotype.Component;
import pl.kpietrzak.bookstore.entity.Reservation;

@Component
public class ConsoleNotificationStrategy implements NotificationStrategy {

    @Override
    public void sendNotificationStrategy(Reservation reservation) {
        System.out.println(
                "Reservation created for user: "
                +reservation.getUser().getUsername()
                +", book: "
                +reservation.getBook().getTitle()
                +", status: "
                +reservation.getStatus()
        );



    }

}
