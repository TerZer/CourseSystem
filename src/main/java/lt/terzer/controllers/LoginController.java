package lt.terzer.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lt.terzer.databases.UserDatabase;
import lt.terzer.filters.JWTAuthorizationFilter;
import lt.terzer.user.RestUser;
import lt.terzer.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/login")
public class LoginController {

    private final UserDatabase userDatabase;

    @Autowired
    public LoginController(UserDatabase userDatabase){
        this.userDatabase = userDatabase;
    }

    @PostMapping("")
    public RestUser login(@RequestParam("user") String username, @RequestParam("password") String pwd) {
        if(validateUser(username, pwd)) {
            String token = getJWTToken(username);
            return new RestUser(username, token);
        }
        return null;
    }


    private boolean validateUser(String username, String password){
        User user = userDatabase.getByUsername(username);
        if(user != null){
            return user.getPassword().equalsIgnoreCase(password);
        }
        return false;
    }

    private String getJWTToken(String username) {
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId("courseSystemJWT")
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512,
                        JWTAuthorizationFilter.SECRET.getBytes()).compact();

        return JWTAuthorizationFilter.PREFIX + token;
    }
}