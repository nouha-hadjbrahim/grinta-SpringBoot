package com.esprit.backendsport.Services.Booking;

import com.esprit.backendsport.DTOs.Booking.AdminBookingDTO;
import com.esprit.backendsport.DTOs.Booking.DashboardStatsDTO;
import com.esprit.backendsport.Entities.Booking.Booking;
import com.esprit.backendsport.Repository.Booking.BookingRepository;
import com.esprit.backendsport.Repository.Booking.SportsTerrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminBookingService {

    private final BookingRepository bookingRepository;
    private final SportsTerrainRepository terrainRepository;

    // ==================== ADMIN BOOKING MANAGEMENT ====================
    
    /**
     * Récupérer toutes les réservations avec infos enrichies
     */
    public List<AdminBookingDTO> getAllBookingsAdmin() {
        return bookingRepository.findAllOrderByDateDesc()
            .stream()
            .map(this::mapToAdminDTO)
            .collect(Collectors.toList());
    }

    /**
     * Filtrer les réservations par statut
     */
    public List<AdminBookingDTO> getBookingsByStatus(String status) {
        return bookingRepository.findByStatus(status)
            .stream()
            .map(this::mapToAdminDTO)
            .collect(Collectors.toList());
    }

    /**
     * Filtrer par date
     */
    public List<AdminBookingDTO> getBookingsByDate(LocalDate date) {
        return bookingRepository.findByDate(date)
            .stream()
            .map(this::mapToAdminDTO)
            .collect(Collectors.toList());
    }

    /**
     * APPROUVER une réservation PENDING
     */
    @Transactional
    public AdminBookingDTO approveBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));
        
        if (!"PENDING".equals(booking.getStatus())) {
            throw new RuntimeException("Only PENDING bookings can be approved");
        }
        
        // Vérifier places disponibles
        var terrain = booking.getSportsTerrain();
        if (terrain.getAvailableSlots() != null && terrain.getAvailableSlots() <= 0) {
            throw new RuntimeException("NO_SLOTS_AVAILABLE");
        }
        
        // ✅ Passer à CONFIRMED
        booking.setStatus("CONFIRMED");
        
        // ✅ Diminuer les slots disponibles
        if (terrain.getAvailableSlots() != null) {
            terrain.setAvailableSlots(terrain.getAvailableSlots() - 1);
            terrainRepository.save(terrain);
        }
        
        Booking saved = bookingRepository.save(booking);
        return mapToAdminDTO(saved);
    }

    /**
     * REJETER une réservation PENDING
     */
    @Transactional
    public AdminBookingDTO rejectBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));
        
        if (!"PENDING".equals(booking.getStatus())) {
            throw new RuntimeException("Only PENDING bookings can be rejected");
        }
        
        // ✅ Passer à CANCELLED
        booking.setStatus("CANCELLED");
        
        // ⚠️ PAS besoin de remettre un slot car PENDING n'avait pas consommé de slot
        
        Booking saved = bookingRepository.save(booking);
        return mapToAdminDTO(saved);
    }

    /**
     * ANNULER une réservation CONFIRMED (admin force cancel)
     */
    @Transactional
    public AdminBookingDTO cancelConfirmedBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));
        
        String previousStatus = booking.getStatus();
        booking.setStatus("CANCELLED");
        
        // ✅ Si c'était CONFIRMED, remettre un slot
        if ("CONFIRMED".equals(previousStatus)) {
            var terrain = booking.getSportsTerrain();
            if (terrain.getAvailableSlots() != null 
                && terrain.getAvailableSlots() < terrain.getCapacity()) {
                terrain.setAvailableSlots(terrain.getAvailableSlots() + 1);
                terrainRepository.save(terrain);
            }
        }
        
        Booking saved = bookingRepository.save(booking);
        return mapToAdminDTO(saved);
    }

    /**
     * MODIFIER une réservation (admin peut tout changer)
     */
    @Transactional
    public AdminBookingDTO updateBookingAdmin(Long bookingId, Booking updatedData) {
        Booking existing = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));
        
        // Vérifier conflit de réservation
        boolean conflict = bookingRepository
            .findBySportsTerrainIdAndDate(
                updatedData.getSportsTerrain().getId(), 
                updatedData.getDate())
            .stream()
            .anyMatch(b -> 
                !b.getId().equals(bookingId) 
                && b.getTime().equals(updatedData.getTime()) 
                && !"CANCELLED".equals(b.getStatus()));
        
        if (conflict) {
            throw new RuntimeException("ALREADY_BOOKED");
        }
        
        existing.setDate(updatedData.getDate());
        existing.setTime(updatedData.getTime());
        existing.setSportsTerrain(updatedData.getSportsTerrain());
        
        Booking saved = bookingRepository.save(existing);
        return mapToAdminDTO(saved);
    }

    /**
     * SUPPRIMER définitivement une réservation
     */
    @Transactional
    public void deleteBookingAdmin(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));
        
        // Si elle était CONFIRMED, remettre un slot
        if ("CONFIRMED".equals(booking.getStatus())) {
            var terrain = booking.getSportsTerrain();
            if (terrain.getAvailableSlots() != null 
                && terrain.getAvailableSlots() < terrain.getCapacity()) {
                terrain.setAvailableSlots(terrain.getAvailableSlots() + 1);
                terrainRepository.save(terrain);
            }
        }
        
        bookingRepository.deleteById(bookingId);
    }

    // ==================== DASHBOARD STATISTICS ====================
    
    /**
     * Statistiques pour le dashboard admin
     */
    public DashboardStatsDTO getDashboardStats() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        
        // 📊 Cards stats
        Long totalToday = bookingRepository.countByDate(today);
        Long pending = bookingRepository.countByStatus("PENDING");
        Double revenue = calculateRevenueForPeriod(startOfMonth, today);
        String mostBooked = getMostBookedTerrain();
        Double occupancy = calculateOccupancyRate();
        
        // 📈 Charts data
        Map<String, Long> byDay = getBookingsLast7Days();
        Map<String, Long> byTerrain = getBookingsByTerrainType();
        Map<String, Double> byMonth = getRevenueLast6Months();
        Map<String, Long> byStatus = getBookingsByStatus();
        
        return DashboardStatsDTO.builder()
            .totalBookingsToday(totalToday)
            .pendingApprovals(pending)
            .revenueThisMonth(revenue)
            .mostBookedTerrain(mostBooked)
            .occupancyRate(occupancy)
            .bookingsByDay(byDay)
            .bookingsByTerrain(byTerrain)
            .revenueByMonth(byMonth)
            .bookingsByStatus(byStatus)
            .build();
    }

    // ==================== HELPER METHODS ====================
    
    /**
     * Mapper Booking vers AdminBookingDTO
     */
    private AdminBookingDTO mapToAdminDTO(Booking b) {
        String userName = getUserFullName(b);
        
        return AdminBookingDTO.builder()
            .id(b.getId())
            .date(b.getDate())
            .time(b.getTime())
            .status(b.getStatus())
            .userId(b.getUser().getId())
            .userName(userName)
            .userEmail(b.getUser().getEmail())
            .terrainId(b.getSportsTerrain().getId())
            .terrainName(b.getSportsTerrain().getName())
            .terrainType(b.getSportsTerrain().getType())
            .terrainLocation(b.getSportsTerrain().getLocation())
            .price(b.getSportsTerrain().getPrice())
            .bookingReference("BK-" + b.getId())
            .build();
    }
    
    /**
     * Construire le nom complet de l'utilisateur
     */
    private String getUserFullName(Booking booking) {
        var user = booking.getUser();
        // ✅ User a un champ "name" unique
        return user.getName() != null ? user.getName() : user.getEmail();
    }

    private Double calculateRevenueForPeriod(LocalDate start, LocalDate end) {
        List<Booking> bookings = bookingRepository.findByDateBetween(start, end);
        return bookings.stream()
            .filter(b -> "CONFIRMED".equals(b.getStatus()))
            .mapToDouble(b -> b.getSportsTerrain().getPrice() + 5) // prix + frais
            .sum();
    }

    private String getMostBookedTerrain() {
        List<Object[]> results = bookingRepository.findMostBookedTerrains();
        if (results.isEmpty()) return "N/A";
        return (String) results.get(0)[0];
    }

    private Double calculateOccupancyRate() {
        long totalSlots = terrainRepository.findAll().stream()
            .mapToLong(t -> t.getCapacity() != null ? t.getCapacity() : 0)
            .sum();
        
        long usedSlots = terrainRepository.findAll().stream()
            .mapToLong(t -> {
                int available = t.getAvailableSlots() != null ? t.getAvailableSlots() : t.getCapacity();
                return t.getCapacity() - available;
            })
            .sum();
        
        if (totalSlots == 0) return 0.0;
        return (usedSlots * 100.0) / totalSlots;
    }

    private Map<String, Long> getBookingsLast7Days() {
        Map<String, Long> result = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dayName = date.getDayOfWeek()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            Long count = bookingRepository.countByDate(date);
            result.put(dayName, count);
        }
        return result;
    }

    private Map<String, Long> getBookingsByTerrainType() {
        List<Object[]> results = bookingRepository.countBookingsByTerrainType();
        return results.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1],
                (a, b) -> a,
                LinkedHashMap::new
            ));
    }

    private Map<String, Double> getRevenueLast6Months() {
        Map<String, Double> result = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = today.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
            String monthName = monthStart.getMonth()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            
            Double revenue = calculateRevenueForPeriod(monthStart, monthEnd);
            result.put(monthName, revenue);
        }
        return result;
    }

    private Map<String, Long> getBookingsByStatus() {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("PENDING", bookingRepository.countByStatus("PENDING"));
        result.put("CONFIRMED", bookingRepository.countByStatus("CONFIRMED"));
        result.put("CANCELLED", bookingRepository.countByStatus("CANCELLED"));
        return result;
    }
}