package technology.overcast.clubs.model;

/**
 * Club POJO
 * 
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
public class Club {

    public String id;
    public String name;
    public String displayName;

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
}
