package technology.overcast;

import io.quarkus.runtime.StartupEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import technology.overcast.clubs.model.Club;
import technology.overcast.clubs.ClubService;

/**
 * Application Lifecycle
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
@ApplicationScoped
public class DataImportService {

    @ConfigProperty(name = "overcast.member.init", defaultValue = "false")
    boolean init;
    
    @Inject 
    ClubService clubService;
    
    void onStart(@Observes StartupEvent ev) {               
        if(init){
            importData();
        }
    }

    public void importData(){
        // Load data from file
        try (InputStream inputStream = getClass().getResourceAsStream("/init/members.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.readLine(); // First line is the headings
            String line;  
            while((line=reader.readLine())!=null){
                String[] cols = line.split(COMMA);
                
                // Club
                String clubName = cols[COL_CLUB_NAME];
                String clubDisplayName = cols[COL_CLUB_DISPLAY_NAME];
                importClub(clubName, clubDisplayName);
                
                // Member
                
                
                
            }   
        } catch (IOException ex) {
            Logger.getLogger(DataImportService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void importClub(String clubName, String clubDisplayName){
        Club club = clubService.getClub(clubName);
        if(club==null){
            // create club
            club = new Club(clubName.trim(), clubDisplayName.trim());
            clubService.setClub(club);
        }else if (clubDisplayName!=club.displayName){
            // update club
            club.displayName = clubDisplayName.trim();
            clubService.setClub(club);
        }
    }
    
    private static final String COMMA = ",";
    private static final int COL_CLUB_NAME = 0;
    private static final int COL_CLUB_DISPLAY_NAME = 1;
    
}
