package lt.terzer.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lt.terzer.courses.Course;
import lt.terzer.databases.CourseDatabase;
import lt.terzer.databases.UserDatabase;
import lt.terzer.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/courses")
public class CourseController {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final CourseDatabase courseDatabase;

    @Autowired
    public CourseController(CourseDatabase courseDatabase){
        this.courseDatabase = courseDatabase;
    }

    @GetMapping("/{id}")
    public String course(Authentication authentication, @PathVariable int id) {
        Course course = courseDatabase.getById(id);
        if(course == null){
            return null;
        }
        return gson.toJson(course);
    }

    @GetMapping("")
    public String course(Authentication authentication) {
        return gson.toJson(courseDatabase.getAll());
    }

}
