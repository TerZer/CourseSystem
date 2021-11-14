package lt.terzer.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lt.terzer.databases.UserDatabase;
import lt.terzer.user.RestUser;
import lt.terzer.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/register")
public class RegisterController {


    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final UserDatabase userDatabase;

    @Autowired
    public RegisterController(UserDatabase userDatabase){
        this.userDatabase = userDatabase;
    }

    @PutMapping("")
    public @ResponseBody String register(@RequestBody String userString) {
        User user = gson.fromJson(userString, User.class);
        if(user == null){
            return "Error";
        }
        if(user.getId() != -1){
            return "Error";
        }
        User contains = userDatabase.getByUsername(user.getUsername());
        if(contains != null){
            return "Error";
        }
        userDatabase.save(user);
        return "Success";
    }

}
