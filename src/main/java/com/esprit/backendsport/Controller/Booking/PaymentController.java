package com.esprit.backendsport.Controller.Booking;

import com.esprit.backendsport.Services.Booking.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PaymentController {

    private final PaymentService paymentService;

    // ✅ Initier le paiement
    @PostMapping("/initiate/{bookingId}")
    public ResponseEntity<?> initiate(@PathVariable Long bookingId) {
        try {
            return ResponseEntity.ok(paymentService.initiatePayment(bookingId));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Vérifier après retour Flouci
    @GetMapping("/verify/{paymentId}")
    public ResponseEntity<?> verify(@PathVariable String paymentId) {
        try {
            return ResponseEntity.ok(paymentService.verifyPayment(paymentId));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }
}