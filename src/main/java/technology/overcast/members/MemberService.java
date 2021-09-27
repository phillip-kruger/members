package technology.overcast.members;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

@ApplicationScoped
public class MemberService {

    @Inject
    KeycloakClient keycloakClient;
    
    @ConfigProperty(name = "overcast.generate.temp.password")
    boolean shouldGenerateTempPassword;
    
    @CacheInvalidateAll(cacheName = "membership-types-cache")
    public List<MembershipType> addMembershipTypes(String club, List<String> names) {
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            List<MembershipType> responses = new ArrayList<>();
            for(String name:names){
                if(name!=null && !name.trim().isEmpty()){
                    name = name.trim();
                    // Check if it exist already
                    RoleRepresentation roleRepresentation = null;
                    try {
                        RoleResource existing = clubRealm.roles().get(name);
                        roleRepresentation = existing.toRepresentation();
                    }catch(javax.ws.rs.NotFoundException nfe){
                        roleRepresentation = new RoleRepresentation();
                        roleRepresentation.setName(name);
                        clubRealm.roles().create(roleRepresentation);
                        // Get the saved role
                        roleRepresentation = clubRealm.roles().get(name).toRepresentation();
                    }
                    
                    MembershipType newMembershipType = toMembershipType(roleRepresentation);
                    responses.add(newMembershipType);
                }
            }
            return responses;
        }
    }
    
    @CacheInvalidateAll(cacheName = "membership-types-cache")
    public MembershipType addMembershipType(String club, String name) {
        return addMembershipTypes(club, List.of(name)).get(0);
    }
    
    @CacheResult(cacheName = "membership-types-cache")
    public List<MembershipType> getMembershipTypes(String club) {
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            RolesResource rolesResource = clubRealm.roles();
            return toMembershipTypes(rolesResource.list());
        }
    }
    
    @CacheResult(cacheName = "membership-type-cache")
    public MembershipType getMembershipType(String club, String id) {
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            RolesResource rolesResource = clubRealm.roles();
            RoleResource role = rolesResource.get(id);
            return toMembershipType(role.toRepresentation());
        }
    }
    
    
    public Member memberJoinMembership(String club, String memberId, String membershipType){
        return memberJoinMemberships(club, memberId, List.of(membershipType));
    }
    
    public Member memberJoinMemberships(String club, String memberId, List<String> membershipTypes){
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            UserResource userResource = clubRealm.users().get(memberId);
            UserRepresentation user = userResource.toRepresentation();
            List<RoleRepresentation> roleRepresentations = new ArrayList<>();
            
            for(String membershipType:membershipTypes){
                if(membershipType!=null && !membershipType.trim().isEmpty()){
                    membershipType = membershipType.trim();
                    RolesResource rolesResource = clubRealm.roles();
                    try {
                        RoleResource role = rolesResource.get(membershipType);
                        RoleRepresentation roleRepresentation = role.toRepresentation();
                        roleRepresentations.add(roleRepresentation);
                    }catch(NotFoundException nfe){
                        nfe.printStackTrace();
                    }
                }
            }
            
            userResource.roles().realmLevel().add(roleRepresentations);
            
            return toMember(user);
        }
    }
    
    public Member memberLeaveMembership(String club, String memberId, String membershipType){
        return memberLeaveMemberships(club, memberId, List.of(membershipType));
    }
    
    public Member memberLeaveMemberships(String club, String memberId, List<String> membershipTypes){
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            UserResource userResource = clubRealm.users().get(memberId);
            UserRepresentation user = userResource.toRepresentation();
            List<RoleRepresentation> roleRepresentations = new ArrayList<>();
            
            for(String membershipType:membershipTypes){
                if(membershipType!=null && !membershipType.trim().isEmpty()){
                    membershipType = membershipType.trim();
                    RolesResource rolesResource = clubRealm.roles();
                    try {
                        RoleResource role = rolesResource.get(membershipType);
                        RoleRepresentation roleRepresentation = role.toRepresentation();
                        roleRepresentations.add(roleRepresentation);
                    }catch(NotFoundException nfe){
                        nfe.printStackTrace();
                    }
                }
            }
            
            userResource.roles().realmLevel().remove(roleRepresentations);
            
            return toMember(user);
        }
    }
    
    @CacheResult(cacheName = "members-cache")
    public List<Member> getMembers(String club) {
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            UsersResource usersResource = clubRealm.users();
            return toMembers(usersResource.list());
        }
    }
    
    @CacheResult(cacheName = "member-cache")
    public Member getMember(String club, String id) {
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            UsersResource usersResource = clubRealm.users();
            UserResource userResource = usersResource.get(id);
            UserRepresentation user = userResource.toRepresentation();
            return toMember(user);
        }
    }
    
    public List<Member> searchMembers(String club, Optional<String> username,Optional<String> email) {
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
    
    public List<List<MembershipType>> getMembershipTypes(String club, List<Member> members){
        List<List<MembershipType>> bulk = new ArrayList<>();
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            UsersResource usersResource = clubRealm.users();
            for(Member member:members){
                UserResource userResource = usersResource.get(member.getId());
                bulk.add(toMembershipTypes(userResource.roles()));
            }
            return bulk;
        }
    }
    
    public List<Member> getMembers(String club, MembershipType membershipType){
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            GroupsResource groupsResource = clubRealm.groups();
            GroupResource groupResource = groupsResource.group(membershipType.getId());
            return toMembers(groupResource.members());
        }
    }
    
    public String getMembershipTypeDescription(String club, String membershipTypeId){
        try(Keycloak keycloak = keycloakClient.getKeycloak()){
            RealmResource clubRealm = keycloak.realm(club);
            GroupsResource groupsResource = clubRealm.groups();
            GroupResource groupResource = groupsResource.group(membershipTypeId);
            Map<String, List<String>> attributes = groupResource.toRepresentation().getAttributes();
            if(attributes==null || attributes.isEmpty() || !attributes.containsKey(ATTRIBUTE_DESCRIPTION)){
                return null;
            }
            return attributes.get(ATTRIBUTE_DESCRIPTION).get(0);
        }
    }
    
    @CacheInvalidateAll(cacheName = "members-cache")
    public Member createMember(String club, @Valid Member member) throws MemberExistAlreadyException {
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

            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            if(shouldGenerateTempPassword){
                // Generate and set random password
                passwordCred.setTemporary(true);
                passwordCred.setValue(PasswordGenerator.generate());
            }else{
                // Use default password
                passwordCred.setTemporary(false);
                passwordCred.setValue(member.getUsername());
            }
            userResource.resetPassword(passwordCred);
                
            // TODO: Send email.

            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(Boolean.TRUE);
            user.setEmailVerified(Boolean.TRUE); // TODO: first send email, maybe as part of password ?
            
            userResource.update(user);
            return toMember(user);   
        }
    }
    
    @CacheInvalidateAll(cacheName = "members-cache")
    @CacheInvalidateAll(cacheName = "member-cache")
    public Member updateMember(String club, @Valid Member member) throws MemberExistAlreadyException {
        if(member.getId()==null || member.getId().isEmpty()){
            throw new RuntimeException("Can not update a member that has no id");
        }
        
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
    
    public Member enabled(String club, String memberId, Boolean enabled){
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
            Member m = toMember(user);
            if(m!=null){
                members.add(m);
            }
        }
        return members;
    }
    
    private UserRepresentation toUserRepresentation(Member member){
        UserRepresentation user = new UserRepresentation();
        return updateUserRepresentation(member, user);
    }
    
    private UserRepresentation updateUserRepresentation(Member member, UserRepresentation user){
        user.setUsername(member.getUsername().trim());
        user.setEmail(member.getEmail().trim());
        user.setFirstName(member.getName().trim());
        user.setLastName(member.getSurname().trim());
        if(member.getGender()!=null){
            user.singleAttribute(ATTRIBUTE_GENDER, member.getGender().name());
        }else{
            user.singleAttribute(ATTRIBUTE_GENDER, Gender.unknown.name());
        }
        user.singleAttribute(ATTRIBUTE_BIRTHDATE, member.getBirthdate().toString());
        return user;
    }
    
    private Member toMember(UserRepresentation user){
        if(isValid(user)){
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

            if(user.getCreatedTimestamp()!=null){
                member.setCreatedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(user.getCreatedTimestamp()), ZoneId.systemDefault()));
            }
            
            return member;
        }
        return null;
    }
    
    private boolean isValid(UserRepresentation user){
        return user.getUsername()!=null && 
            user.getFirstName()!=null &&
            user.getLastName()!=null && 
            user.getEmail()!=null;
    }
    
    private List<MembershipType> toMembershipTypes(RoleMappingResource roleMappingResource){
        List<MembershipType> membershipTypes = new ArrayList<>();
        MappingsRepresentation mappingsRepresentation = roleMappingResource.getAll();
        List<RoleRepresentation> rolesResources = mappingsRepresentation.getRealmMappings();
        
        for(RoleRepresentation rolesRepresentation:rolesResources){
            membershipTypes.add(toMembershipType(rolesRepresentation));
        }
        return membershipTypes;
    }
    
    
    private List<MembershipType> toMembershipTypes(List<RoleRepresentation> rolesResources){
        List<MembershipType> membershipTypes = new ArrayList<>();
        for(RoleRepresentation rolesRepresentation:rolesResources){
            membershipTypes.add(toMembershipType(rolesRepresentation));
        }
        return membershipTypes;
    }
    
    private MembershipType toMembershipType(RoleRepresentation roleRepresentation){
        return new MembershipType(roleRepresentation.getId(), roleRepresentation.getName());
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