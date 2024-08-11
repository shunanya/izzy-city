package com.izzy.service;

import com.izzy.model.Scooter;
import com.izzy.repository.ScooterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ScooterServiceTest {

    private ScooterService scooterService;
    private ScooterRepository scooterRepository;

    @BeforeEach
    void setUp() {
        scooterRepository = mock(ScooterRepository.class);
        scooterService = new ScooterService(scooterRepository, null);
    }

    @Test
    void testGetAllScooters() {
        Scooter scooter1 = new Scooter();
        Scooter scooter2 = new Scooter();
        when(scooterRepository.findAll()).thenReturn(List.of(scooter1, scooter2));
//        when(scooterRepository.findAll()).thenAnswer(invocation -> {
//            System.out.println("findAll called");
//            return List.of(scooter1, scooter2);
//        });
        List<Scooter> scooters = scooterService.getAllScooters();

        assertNotNull(scooters);
        assertEquals(2, scooters.size());
        verify(scooterRepository, times(1)).findAll();
    }

    @Test
    void testGetScooterById() {
        Scooter scooter = new Scooter();
        when(scooterRepository.findById(1L)).thenReturn(Optional.of(scooter));

        Scooter foundScooter = scooterService.getScooterById(1L);

        assertNotNull(foundScooter);
        verify(scooterRepository, times(1)).findById(1L);
    }

    @Test
    void testGetScooterByIdNotFound() {
        when(scooterRepository.findById(1L)).thenReturn(Optional.empty());

        Scooter foundScooter = scooterService.getScooterById(1L);

        assertNull(foundScooter);
        verify(scooterRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateScooter() {
        Scooter scooter = new Scooter();
        when(scooterRepository.save(scooter)).thenReturn(scooter);

        Scooter createdScooter = scooterService.createScooter(scooter);

        assertNotNull(createdScooter);
        verify(scooterRepository, times(1)).save(scooter);
    }

    @Test
    void testUpdateScooter() {
        Scooter existingScooter = new Scooter();
        existingScooter.setId(1L);
        when(scooterRepository.findById(1L)).thenReturn(Optional.of(existingScooter));
        when(scooterRepository.save(existingScooter)).thenReturn(existingScooter);

        Scooter updatedScooter = scooterService.updateScooter(1L, existingScooter);

        assertNotNull(updatedScooter);
        verify(scooterRepository, times(1)).findById(1L);
        verify(scooterRepository, times(1)).save(existingScooter);
    }

    @Test
    void testUpdateScooterNotFound() {
        Scooter scooter = new Scooter();
        when(scooterRepository.findById(1L)).thenReturn(Optional.empty());

        Scooter updatedScooter = scooterService.updateScooter(1L, scooter);

        assertNull(updatedScooter);
        verify(scooterRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteScooter() {
        Scooter scooter = new Scooter();
        when(scooterRepository.findById(1L)).thenReturn(Optional.of(scooter));

        boolean isDeleted = scooterService.deleteScooter(1L);

        assertTrue(isDeleted);
        verify(scooterRepository, times(1)).findById(1L);
        verify(scooterRepository, times(1)).delete(scooter);
    }

    @Test
    void testDeleteScooterNotFound() {
        when(scooterRepository.findById(1L)).thenReturn(Optional.empty());

        boolean isDeleted = scooterService.deleteScooter(1L);

        assertFalse(isDeleted);
        verify(scooterRepository, times(1)).findById(1L);
    }
}