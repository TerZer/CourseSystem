package lt.terzer.configs;

import lt.terzer.databases.CourseDatabase;
import lt.terzer.databases.FileDatabase;
import lt.terzer.databases.UserDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public UserDatabase userDatabase() {
        return new UserDatabase("localhost:3306", "test", "users", "root", "checkPass123");
    }

    @Bean
    public CourseDatabase courseDatabase() {
        return new CourseDatabase("localhost:3306", "test", "courses", "root", "checkPass123");
    }

    @Bean
    public FileDatabase fileDatabase() {
        return new FileDatabase("localhost:3306", "test", "files", "root", "checkPass123");
    }

}
