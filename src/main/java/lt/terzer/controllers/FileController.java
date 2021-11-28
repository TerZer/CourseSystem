package lt.terzer.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lt.terzer.courses.Course;
import lt.terzer.databases.CourseDatabase;
import lt.terzer.databases.FileDatabase;
import lt.terzer.databases.UserDatabase;
import lt.terzer.files.File;
import lt.terzer.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/files")
//TODO not secure
public class FileController {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final FileDatabase fileDatabase;

    @Autowired
    public FileController(FileDatabase fileDatabase){
        this.fileDatabase = fileDatabase;
    }

    @GetMapping("/{id}")
    public String file(Authentication authentication, @PathVariable int id) {
        return gson.toJson(fileDatabase.getById(id));
    }

    @PutMapping("")
    public @ResponseBody String file(Authentication authentication, @RequestBody String fileString) {
        File file = gson.fromJson(fileString, File.class);
        if (file == null) {
            return gson.toJson("Error");
        }
        if (file.getId() != -1) {
            return gson.toJson("Error");
        }
        fileDatabase.save(file);
        return gson.toJson("Success");
    }

    @PutMapping("/{id}")
    public @ResponseBody String file(Authentication authentication, @RequestBody String fileString, @PathVariable int id) {
        File file = gson.fromJson(fileString, File.class);
        if (file == null) {
            return gson.toJson("Error");
        }
        if (file.getId() != id) {
            return gson.toJson("Error");
        }
        fileDatabase.save(file);
        return gson.toJson("Success");
    }

    @GetMapping("")
    public String files(Authentication authentication) {
        return gson.toJson(fileDatabase.getAll());
    }

}
