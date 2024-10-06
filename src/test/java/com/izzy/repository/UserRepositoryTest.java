package com.izzy.repository;

import com.izzy.model.Role;
import com.izzy.model.User;
import com.izzy.model.Zone;
import com.izzy.security.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ZoneRepository zoneRepository;
    @Autowired
    private RoleRepository roleRepository;

    private final String zoneName = "zone_test";
    private final String roleName = "Admin";
    private final String firstName = "user_test";
    private final String phoneNumber = "111111";

    @BeforeEach
    void setUp() {
        Zone zone = zoneRepository.findByName(zoneName).orElse(zoneRepository.save(new Zone(1L,zoneName)));
        Role role = roleRepository.findRoleByName(roleName).orElseThrow(()->new RuntimeException("Role not found"));

        User user = new User(1L, firstName, phoneNumber, "Male", zone, "day", List.of(role));
//        user.setDateOfBirth(LocalDate.now().minusMonths(1));
        user = userRepository.save(user);
        System.out.println(user);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2023..","-2025",""})
    public void testFindUsersByFilteringWithZone(String input) {
        List<Timestamp> range = Utils.parseDateRangeToPairOfTimestamps(input);
        LocalDate dob = LocalDate.of(2024, 3, 28);
        List<User> users = userRepository.findUsersByFiltering(firstName, null, phoneNumber, null, dob, LocalDate.now(), "day", range.get(0), range.get(1), zoneName);

        assertNotNull(users, "users shouldn't be null");
        assertFalse(users.isEmpty(), "users should not be empty");
    }

    @Test
    public void testFindUsersByFilteringWithoutZone() {
        List<User> users = userRepository.findUsersByFiltering(null, null, phoneNumber, null, null, null, "day", null, null, null);

        assertNotNull(users, "users shouldn't be null");
        assertFalse(users.isEmpty(), "users should not be empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {"2023.."})
    public void testFindUsersByCreatedAt(String input) {
        List<Timestamp> range = Utils.parseDateRangeToPairOfTimestamps(input);
        List<User> users = userRepository.findUsersCreatedBetween(range.get(0), range.get(1));

        assertNotNull(users, "users shouldn't be null");
        assertFalse(users.isEmpty(), "users should not be empty");
    }
}