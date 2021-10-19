package lt.terzer;

import lt.terzer.courses.Course;
import lt.terzer.databases.CourseDatabase;
import lt.terzer.sql.DatabaseStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CourseTest {

     private CourseDatabase courseDatabase;

    @BeforeAll
    public void setUp(){
        courseDatabase = new CourseDatabase("localhost:3306", "test", "courses", "root", "checkPass123");
    }

    @BeforeEach
    public void reset() {

    }

    @Test
    public void findResult(){
        assert courseDatabase.status() == DatabaseStatus.CONNECTED;

        Optional<Course> course = courseDatabase.getByIds(Arrays.asList(1,2)).stream().findFirst();
        course.ifPresent(c -> courseDatabase.remove(c));
    }
}