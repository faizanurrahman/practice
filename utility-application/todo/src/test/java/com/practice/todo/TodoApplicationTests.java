package com.practice.todo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(JobRunrTestStorageConfig.class)
class TodoApplicationTests {

	@Test
	void contextLoads() {}
}
