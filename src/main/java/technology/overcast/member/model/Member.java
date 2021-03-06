package technology.overcast.member.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import org.eclipse.microprofile.graphql.Ignore;

/**
 * Represent a club member
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
public class Member {
    private String id;
    @NotNull @NotEmpty @Size(min = 3, message = "Not a valid user name, it's too short")
    private String username;
    @NotNull @NotEmpty 
    private String name;
    @NotNull @NotEmpty 
    private String surname;
    @NotNull @NotEmpty @Email(message = "Not a valid email address")
    private String email;
    private LocalDateTime createdAt;
    private Gender gender;
    @NotNull @Past(message = "Not a valid birth date") 
    private LocalDate birthdate;
    
    private boolean enabled = true; // default
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Ignore
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Ignore
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
