package lt.terzer;

import lt.terzer.databases.UserDatabase;
import lt.terzer.sql.DatabaseStatus;
import lt.terzer.user.Company;
import lt.terzer.user.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.stream.Collectors;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTest {

     private UserDatabase userDatabase;

    @BeforeAll
    public void setUp(){
        userDatabase = new UserDatabase("localhost:3306", "test", "users", "root", "checkPass123");
    }

    @BeforeEach
    public void reset() {

    }

    @Test
    public void findResult(){
        assert userDatabase.status() == DatabaseStatus.CONNECTED;

        User user = userDatabase.getByUsername("test");
        user.addAccessibleCourse(2);
        userDatabase.save(user);

        userDatabase.getAll().forEach(System.out::println);

        userDatabase.remove(userDatabase.getAll().stream().filter(u -> u.getUsername().equals("dada")).collect(Collectors.toList()));
    }
}