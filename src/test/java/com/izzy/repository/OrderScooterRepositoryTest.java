package com.izzy.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.izzy.model.Order;
import com.izzy.model.OrderScooter;
import com.izzy.model.OrderScooterId;
import com.izzy.model.Scooter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class OrderScooterRepositoryTest {

    @Autowired
    private OrderScooterRepository orderScooterRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Create and configure ObjectMapper
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    @Test
    void findOrderScootersByUserId() throws JsonProcessingException {
        List<Object[]> orderScooterList = orderScooterRepository.findByUserId(6L);

        assertNotNull(orderScooterList);
        assertFalse(orderScooterList.isEmpty());

        String osJson = objectMapper.writeValueAsString(orderScooterList);
        System.out.println(osJson);

        List<OrderScooter> oss = orderScooterList.stream().map(obj->{
            OrderScooter os = new OrderScooter(objectMapper.convertValue(obj[0], Order.class), objectMapper.convertValue(obj[1], Scooter.class), (Integer)obj[2]);
            os.setId(new OrderScooterId(os.getOrder().getId(), os.getScooter().getId()));
            return os;
        }).collect(Collectors.toList());

        // Print the JSON string
        osJson = objectMapper.writeValueAsString(oss);
        System.out.println(osJson);


     }
}