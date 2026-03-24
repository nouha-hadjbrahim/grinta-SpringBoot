package com.esprit.backendsport.Repository.Booking;

import com.esprit.backendsport.Entities.Booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    // ==================== MÉTHODES EXISTANTES ====================
    
    /**
     * Trouver les réservations par terrain et date
     */
    List<Booking> findBySportsTerrainIdAndDate(Long terrainId, LocalDate date);
    
    /**
     * Trouver les réservations d'un utilisateur
     */
    List<Booking> findByUserId(Long userId);
    
    
    // ==================== NOUVELLES MÉTHODES ADMIN ====================
    
    /**
     * Trouver les réservations par statut
     * @param status - PENDING, CONFIRMED, CANCELLED
     */
    List<Booking> findByStatus(String status);
    
    /**
     * Trouver les réservations pour une date spécifique
     */
    @Query("SELECT b FROM Booking b WHERE b.date = :date")
    List<Booking> findByDate(@Param("date") LocalDate date);
    
    /**
     * Trouver les réservations entre deux dates
     */
    @Query("SELECT b FROM Booking b WHERE b.date BETWEEN :startDate AND :endDate")
    List<Booking> findByDateBetween(@Param("startDate") LocalDate startDate, 
                                     @Param("endDate") LocalDate endDate);
    
    /**
     * Compter les réservations par statut
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status")
    Long countByStatus(@Param("status") String status);
    
    /**
     * Compter les réservations pour une date
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.date = :date")
    Long countByDate(@Param("date") LocalDate date);
    
    /**
     * Récupérer toutes les réservations triées par date décroissante
     */
    @Query("SELECT b FROM Booking b ORDER BY b.date DESC, b.time DESC")
    List<Booking> findAllOrderByDateDesc();
    
    
    // ==================== MÉTHODES STATISTIQUES ====================
    
    /**
     * Compter les réservations par type de terrain
     * @return List de [type, count]
     */
    @Query("SELECT b.sportsTerrain.type, COUNT(b) FROM Booking b " +
           "WHERE b.status = 'CONFIRMED' GROUP BY b.sportsTerrain.type")
    List<Object[]> countBookingsByTerrainType();
    
    /**
     * Trouver les terrains les plus réservés
     * @return List de [terrain name, count] triée par count DESC
     */
    @Query("SELECT b.sportsTerrain.name, COUNT(b) FROM Booking b " +
           "WHERE b.status = 'CONFIRMED' GROUP BY b.sportsTerrain.name " +
           "ORDER BY COUNT(b) DESC")
    List<Object[]> findMostBookedTerrains();
}