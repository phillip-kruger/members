package technology.overcast;

import io.quarkus.runtime.StartupEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import technology.overcast.clubs.model.Club;
import technology.overcast.clubs.ClubService;
import technology.overcast.member.model.Gender;
import technology.overcast.member.model.Member;
import technology.overcast.members.MemberExistAlreadyException;
import technology.overcast.members.MemberService;

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
    
    @Inject
    MemberService memberService;
    
    void onStart(@Observes StartupEvent ev) {               
        if(init){
            importData();
        }
    }

    public void importData(){
        // Load data from file
        try (InputStream inputStream = getClass().getResourceAsStream("/init/clubs.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.readLine(); // First line is the headings
            String line;  
            while((line=reader.readLine())!=null){
                String[] cols = line.split(COMMA);
                
                // Club
                String clubName = cols[COL_CLUB_NAME];
                String clubDisplayName = cols[COL_CLUB_DISPLAY_NAME];
                importClub(clubName, clubDisplayName);
                
                // MembershipTypes
                String membershipTypes = cols[COL_MEMBERSHIP_TYPES];
                importMembershipType(clubName, Arrays.asList(membershipTypes.split(SPACE)));
            }   
        } catch (IOException ex) {
            Logger.getLogger(DataImportService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try (InputStream inputStream = getClass().getResourceAsStream("/init/members.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.readLine(); // First line is the headings
            String line;  
            while((line=reader.readLine())!=null){
                String[] cols = line.split(COMMA);
                
                // Member
                String clubName = cols[COL_CLUB_NAME];
                String memberUserName = cols[COL_MEMBER_USERNAME];
                String memberName = cols[COL_MEMBER_NAME];
                String memberSurname = cols[COL_MEMBER_SURNAME];
                String memberEmail = cols[COL_MEMBER_EMAIL];
                String memberGender = cols[COL_MEMBER_GENDER];
                String memberBirthDate = cols[COL_MEMBER_BIRTHDATE];
                try {
                    Member m = importMember(clubName, memberUserName, memberName, memberSurname, memberEmail, memberGender, memberBirthDate);
                    // Add to groups
                    String memberOf = cols[COL_MEMBER_OF];
                    importMemberOf(clubName, m.getId(), Arrays.asList(memberOf.split(SPACE)));
                } catch (MemberExistAlreadyException ex) {
                    Logger.getLogger(DataImportService.class.getName()).log(Level.SEVERE, null, ex);
                }
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
            club = clubService.setClub(club);
        }else if (clubDisplayName!=club.getDisplayName()){
            // update club
            club.setDisplayName(clubDisplayName.trim());
            club = clubService.setClub(club);
        }
    }

    private void importMembershipType(String club, List<String> typeNames){
        memberService.addMembershipTypes(club, typeNames);
    }
    
    private Member importMember(String club, String memberUserName, String memberName, String memberSurname, String memberEmail, String memberGender, String memberBirthDate) throws MemberExistAlreadyException {
        
        Member m = new Member();
        m.setUsername(memberUserName.trim());
        m.setName(memberName.trim());
        m.setSurname(memberSurname.trim());
        m.setEmail(memberEmail.trim());
        m.setGender(Gender.valueOf(memberGender.trim().toLowerCase()));
        m.setBirthdate(LocalDate.parse(memberBirthDate.trim()));
        
        List<Member> members = memberService.searchMembers(club, Optional.of(memberUserName), Optional.of(memberEmail));
        
        if(members==null || members.isEmpty()){
            // create member
            return memberService.createMember(club, m);
        }else if(members.size()==1){
            // update member
            m.setId(members.get(0).getId());
            return memberService.updateMember(club, m);
        }else{
            throw new RuntimeException("Found multiple users with the same username and/or email");
        }
        
    }
    
    private void importMemberOf(String club, String id, List<String> memberships) {
        memberService.memberJoinMemberships(club,id, memberships);
        memberService.memberLeaveMembership(club, id, "default-roles-quarkus");
    }
    
    private static final String SPACE = " ";
    private static final String COMMA = ",";
    private static final int COL_CLUB_NAME = 0;
    private static final int COL_CLUB_DISPLAY_NAME = 1;
    private static final int COL_MEMBERSHIP_TYPES = 2;
    private static final int COL_MEMBER_USERNAME = 1;
    private static final int COL_MEMBER_NAME = 2;
    private static final int COL_MEMBER_SURNAME = 3;
    private static final int COL_MEMBER_EMAIL = 4;
    private static final int COL_MEMBER_GENDER = 5;
    private static final int COL_MEMBER_BIRTHDATE = 6;
    private static final int COL_MEMBER_OF = 7;

}
