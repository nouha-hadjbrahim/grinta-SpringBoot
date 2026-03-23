package com.esprit.backendsport.Repository.Booking;

import com.esprit.backendsport.Entities.Booking.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);
    Optional<Payment> findByPaymentId(String paymentId);
}