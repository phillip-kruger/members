package technology.overcast;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

/**
 * Access to the Keycloak client
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
@ApplicationScoped
public class KeycloakClient {

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;
    
    @ConfigProperty(name = "ovecast.keycloak.masterRealm", defaultValue = "master")
    String masterRealm;
    
    @ConfigProperty(name = "ovecast.keycloak.clientId", defaultValue = "admin-cli")
    String clientId;
    
    @ConfigProperty(name = "ovecast.keycloak.admin.username", defaultValue = "admin")
    String username;
    
    @ConfigProperty(name = "ovecast.keycloak.admin.password", defaultValue = "admin")
    String password;
    
    private Keycloak keycloak;
    
    @PostConstruct
    public void init(){
        this.keycloak = KeycloakBuilder.builder()
                        .serverUrl(getServerUrl())
                        .realm(masterRealm)
                        .clientId(clientId)
                        .username(username)
                        .password(password)
                        .grantType(GRANT_TYPE)
                        .build();
    }
    
    public Keycloak getKeycloak(){
        return keycloak;
    }
    
    private String getServerUrl(){
        int i = authServerUrl.indexOf("/realms/");
        return authServerUrl.substring(0, i);
    }
    
    private static final String GRANT_TYPE = "password";
}
