package lt.terzer.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lt.terzer.courses.Course;
import lt.terzer.databases.CourseDatabase;
import lt.terzer.databases.FileDatabase;
import lt.terzer.files.File;
import lt.terzer.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/delete")
//TODO not secure
public class DeleteController {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final CourseDatabase courseDatabase;
    private final FileDatabase fileDatabase;

    public DeleteController(CourseDatabase courseDatabase, FileDatabase fileDatabase) {
        this.courseDatabase = courseDatabase;
        this.fileDatabase = fileDatabase;
    }

    @DeleteMapping("/courses/{id}")
    public void course(Authentication authentication, @PathVariable int id) {
        Course course = courseDatabase.getById(id);
        if(course != null){
            courseDatabase.remove(course);
        }
    }

    @DeleteMapping("/files/{id}")
    public void file(Authentication authentication, @PathVariable int id) {
        File file = fileDatabase.getById(id);
        if(file != null){
            fileDatabase.remove(file);
        }
    }

}
