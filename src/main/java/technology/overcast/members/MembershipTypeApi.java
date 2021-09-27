package technology.overcast.members;

import javax.annotation.security.RolesAllowed;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

/**
 * Basic test to make sure the role are working
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
@GraphQLApi
public class MembershipTypeApi {

    @Query
    @RolesAllowed("admin")
    public String admin() {
        return "admin";
    }

    @Query
    @RolesAllowed("committee")
    public String committee() {
        return "committee";
    } 
            
    @Query
    @RolesAllowed("barman")
    public String barman() {
        return "barman";
    } 
            
    @Query
    @RolesAllowed("league")
    public String league() {
        return "league";
    } 
            
    @Query
    @RolesAllowed("external")
    public String external() {
        return "external";
    } 
            
    @Query
    @RolesAllowed("denel")
    public String denel() {
        return "denel";
    } 
            
    @Query
    @RolesAllowed("pensioner")
    public String pensioner() {
        return "pensioner";
    }
            
    @Query
    @RolesAllowed("student")
    public String student() {
        return "student";
    }
            
    @Query
    @RolesAllowed("scholar")
    public String scholar() {
        return "scholar";
    }
            
    @Query
    @RolesAllowed("supporter")
    public String supporter() {
        return "supporter";
    }
}