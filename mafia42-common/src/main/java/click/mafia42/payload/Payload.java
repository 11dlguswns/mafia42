package click.mafia42.payload;

import java.io.Serializable;

public class Payload implements Serializable {
    private String token;
    private Commend commend;
    private Object body;

    public Payload() {
    }

    public Payload(String token, Commend commend, Object body) {
        this.token = token;
        this.commend = commend;
        this.body = body;
    }

    public void updateToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public Commend getCommend() {
        return commend;
    }

    public Object getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "token='" + token + '\'' +
                ", commend=" + commend +
                ", body=" + body +
                '}';
    }
}
