package technology.overcast.members;

/**
 * Represent a membership type
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
public class MembershipType {
    private String id;
    private String name;

    public MembershipType() {
    }

    public MembershipType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
