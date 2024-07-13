package dev.tpcoder.goutbackend.user;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.tpcoder.goutbackend.user.dto.UserCreationDto;
import dev.tpcoder.goutbackend.user.dto.UserInfoDto;
import dev.tpcoder.goutbackend.user.dto.UserUpdateDto;
import dev.tpcoder.goutbackend.user.model.User;
import dev.tpcoder.goutbackend.user.service.UserService;

@WebMvcTest(UserController.class)
class UserControllerTest {

        @Autowired
        private WebApplicationContext webApplicationContext;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private UserService userService;

        private MockMvc mockMvc;

        @BeforeEach
        public void setup() {
                mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }

        @Test
        void whenGetPageUserThenSuccessful() throws Exception {
                var mockUser = new User(1, "Test", "Test1", "0800000001");
                List<User> users = List.of(mockUser);
                Page<User> pageUsers = new PageImpl<>(users);
                when(userService.getUsersByFirstName(anyString(), any(Pageable.class)))
                                .thenReturn(pageUsers);
                mockMvc.perform(
                                get("/api/v1/users?keyword=est&page=0&size=2&sortField=id&sortDirection=asc")
                                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        void whenGetUserByIdThenSuccessful() throws Exception {
                var mockUser = new UserInfoDto(1, "Test", "Test", "0800000001");
                when(userService.getUserDtoById(anyInt()))
                                .thenReturn(mockUser);
                mockMvc.perform(
                                get(String.format("/api/v1/users/%d", 1))
                                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.firstName").value("Test"))
                                .andExpect(jsonPath("$.lastName").value("Test"))
                                .andExpect(jsonPath("$.phoneNumber").value("0800000001"));
        }

        @Test
        void whenCreateUserThenSuccessful() throws Exception {
                var mockUser = new UserInfoDto(1, "Test", "Test", "0800000001");
                when(userService.createUser(any(UserCreationDto.class)))
                                .thenReturn(mockUser);
                var body = new UserCreationDto(
                                "Test",
                                "Test",
                                "0800000001",
                                "test@test.com",
                                "123456789");
                mockMvc.perform(
                                post("/api/v1/users")
                                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                .content(objectMapper.writeValueAsString(body)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.firstName").value("Test"))
                                .andExpect(jsonPath("$.lastName").value("Test"))
                                .andExpect(jsonPath("$.phoneNumber").value("0800000001"));
        }

        @Test
        void whenUpdateUserThenSuccessful() throws Exception {
                var mockUser = new UserInfoDto(1, "Test", "Test1", "0800000001");
                when(userService.updateUser(anyInt(), any(UserUpdateDto.class)))
                                .thenReturn(mockUser);
                var body = new UserUpdateDto(
                                "Test",
                                "Test1");
                mockMvc.perform(
                                patch(String.format("/api/v1/users/%d", 1))
                                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                .content(objectMapper.writeValueAsString(body)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.firstName").value("Test"))
                                .andExpect(jsonPath("$.lastName").value("Test1"))
                                .andExpect(jsonPath("$.phoneNumber").value("0800000001"));
        }

        @Test
        void whenDeleteUserByIdThenSuccessful() throws Exception {
                when(userService.deleteUserById(anyInt()))
                                .thenReturn(true);
                mockMvc.perform(
                                delete(String.format("/api/v1/users/%d", 1))
                                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(status().isOk());
        }
}
