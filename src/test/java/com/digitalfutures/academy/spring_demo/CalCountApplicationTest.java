package com.digitalfutures.academy.spring_demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

// Loads the Spring application context
@SpringBootTest
@AutoConfigureMockMvc
class CalCountApplicationTest {

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void clearCollection() {
		TestMongoConfig.clearCollection();
	}

	@Nested
	@DisplayName("GET user tests")
	class GetAll {
//		@Test
//		@DisplayName("Should return status code of okay when all the users are found")
//		void shouldReturnOkayHttpStatusCodeWhenAllUsersAreFound() throws Exception {
//			mockMvc.perform(get("/api/users"))
//					.andExpect(status().isOk());
//		}
//
//		@Test
//		@DisplayName("Should return JSON - regardless of how many todos are found")
//		void shouldReturnJSON() throws Exception {
//			mockMvc.perform(get("/api/users"))
//					.andExpect(content().contentType(MediaType.APPLICATION_JSON));
//		}
	}
}
