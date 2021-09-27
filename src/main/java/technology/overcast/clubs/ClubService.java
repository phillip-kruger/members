package technology.overcast.clubs;

import technology.overcast.clubs.model.Club;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.representations.idm.RealmRepresentation;
import technology.overcast.KeycloakClient;

@ApplicationScoped
public class ClubService {

    @Inject
    KeycloakClient keycloakClient;
    
    public List<Club> getClubs() {
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmsResource realmsResource = keycloak.realms();
            List<Club> clubs = new ArrayList<>();

            for(RealmRepresentation realmRepresentation:realmsResource.findAll()){
                Club club = toClub(realmRepresentation);
                if(club!=null){
                    clubs.add(club);
                }
            }    
            return clubs;
        }
    }
    
    public Club getClub(String name) {
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource realmResource = keycloak.realm(name);
            if(realmResource!=null){
                try {
                    RealmRepresentation realmRepresentation = realmResource.toRepresentation();
                    return toClub(realmRepresentation);
                }catch (RuntimeException re){
                    // OK
                }
            }
            return null;
        }
    }
    
    public Club setClub(Club club){
        
        Club existing = getClub(club.getName());
        if(existing!=null){
            club.setId(existing.getId());
        }
        
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            if(club.getId()!=null){
                // Update
                RealmRepresentation rr = keycloak.realms().realm(club.getName()).toRepresentation();
                rr.setDisplayName(club.getDisplayName());
                keycloak.realms().realm(club.getName()).update(rr);
            }else{
                // Add
                RealmRepresentation rr = new RealmRepresentation();
                rr.setRealm(club.getName());
                rr.setDisplayName(club.getDisplayName());
                keycloak.realms().create(rr);
            }
            return club;
        }
    }
    
    private String getDisplayName(RealmRepresentation rr){
        if(rr.getDisplayName()!=null){
            return rr.getDisplayName();
        }
        return rr.getRealm();
    }
    
    private Club toClub(RealmRepresentation realmRepresentation){
        String id = realmRepresentation.getId();
        String name = realmRepresentation.getRealm();
        if(!id.equalsIgnoreCase(MASTER)){
            return new Club(realmRepresentation.getId(),realmRepresentation.getRealm(), getDisplayName(realmRepresentation));
        }
        return null;
    }
    
    private static final String MASTER = "master";
}