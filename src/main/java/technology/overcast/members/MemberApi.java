package technology.overcast.members;

import technology.overcast.member.model.Member;
import java.util.List;
import java.util.Optional;
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
    
    @Query
    public List<MembershipType> getMembershipTypes(String club) {
        return memberService.getMembershipTypes(club);
    }
    
    @Query
    public MembershipType getMembershipType(String club, String id) {
        return memberService.getMembershipType(club, id);
    }
    
    @Query
    public List<Member> getMembers(String club) {
        return memberService.getMembers(club);
    }
    
    @Query
    public Member getMember(String club, String id) {
        return memberService.getMember(club, id);
    }
    
    @Query
    public List<Member> searchMembers(String club, Optional<String> username,Optional<String> email) {
        return memberService.searchMembers(club, username, email);
    }
    
    public List<MembershipType> getMembershipTypes(@Source Member member){
        return memberService.getMembershipTypes(member);
    }
    
    public List<Member> getMembers(@Source MembershipType membershipType){
        return memberService.getMembers(membershipType);
    }
    
    public String getDescription(@Source MembershipType membershipType){
        return memberService.getDescription(membershipType);
    }
    
    @Mutation
    public Member createMember(String club, @Valid Member member) throws MemberExistAlreadyException {
        return memberService.createMember(club, member);
    }
    
    @Mutation
    public Member updateMember(String club, @Valid Member member) throws MemberExistAlreadyException {
        return memberService.updateMember(club, member);
    }
    
    @Mutation
    public Member disableMember(String club, String memberId){
        return memberService.enabled(club, memberId,Boolean.FALSE);
    }
    
    @Mutation
    public Member enableMember(String club, String memberId){
        return memberService.enabled(club, memberId,Boolean.TRUE);
    }
    
    @Mutation
    public Member addMembershipType(String club, String memberId, String groupId){
        return memberService.addMembershipType(club, memberId, groupId);
    }
    
    @Mutation
    public Member removeMembershipType(String club, String memberId, String groupId){
        return memberService.removeMembershipType(club, memberId, groupId);
    }
    
}