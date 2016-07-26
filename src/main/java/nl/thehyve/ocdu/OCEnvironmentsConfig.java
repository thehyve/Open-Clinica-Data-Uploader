package nl.thehyve.ocdu;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Represents configured OpenClinica environments which will be presented to the user during login.
 * OpenClinica environments can be added in application.yml inside resources folder
 *
 * Created by piotrzakrzewski on 18/04/16.
 */
@ConfigurationProperties(prefix = "OpenClinicaEnvironments")
@Configuration
public class OCEnvironmentsConfig {

    public final static String OC_ENV_ATTRIBUTE_NAME = "ocEnvironment";
    private List<OCEnvironment> ocEnvironments;

    public List<OCEnvironment> getOcEnvironments() {
        return ocEnvironments;
    }

    public OCEnvironmentsConfig(List<OCEnvironment> ocEnvironments) {
        this.ocEnvironments = ocEnvironments;
    }

    public void setOcEnvironments(List<OCEnvironment> ocEnvironments) {
        this.ocEnvironments = ocEnvironments;
    }

    public OCEnvironmentsConfig() {
    }


    public static class OCEnvironment {
        private String url;
        private String name;

        public OCEnvironment() {

        }

        public OCEnvironment(String url, String name, String version) {
            this.url = url;
            this.name = name;
            this.version = version;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        private String version;
    }
}
