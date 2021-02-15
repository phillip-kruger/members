package technology.overcast.members;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import org.eclipse.microprofile.graphql.Ignore;
import org.eclipse.microprofile.graphql.NonNull;

/**
 * Represent a club member
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
public class Member {
    private String id;
    @Size(min = 3, message = "Not a valid user name, it's too short")
    private String username;
    private String name;
    private String surname;
    @Email(message = "Not a valid email address")
    private String email;
    private LocalDateTime createdAt;
    private Gender gender;
    @Past(message = "Not a valid birth date") 
    private LocalDate birthdate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    @NonNull
    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    @NonNull
    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    @NonNull
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    @NonNull
    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Ignore
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    @NonNull
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    @Override
    public String toString() {
        return "Member{" + "id=" + id + ", username=" + username + ", name=" + name + ", surname=" + surname + ", email=" + email + ", createdAt=" + createdAt + ", gender=" + gender + ", birthdate=" + birthdate + '}';
    }
}
