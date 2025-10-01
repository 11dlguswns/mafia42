package click.mafia42.payload;

import java.io.Serializable;
import java.util.UUID;

public class Payload implements Serializable {
    private UUID payloadId;
    private String token;
    private Commend commend;
    private Object body;

    public Payload() {
    }

    public Payload(Commend commend, Object body) {
        this.commend = commend;
        this.body = body;
    }

    public void updatePayloadId(UUID payloadId) {
        this.payloadId = payloadId;
    }

    public void updateToken(String token) {
        this.token = token;
    }

    public UUID getPayloadId() {
        return payloadId;
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
