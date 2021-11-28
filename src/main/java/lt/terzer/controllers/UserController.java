package lt.terzer.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lt.terzer.courses.Course;
import lt.terzer.databases.UserDatabase;
import lt.terzer.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final UserDatabase userDatabase;

    @Autowired
    public UserController(UserDatabase userDatabase){
        this.userDatabase = userDatabase;
    }


    @GetMapping("/{id}")
    public String user(Authentication authentication, @PathVariable int id) {
        User user = userDatabase.getById(id);
        if(user == null){
            return null;
        }
        return gson.toJson(user);
    }

    @PostMapping("")
    public String user(Authentication authentication, @RequestParam("user") String username){
        User user = userDatabase.getByUsername(username);
        if(user == null){
            return null;
        }
        return gson.toJson(user);
    }

    @PutMapping("/{id}")
    public String updateUser(Authentication authentication, @RequestBody String userString, @PathVariable int id){
        User user = userDatabase.getByUsername(authentication.getName());
        if(user.isCourseCreator() || user.isAdmin()) {
            User user1 = gson.fromJson(userString, User.class);
            if (user1 == null) {
                return gson.toJson("Error");
            }
            if (user1.getId() != id) {
                return gson.toJson("Error");
            }
            userDatabase.save(user1);
            return gson.toJson("Success");
        }
        else{
            return gson.toJson("Error");
        }
    }


    @GetMapping("")
    public String user(Authentication authentication) {
        return gson.toJson(userDatabase.getAll());
    }

}