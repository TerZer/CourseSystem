package lt.terzer.controllers;


import lt.terzer.databases.UserDatabase;
import lt.terzer.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserDatabase usersDatabase;

    @Test
    public void no_user() throws Exception {
        mockMvc.perform(post("/login")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void no_user_name() throws Exception {
        mockMvc.perform(post("/login").param("password", "")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void no_user_password() throws Exception {
        mockMvc.perform(post("/login").param("user", "")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void no_data() throws Exception {
        User user = new User("user", "pass");
        when(usersDatabase.getByUsername(user.getUsername())).thenReturn(user);
        mockMvc.perform(post("/login").param("user", "").param("password", "")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void bad_username() throws Exception {
        User user = new User("user", "pass");
        when(usersDatabase.getByUsername(user.getUsername())).thenReturn(user);
        mockMvc.perform(post("/login").param("user", "asd").param("password", "pass")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void bad_password() throws Exception {
        User user = new User("user", "pass");
        when(usersDatabase.getByUsername(user.getUsername())).thenReturn(user);
        mockMvc.perform(post("/login").param("user", "user").param("password", "123")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void correct() throws Exception {
        User user = new User("user", "pass");
        when(usersDatabase.getByUsername(user.getUsername())).thenReturn(user);
        mockMvc.perform(post("/login").param("user", "user").param("password", "pass")).andExpect(MockMvcResultMatchers.status().isOk());
    }

}
