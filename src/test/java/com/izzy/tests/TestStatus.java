package com.izzy.tests;

import com.izzy.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class TestStatus {

    @Test
    public void checkTestStatus() {
        List<String> statusList = new ArrayList<>() {{
            add("CANCELED");
            add("completed");
            add("kuku");
        }};

        statusList.forEach(s -> System.out.println(Task.Status.getStatusByString(s)));
    }
}
