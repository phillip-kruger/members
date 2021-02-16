package technology.overcast;

import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

/**
 * Access to the Keycloak client
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
@ApplicationScoped
public class KeycloakClient {

    @ConfigProperty(name = "ovecast.keycloak.serverUrl", defaultValue = "http://localhost:8080/auth")
    String serverUrl;
    
    @ConfigProperty(name = "ovecast.keycloak.masterRealm", defaultValue = "master")
    String masterRealm;
    
    @ConfigProperty(name = "ovecast.keycloak.clientId", defaultValue = "admin-cli")
    String clientId;
    
    @ConfigProperty(name = "ovecast.keycloak.username", defaultValue = "admin")
    String username;
    
    @ConfigProperty(name = "ovecast.keycloak.password", defaultValue = "(b4xfUs_[cP/p4:b5KcH*;U,(KUF\\EMz`:U,q3(AF\"J:\\}_`y$B<<8_Ce7eG''_2DEsW>'~&wQx%-W3JT/FC\\c+tFKQ4s\"-\\`YA%}W4h^AP<%52;Nx`3SF$&*SX=%V=:'tUH;[j{`BG#-UCNg38^;gV6:*A7,\"84xbxQmS]8<WAG.SJxvnuvnT-a2j(ffWe\"xV4\\3Wr4yDGM$c^VD*ch$ANNLw_9V?3>\\*M%x+VBkLFf<xw:94(7A/MqBj@5va{]")
    String password;
    
    public Keycloak getKeycloak(){
        
        return KeycloakBuilder.builder()
				.serverUrl(serverUrl)
				.realm(masterRealm)
				.clientId(clientId)
                                .username(username)
                                .password(password)
                                .grantType(GRANT_TYPE)
				.build();
    }
    
    private static final String GRANT_TYPE = "password";
    
}
