package technology.overcast;

import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Tenant information
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
@ApplicationScoped
public class Tenant {

    @ConfigProperty(name = "ovecast.tenant", defaultValue = "Kentron") // TODO: Remove defaultValue
    String tenant;
    
    public String getTenant(){
        return tenant;
    }
    
}
