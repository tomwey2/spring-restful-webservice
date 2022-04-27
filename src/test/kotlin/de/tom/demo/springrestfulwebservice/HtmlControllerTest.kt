package de.tom.demo.springrestfulwebservice

import de.tom.demo.springrestfulwebservice.entities.users.UserController
import de.tom.demo.springrestfulwebservice.entities.users.UserRepository
import de.tom.demo.springrestfulwebservice.entities.users.UserService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

/**
 * Testing web endpoints using MockMvc.
 * Testing within a mocked environment is usually faster than running with a full servlet container.
 * By default, @SpringBootTest does not start the server but instead sets up a mock environment
 * for testing web endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
class HtmlControllerMockedTest(@Autowired val mockMvc: MockMvc) {

    @Test
    fun testWithMockMvc() {
        mockMvc.perform(MockMvcRequestBuilders.get("/hello"))
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
class HtmlControllerIntegrationTest(@Autowired val client: TestRestTemplate, @LocalServerPort val port: Int) {

    @Test
    fun `Assert blog page title, content and status code`() {
        val url = "http://localhost:${port}/hello"
        val entity = client.getForEntity<String>(url)
        Assertions.assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(entity.body).contains("<h1>Hello</h1>")
    }
}

/**
 * Testing only the web layer
 * Spring Boot instantiates only the web layer rather than the whole context.
 */
@WebMvcTest(controllers = [HtmlController::class], useDefaultFilters = false,
    includeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [HtmlController::class])]
)
class HtmlControllerWebLayerTest(@Autowired val mockMvc: MockMvc) {

    @Test
    fun testWithMockMvc() {
        mockMvc.perform(MockMvcRequestBuilders.get("/hello"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("<h1>Hello</h1>"))
    }

}