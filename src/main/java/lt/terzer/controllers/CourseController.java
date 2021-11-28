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
    private final UserDatabase userDatabase;

    @Autowired
    public CourseController(UserDatabase userDatabase, CourseDatabase courseDatabase){
        this.userDatabase = userDatabase;
        this.courseDatabase = courseDatabase;
    }

    @GetMapping("/{id}")
    public String course(Authentication authentication, @PathVariable int id) {
        User user = userDatabase.getByUsername(authentication.getName());
        if(!user.isAdmin() && !user.getAccessibleCourses().contains(id)) {
            return null;
        }
        Course course = courseDatabase.getById(id);
        if(course == null){
            return null;
        }
        return gson.toJson(course);
    }

    @PutMapping("")
    public @ResponseBody String course(Authentication authentication, @RequestBody String courseString) {
        User user = userDatabase.getByUsername(authentication.getName());
        if(user.isCourseCreator() || user.isAdmin()) {
            Course course = gson.fromJson(courseString, Course.class);
            if (course == null) {
                return gson.toJson("Error");
            }
            if (course.getId() != -1) {
                return gson.toJson("Error");
            }
            courseDatabase.save(course);
            return "Success";
        }
        else{
            return gson.toJson("Error");
        }
    }

    @PutMapping("/{id}")
    public @ResponseBody String course(Authentication authentication, @RequestBody String courseString, @PathVariable int id) {
        User user = userDatabase.getByUsername(authentication.getName());
        if(user.getEditableCourses().contains(id) || user.isAdmin()) {
            Course course = gson.fromJson(courseString, Course.class);
            if (course == null) {
                return gson.toJson("Error");
            }
            if (course.getId() != id) {
                return gson.toJson("Error");
            }
            courseDatabase.save(course);
            return gson.toJson("Success");
        }
        else{
            return gson.toJson("Error");
        }
    }

    @GetMapping("")
    public String course(Authentication authentication) {
        User user = userDatabase.getByUsername(authentication.getName());
        if(user.isAdmin()) {
            return gson.toJson(courseDatabase.getAll());
        }
        return gson.toJson(courseDatabase.getByIds(user.getAccessibleCourses()));
    }

}
