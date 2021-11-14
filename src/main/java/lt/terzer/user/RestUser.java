package lt.terzer.user;

public class RestUser {

    private String token;
    private String user;

    public RestUser(String user, String token){
        this.token = token;
        this.user = user;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public String getUser() {
        return user;
    }
}
