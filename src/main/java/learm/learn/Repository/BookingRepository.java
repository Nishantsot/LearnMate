
package learm.learn.Repository;

import learm.learn.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'BOOKED'")
    long countActiveBookings();

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'COMPLETED'")
    long countCompletedBookings();
}
