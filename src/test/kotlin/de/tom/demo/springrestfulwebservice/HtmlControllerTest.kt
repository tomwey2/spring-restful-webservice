package de.tom.demo.springrestfulwebservice

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

/**
 * Testing with a mocked environment.
 * By default, @SpringBootTest does not start the server but instead sets up a mock environment
 * for testing web endpoints.
 * With Spring MVC, we can query our web endpoints using MockMvc.
 * Testing within a mocked environment is usually faster than running with a full servlet container.
 */
@SpringBootTest
@AutoConfigureMockMvc
class HtmlControllerMockedTest {

    @Test
    fun testWithMockMvc(@Autowired mvc: MockMvc) {
        mvc.perform(MockMvcRequestBuilders.get("/hello"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("<h1>Hello</h1>"))
    }

}

/**
 * Testing with a running server.
 * Starts a full running server at a random port.
 * Using Spring Boot's TestRestTemplate.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HtmlControllerIntegrationTest(@Autowired val client: TestRestTemplate) {

    @Test
    fun `Assert blog page title, content and status code`() {
        val entity = client.getForEntity<String>("/hello")
        Assertions.assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(entity.body).contains("<h1>Hello</h1>")
    }
}

