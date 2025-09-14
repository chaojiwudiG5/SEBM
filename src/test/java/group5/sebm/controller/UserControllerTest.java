package group5.sebm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group5.sebm.controller.vo.UserVo;
import group5.sebm.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // 模拟 HTTP 请求

    @MockBean
    private UserService userService; // Mock Service 层

    @Autowired
    private ObjectMapper objectMapper; // 用于序列化/反序列化 JSON

    @Test
    public void testGetAllUsers() throws Exception {
        List<UserVo> mockUsers = Arrays.asList(
                new UserVo(1, "Alice", 25),
                new UserVo(2, "Bob", 30)
        );

        // 当 Service 层调用 getAllUsers() 返回 mockUsers
        when(userService.getAllUsers()).thenReturn(mockUsers);

        // 发送 GET 请求 /users 并验证返回值
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Alice"))
                .andExpect(jsonPath("$[1].age").value(30));
    }

    @Test
    public void testGetUserById() throws Exception {
        UserVo mockUser = new UserVo(1, "Alice", 25);

        // 当 Service 层调用 getDiscountUserById(1) 返回 mockUser
        when(userService.getDiscountUserById(1)).thenReturn(mockUser);
        String requestJson = objectMapper.writeValueAsString(new UserVo(1, null, null));

        // 发送 POST 请求 /id 并验证返回值
        mockMvc.perform(post("/id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Alice"))
                .andExpect(jsonPath("$.age").value(25));
    }
}
