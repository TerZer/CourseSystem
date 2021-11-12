package lt.terzer.user;

public class RestUser {

    private final String token;
    private final String user;

    public RestUser(String token, String user){
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public String getUser() {
        return user;
    }
}
