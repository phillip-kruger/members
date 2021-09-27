package technology.overcast.clubs.model;

/**
 * Club POJO
 * 
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
public class Club {

    private String id;
    private String name;
    private String displayName;

    public Club() {
    }

    public Club(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }
    
    public Club(String id, String name, String displayName) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
