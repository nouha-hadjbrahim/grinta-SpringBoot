package com.esprit.backendsport.Services.Booking;

import com.esprit.backendsport.Entities.Booking.Booking;
import com.esprit.backendsport.Entities.Booking.Payment;
import com.esprit.backendsport.Repository.Booking.BookingRepository;
import com.esprit.backendsport.Repository.Booking.PaymentRepository;
import com.esprit.backendsport.Repository.Booking.SportsTerrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final SportsTerrainRepository terrainRepository;

    public Map<String, Object> initiatePayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

        double amount = booking.getSportsTerrain().getPrice() + 5;

        // ✅ Générer un faux ID de paiement
        String fakePaymentId = "FLOUCI_" + bookingId + "_" + System.currentTimeMillis();

        // Sauvegarder en DB
        Payment payment = Payment.builder()
            .booking(booking)
            .paymentId(fakePaymentId)
            .amount(amount)
            .status("PENDING")
            .createdAt(LocalDateTime.now())
            .build();
        paymentRepository.save(payment);

        // ✅ Retourner l'URL de succès directement
        Map<String, Object> response = new HashMap<>();
        response.put("paymentId", fakePaymentId);
        response.put("paymentUrl",
            "http://localhost:4200/main/bookings/payment-success?payment_id=" + fakePaymentId);
        response.put("amount", amount);
        response.put("bookingId", bookingId);
        return response;
    }

    public Map<String, Object> verifyPayment(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));

        // ✅ Marquer comme SUCCESS
        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        // ✅ Confirmer le booking
        Booking booking = payment.getBooking();
        booking.setStatus("CONFIRMED");

        // ✅ Décrémenter les slots
        var terrain = booking.getSportsTerrain();
        if (terrain.getAvailableSlots() != null && terrain.getAvailableSlots() > 0) {
            terrain.setAvailableSlots(terrain.getAvailableSlots() - 1);
            terrainRepository.save(terrain);
        }
        bookingRepository.save(booking);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", "SUCCESS");
        responseMap.put("bookingId", booking.getId());
        responseMap.put("terrain", booking.getSportsTerrain().getName());
        responseMap.put("date", booking.getDate().toString());
        responseMap.put("time", booking.getTime());
        responseMap.put("amount", payment.getAmount());
        return responseMap;
    }
}