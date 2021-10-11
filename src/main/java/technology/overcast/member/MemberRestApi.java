package technology.overcast.member;

import technology.overcast.member.model.Member;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@ApplicationScoped
@Path("/member")
@Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Member service",description = "Member management Services")
public class MemberRestApi {

    @Inject
    MemberService memberService;
    
    @GET
    @Path("/{club}")
    @Operation(description = "Get all members in a certain club")
    public List<Member> getMembers(@PathParam("club") String club) {
        return memberService.getMembers(club);
    }
    
    @GET
    @Path("/{club}/{id}")
    @Operation(description = "Get a member in a club using the member id")
    public Member getMember(@PathParam("club") String club, @PathParam("id") String id) {
        return memberService.getMember(club, id);
    }

    @GET
    @Path("/{club}/{id}/{membership}")
    @Operation(description = "Get all member in a certain club")
    public List<MembershipType> getMembershipTypes(@PathParam("club")String club, @PathParam("id") String id){
        return memberService.getMembershipTypes(club, getMember(club, id));
    }
    
    @POST
    @Path("/{club}")
    @RolesAllowed({"admin","committee"})
    public Member createMember(@PathParam("club")String club, @Valid Member member) throws MemberExistAlreadyException {
        return memberService.createMember(club, member);
    }
    
    
}