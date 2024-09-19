package com.izzy.service;

import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.Scooter;
import com.izzy.repository.ScooterRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ScooterServiceTest {

    private ScooterService scooterService;
    @Autowired
    private ScooterRepository scooterRepository;

    private final String identifier = "03367";

    @BeforeEach
    void setUp() {
        scooterRepository = mock(ScooterRepository.class);
        scooterService = new ScooterService(scooterRepository, null);
    }

    @Test
    @Order(1)
    void findScooterByIdentifier() {
        Scooter scooter = new Scooter();
        scooter.setId(1L);
        scooter.setIdentifier(identifier);
        scooter.setSpeedLimit(50);
        scooter.setBatteryLevel(20);
        scooterRepository.save(scooter);

        when(scooterRepository.findScooterByIdentifier(anyString())).thenReturn(Optional.of(scooter));

        Scooter ret_scooter = scooterService.getScooterByIdentifier(identifier);

        assertNotNull(ret_scooter);
    }

    @Test
    @Order(2)
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
    @Order(3)
    void testGetScooterById() {
        Scooter scooter = new Scooter();
        when(scooterRepository.findById(1L)).thenReturn(Optional.of(scooter));

        Scooter foundScooter = scooterService.getScooterById(1L);

        assertNotNull(foundScooter);
        verify(scooterRepository, times(1)).findById(1L);
    }

    @Test
    @Order(4)
    void testGetScooterByIdNotFound() {
        when(scooterRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> scooterService.getScooterById(1L));
        assertEquals("Error: Scooter not found with id: '1'", ex.getMessage());

        verify(scooterRepository, times(1)).findById(1L);
    }

    @Test
    @Order(5)
    void testCreateScooter() {
        Scooter scooter = new Scooter();
        when(scooterRepository.save(scooter)).thenReturn(scooter);

        Scooter createdScooter = scooterService.createScooter(scooter);

        assertNotNull(createdScooter);
        verify(scooterRepository, times(1)).save(scooter);
    }

    @Test
    @Order(6)
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
    @Order(7)
    void testUpdateScooterNotFound() {
        Scooter scooter = new Scooter();
        when(scooterRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> scooterService.updateScooter(1L, scooter));
        assertEquals("Error: Scooter not found with id: '1'", ex.getMessage());

        verify(scooterRepository, times(1)).findById(1L);
    }

    @Test
    @Order(8)
    void testDeleteScooter() {
        Scooter scooter = new Scooter();
        when(scooterRepository.findById(1L)).thenReturn(Optional.of(scooter));

        boolean isDeleted = scooterService.deleteScooter(1L);

        assertTrue(isDeleted);
        verify(scooterRepository, times(1)).findById(1L);
        verify(scooterRepository, times(1)).delete(scooter);
    }

    @Test
    @Order(9)
    void testDeleteScooterNotFound() {
        when(scooterRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> scooterService.deleteScooter(1L));
        assertEquals("Error: Scooter not found with id: '1'", ex.getMessage());

        verify(scooterRepository, times(1)).findById(1L);
    }
}