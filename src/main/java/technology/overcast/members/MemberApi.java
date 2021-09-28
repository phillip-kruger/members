package technology.overcast.members;

import technology.overcast.member.model.Member;
import java.util.List;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

@GraphQLApi
public class MemberApi {

    @Inject
    MemberService memberService;
    
    @Inject
    RequestContext requestContext;
    
    @Query
    public List<MembershipType> getMembershipTypes(String club) {
        requestContext.setClub(club);
        return memberService.getMembershipTypes(club);
    }
    
    @Query
    public MembershipType getMembershipType(String club, String id) {
        requestContext.setClub(club);
        return memberService.getMembershipType(club, id);
    }
    
    @Query
    //@Authenticated
    public List<Member> getMembers(String club) {
        requestContext.setClub(club);
        return memberService.getMembers(club);
    }
    
    @Query
    //@Authenticated
    public Member getMember(String club, String id) {
        requestContext.setClub(club);
        return memberService.getMember(club, id);
    }
    
    @Query
    //@Authenticated
    public List<Member> searchMembers(String club, Optional<String> username,Optional<String> email) {
        requestContext.setClub(club);
        return memberService.searchMembers(club, username, email);
    }
    
//    @Authenticated
    public List<List<MembershipType>> getMembershipTypes(@Source List<Member> members){
        return memberService.getMembershipTypes(requestContext.getClub(), members);
    }
    
//    @Authenticated
    public List<Member> getMembers(@Source MembershipType membershipType){
        return memberService.getMembers(requestContext.getClub(), membershipType);
    }
    
    @Mutation
    @RolesAllowed({"admin","committee"})
    public Member createMember(String club, @Valid Member member) throws MemberExistAlreadyException {
        return memberService.createMember(club, member);
    }
    
    @Mutation
    // TODO: Check role manually, as users can update themselves
    public Member updateMember(String club, @Valid Member member) throws MemberExistAlreadyException {
        return memberService.updateMember(club, member);
    }
    
    @Mutation
    @RolesAllowed({"admin","committee"})
    public Member disableMember(String club, String memberId){
        return memberService.enabled(club, memberId,Boolean.FALSE);
    }
    
    @Mutation
    @RolesAllowed({"admin","committee"})
    public Member enableMember(String club, String memberId){
        return memberService.enabled(club, memberId,Boolean.TRUE);
    }
    
    @Mutation
    @RolesAllowed({"admin","committee"})
    public Member joinMembership(String club, String memberId, String groupId){
        return memberService.memberJoinMembership(club, memberId, groupId);
    }
    
    @Mutation
    @RolesAllowed({"admin","committee"})
    public Member leaveMembership(String club, String memberId, String groupId){
        return memberService.memberLeaveMembership(club, memberId, groupId);
    }
    
}