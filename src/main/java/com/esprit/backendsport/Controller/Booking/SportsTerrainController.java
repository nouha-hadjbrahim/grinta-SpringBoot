package com.esprit.backendsport.Controller.Booking;

import com.esprit.backendsport.Entities.Booking.SportsTerrain;
import com.esprit.backendsport.Repository.Booking.SportsTerrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terrains")
@CrossOrigin("*")
@RequiredArgsConstructor
public class SportsTerrainController {

    private final SportsTerrainRepository terrainRepository;

    @GetMapping
    public List<SportsTerrain> getAll() {
        return terrainRepository.findAll();
    }
}