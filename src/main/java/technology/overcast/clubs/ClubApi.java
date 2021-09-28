package technology.overcast.clubs;

import technology.overcast.clubs.model.Club;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
public class ClubApi {

    @Inject
    ClubService clubService;
    
    @Query
    public List<Club> getClubs() {
        return clubService.getClubs();
    }
    
    @Query
    public Club getClub(String name) {
        return clubService.getClub(name);
    }
    
    @Mutation
    @RolesAllowed("admin")
    public Club setClub(Club club){
        return clubService.setClub(club);
    }
}