package com.rent.healthbookmessenger.profile.controller;

import com.rent.healthbookmessenger.profile.dto.Profile;
import com.rent.healthbookmessenger.profile.service.ProfileService;
import com.rent.healthbookmessenger.web.exception.EntityNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/profiles")
@Tag(name = "Профили", description = "Прокси для работы с профилями из главного приложения")
public class ProfileController {

    Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Profile getProfile(@PathVariable UUID id) {
        logger.info("Called getProfile from microservice");
        Profile profile = service.getProfile(id);
        if (profile == null) {
            throw new EntityNotFoundException("Профиль не найден");
        }
        return profile;
    }
}
