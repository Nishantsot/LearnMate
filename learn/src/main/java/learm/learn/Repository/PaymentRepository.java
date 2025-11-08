package learm.learn.Repository;

import learm.learn.Entity.Payment;
import learm.learn.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 🔹 1️⃣ Admin Dashboard – Total Revenue
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'SUCCESS'")
    Double totalRevenue();

    // 🔹 2️⃣ Tutor Dashboard – Total Earnings by Tutor
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.tutor = :tutor AND p.status = 'SUCCESS'")
    Double totalEarningsByTutor(@Param("tutor") User tutor);

    // 🔹 3️⃣ Tutor Dashboard – Recent Payments
    @Query("SELECT p FROM Payment p WHERE p.tutor = :tutor ORDER BY p.paymentDate DESC")
    List<Payment> findRecentPaymentsByTutor(@Param("tutor") User tutor);

    // 🔹 4️⃣ Admin Dashboard – Revenue over Time (e.g., for charts)
    @Query("SELECT MONTH(p.paymentDate), SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS' GROUP BY MONTH(p.paymentDate) ORDER BY MONTH(p.paymentDate)")
    List<Object[]> monthlyRevenue();

    // 🔹 5️⃣ Admin Dashboard – Top Tutors by Earnings
    @Query("SELECT p.tutor.name, SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS' GROUP BY p.tutor.name ORDER BY SUM(p.amount) DESC")
    List<Object[]> topTutorsByEarnings();

    // 🔹 6️⃣ Admin – Payments between dates (optional filter for reports)
    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :start AND :end")
    List<Payment> findPaymentsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
