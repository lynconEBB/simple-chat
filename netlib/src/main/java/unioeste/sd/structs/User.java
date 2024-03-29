package unioeste.sd.structs;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    public String username;

    public String name;
    public SecretKey key;

    public User(String username) {
        this.username = username;
    }

    public User(String username, String name) {
        this.username = username;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
