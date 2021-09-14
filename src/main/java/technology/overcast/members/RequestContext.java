package technology.overcast.members;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class RequestContext {
    private String club;

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }
    
}