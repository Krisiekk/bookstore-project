package pl.kpietrzak.bookstore.notification;

import org.junit.jupiter.api.Test;
import pl.kpietrzak.bookstore.entity.Reservation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NotificationServiceTest {

    @Test
    void shouldSendReservationCreatedNotification() {
        NotificationStrategy notificationStrategy = mock(NotificationStrategy.class);
        NotificationService notificationService = new NotificationService(notificationStrategy);

        Reservation reservation = new Reservation();

        notificationService.notifyReservationCreated(reservation);

        verify(notificationStrategy).sendNotificationStrategy(reservation);
    }
}
