package click.mafia42.entity.user;

import java.util.Objects;
import java.util.UUID;

public class User {
    private UUID id;
    private String nickname;
    private String password;

    public User() {
    }

    public User(UUID id, String nickname, String password) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
    }

    public User(String nickname, String password) {
        this.id = UUID.randomUUID();
        this.nickname = nickname;
        this.password = password;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public UUID getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getNickname(), user.getNickname()) && Objects.equals(getPassword(), user.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNickname(), getPassword());
    }
}
