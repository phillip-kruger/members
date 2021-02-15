package technology.overcast.clubs;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.representations.idm.RealmRepresentation;
import technology.overcast.KeycloakClient;

@GraphQLApi
public class ClubManagementApi {

    private static final String MASTER = "master";
    
    @Inject
    KeycloakClient keycloakClient;
    
    @Query
    public List<Club> clubs() {
        Keycloak keycloak = keycloakClient.getKeycloak();
        RealmsResource realmsResource = keycloak.realms();
        
        List<Club> clubs = new ArrayList<>();
          
        for(RealmRepresentation realmRepresentation:realmsResource.findAll()){
            String id = realmRepresentation.getId();
            if(!id.equalsIgnoreCase(MASTER)){
                clubs.add(new Club(realmRepresentation.getId(),realmRepresentation.getDisplayName()));
            }
        }
        
        return clubs;
    }
    
    // TODO: Allow adding and removing of Clubs
}