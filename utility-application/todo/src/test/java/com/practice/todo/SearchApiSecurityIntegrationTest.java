package com.practice.todo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(JobRunrTestStorageConfig.class)
class SearchApiSecurityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void searchTasksRequiresAuthentication() throws Exception {
		mockMvc.perform(get("/api/search/tasks")).andExpect(status().isForbidden());
	}

	@Test
	void analyticsDashboardRequiresAuthentication() throws Exception {
		mockMvc.perform(get("/api/analytics/workspaces/00000000-0000-0000-0000-000000000001/dashboard"))
				.andExpect(status().isForbidden());
	}
}
