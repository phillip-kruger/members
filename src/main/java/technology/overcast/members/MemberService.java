package technology.overcast.members;

import technology.overcast.member.model.Gender;
import technology.overcast.member.model.Member;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import technology.overcast.KeycloakClient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

@RequestScoped
public class MemberService {

    @Inject
    KeycloakClient keycloakClient;
    
    private String club;
    
    public List<MembershipType> getMembershipTypes(String club) {
        this.club = club;
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            GroupsResource groupsResource = clubRealm.groups();
            return toMembershipTypes(groupsResource.groups());
        }
    }
    
    public MembershipType getMembershipType(String club, String id) {
        this.club = club;
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            GroupsResource groupsResource = clubRealm.groups();
            GroupResource group = groupsResource.group(id);
            return toMembershipType(group.toRepresentation());
        }
    }
    
    public List<Member> getMembers(String club) {
        this.club = club;
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            UsersResource usersResource = clubRealm.users();
            return toMembers(usersResource.list());
        }
    }
    
    public Member getMember(String club, String id) {
        this.club = club;
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            UsersResource usersResource = clubRealm.users();
            UserResource userResource = usersResource.get(id);
            UserRepresentation user = userResource.toRepresentation();
            return toMember(user);
        }
    }
    
    public List<Member> searchMembers(String club, Optional<String> username,Optional<String> email) {
        this.club = club;
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
        
            RealmResource clubRealm = keycloak.realm(club);
            UsersResource usersResource = clubRealm.users();
        
            Set<UserRepresentation> searchResults = new HashSet<>();
         
            if(username.isPresent()){
                searchResults.addAll(usersResource.search(username.get()));
            }
            if(email.isPresent()){    
                searchResults.addAll(usersResource.search(email.get(),0,1));
            }
        
            if(searchResults.isEmpty()){
                return null;
            }
        
            return toMembers(searchResults.stream().filter(distinctByKey(UserRepresentation::getId)).collect(Collectors.toList()));
        }
    }
    
    public List<MembershipType> getMembershipTypes(Member member){
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(this.club);
            UsersResource usersResource = clubRealm.users();
            UserResource userResource = usersResource.get(member.getId());
            return toMembershipTypes(userResource.groups());
        }
    }
    
    public List<Member> getMembers(MembershipType membershipType){
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(this.club);
            GroupsResource groupsResource = clubRealm.groups();
            GroupResource groupResource = groupsResource.group(membershipType.getId());
            return toMembers(groupResource.members());
        }
    }
    
    public String getDescription(MembershipType membershipType){
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(this.club);
            GroupsResource groupsResource = clubRealm.groups();
            GroupResource groupResource = groupsResource.group(membershipType.getId());
            Map<String, List<String>> attributes = groupResource.toRepresentation().getAttributes();
            if(attributes==null || attributes.isEmpty() || !attributes.containsKey(ATTRIBUTE_DESCRIPTION)){
                return null;
            }
            return attributes.get(ATTRIBUTE_DESCRIPTION).get(0);
        }
    }
    
    public Member createMember(String club, @Valid Member member) throws MemberExistAlreadyException {
        this.club = club;
        if(member.getId()!=null && !member.getId().isEmpty()){
            throw new RuntimeException("Can not create a member that has an id [" + member.getId() + "]");
        }
        validateMember(club, member);
        
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
        
            RealmResource clubRealm = keycloak.realm(club);
            UsersResource usersResource = clubRealm.users();


            Response response = usersResource.create(toUserRepresentation(member));

            int status = response.getStatus();
            if(status!=201){
                throw new RuntimeException("Member [" + member + "] not created ! " + response.getStatusInfo().getReasonPhrase()); // TODO: Notify ?
            }
            String id = CreatedResponseUtil.getCreatedId(response);

            // Get the newly created member
            UserResource userResource = usersResource.get(id);

            // TODO: Assisgn client (see https://gist.github.com/thomasdarimont/c4e739c5a319cf78a4cff3b87173a84b)

            // Generate and set random password
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(true);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(PasswordGenerator.generate());
            userResource.resetPassword(passwordCred);

            // TODO: Send email.

            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(Boolean.TRUE);
            user.setEmailVerified(Boolean.TRUE); // TODO: first send email, maybe as part of password ?
            userResource.update(user);
            return toMember(user);   
        }
    }
    
    public Member updateMember(String club, @Valid Member member) throws MemberExistAlreadyException {
        if(member.getId()==null || member.getId().isEmpty()){
            throw new RuntimeException("Can not update a member that has no id");
        }
        this.club = club;
        
        validateMember(club, member);
        
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
        
            RealmResource clubRealm = keycloak.realm(club);
            UsersResource usersResource = clubRealm.users();
        
            UserResource current = usersResource.get(member.getId());
        
            UserRepresentation user = current.toRepresentation();
        
            boolean shouldValidateEmail = true;
            String oldEmail = user.getEmail();
            if(oldEmail.equalsIgnoreCase(member.getEmail())){
                shouldValidateEmail = false;
            }
        
            user = updateUserRepresentation(member, user);

            if(shouldValidateEmail){
                // TODO: if new email do validation
                user.setEmailVerified(Boolean.TRUE); 
            }
        
            current.update(user);
            return toMember(user);   
        }
    }
    
    public Member addMembershipType(String club, String memberId, String groupId){
        this.club = club;
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            UserResource userResource = clubRealm.users().get(memberId);
            UserRepresentation user = userResource.toRepresentation();
            userResource.joinGroup(groupId);
            return toMember(user);
        }
    }
    
    public Member removeMembershipType(String club, String memberId, String groupId){
        this.club = club;
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            UserResource userResource = clubRealm.users().get(memberId);
            UserRepresentation user = userResource.toRepresentation();
            userResource.leaveGroup(groupId);
            return toMember(user);
        }
    }
    
    public Member enabled(String club, String memberId, Boolean enabled){
        this.club = club;
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            UserResource userResource = clubRealm.users().get(memberId);
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(enabled);
            userResource.update(user);

            return toMember(user);
        }
    }
    
    // TODO: Reset password
    
    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
    
    private List<Member> toMembers(Collection<UserRepresentation> usersRepresentations){
        List<Member> members = new ArrayList<>();
        for(UserRepresentation user:usersRepresentations){
            members.add(toMember(user));
        }
        return members;
    }
    
    private UserRepresentation toUserRepresentation(Member member){
        UserRepresentation user = new UserRepresentation();
        return updateUserRepresentation(member, user);
    }
    
    private UserRepresentation updateUserRepresentation(Member member, UserRepresentation user){
        user.setUsername(member.getUsername());
        user.setEmail(member.getEmail());
        user.setFirstName(member.getName());
        user.setLastName(member.getSurname());
        if(member.getGender()!=null){
            user.singleAttribute(ATTRIBUTE_GENDER, member.getGender().name());
        }else{
            user.singleAttribute(ATTRIBUTE_GENDER, Gender.unknown.name());
        }
        user.singleAttribute(ATTRIBUTE_BIRTHDATE, member.getBirthdate().toString());
        
        return user;
    }
    
    private Member toMember(UserRepresentation user){
        Member member = new Member();
        member.setId(user.getId());
        member.setUsername(user.getUsername());
        member.setName(user.getFirstName());
        member.setSurname(user.getLastName());
        member.setEmail(user.getEmail());

        String genderString = user.firstAttribute(ATTRIBUTE_GENDER);
        if(genderString!=null && !genderString.isEmpty()){
            member.setGender(Gender.valueOf(genderString));
        }else{
            member.setGender(Gender.unknown);
        }

        String birthdateString = user.firstAttribute(ATTRIBUTE_BIRTHDATE);
        if(birthdateString!=null && !birthdateString.isEmpty()){
            member.setBirthdate(LocalDate.parse(birthdateString, DATEFORMATTER));
        }

        member.setEnabled(user.isEnabled());
        
        member.setCreatedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(user.getCreatedTimestamp()), ZoneId.systemDefault()));
        return member;
    }
    
    private List<MembershipType> toMembershipTypes(List<GroupRepresentation> groupsResources){
        List<MembershipType> membershipTypes = new ArrayList<>();
        for(GroupRepresentation groupRepresentation:groupsResources){
            membershipTypes.add(toMembershipType(groupRepresentation));
        }
        return membershipTypes;
    }
    
    private MembershipType toMembershipType(GroupRepresentation groupRepresentation){
        return new MembershipType(groupRepresentation.getId(), groupRepresentation.getName());
    }
    
    private void validateMember(String club, Member member) throws MemberExistAlreadyException{
        
        // Validate Username
        List<Member> membersWithUsername = searchMembers(club, Optional.of(member.getUsername()), Optional.empty());
        if(membersWithUsername!=null && !membersWithUsername.isEmpty()){
            // New user
            if(member.getId()==null || member.getId().isEmpty()){
                throw new MemberExistAlreadyException("Member with username [" + member.getUsername() + "] exists already");
            }else if(membersWithUsername.size()>1){
                throw new RuntimeException("Found multiple members with the same username [" + member.getUsername() + "]");            
            }else {
                // Existing user
                Member me = membersWithUsername.get(0);
                if(!me.getId().equals(member.getId())){
                    throw new MemberExistAlreadyException("Member with username [" + member.getUsername() + "] exists already");
                }
            }
        }
        
        // Validate email
        List<Member> membersWithEmail = searchMembers(club, Optional.empty(),Optional.of(member.getEmail()));        
        if(membersWithEmail!=null && !membersWithEmail.isEmpty()){
            // New user
            if(member.getId()==null || member.getId().isEmpty()){
                throw new MemberExistAlreadyException("Member with email [" + member.getEmail() + "] exists already");
            }else if(membersWithEmail.size()>1){
                throw new RuntimeException("Found multiple members with the same email address [" + member.getEmail() + "]");            
            }else {
                // Existing user
                Member me = membersWithEmail.get(0);
                if(!me.getId().equals(member.getId())){
                    throw new MemberExistAlreadyException("Member with email [" + member.getEmail() + "] exists already");
                }
            }
        }
    }
    
    private static DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Member attributes
    private static final String ATTRIBUTE_GENDER = "gender";
    private static final String ATTRIBUTE_BIRTHDATE = "birthdate";
    
    // Type attributes
    private static final String ATTRIBUTE_DESCRIPTION = "description";
    
}