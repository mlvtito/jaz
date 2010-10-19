package net.rwx.jaz;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * JaZ configuration file helper.
 *
 * This class manage configuration file. File name must be <i>jaz.properties</i>
 * and file location must be in classpath.
 *
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class JazConfig
{
    /** Singleton instance */
    private static JazConfig instance;

    /** Resource bundle for configuration file. */
    private ResourceBundle config;
    
    /**
     * Look for properties file into classpath
     */
    public static void init()
    {
        instance = new JazConfig("jaz");
    }

    /**
     * Singleton method to get unique instance of this class
     *
     * @return
     */
    public static JazConfig getInstance() {
        return instance;
    }

    /**
     * Private constructor for singleton
     * 
     * @param configName Properties file name
     */
    private JazConfig(String configName) {
        this.config = ResourceBundle.getBundle(configName);
    }

    /**
     * Get instance's names list from properties file. The corresponding
     *
     * property name is <i>jaz-instances-list</i>.
     *
     * @return A string with comma sperator name list.
     */
    public String getInstanceList() {
        return this.config.getString("jaz-instances-list");
    }

    public String getConnectorImpl() {
        return this.config.getString("jaz-connector-impl");
    }
    /**
     * Get Zabbix listen port for a specific instance. Corresponding property 
     * name is <i>jaz-agent.&lt;instance name&gt;.port</i>.
     * 
     * @param instance Instance name (which is an item from instance's names 
     * list.
     * 
     * @return Port number
     */
    public String getAgentPort(String instance) {
        return this.config.getString("jaz-agent." + instance + ".port");
    }

    /**
     * Get JMX port to connect to for a specific instance. Corresponding
     * property name is <i>jaz-jvm.&lt;instance name&gt;.port</i>.
     *
     * @param instance Instance name (wich is an item from instance's names
     * list.
     *
     * @return Port number
     */
    public String getJvmPort(String instance) {
        return this.config.getString("jaz-jvm." + instance + ".port");
    }

    /**
     * Get JMX hostname to connectto for a specific instance. orresponding
     * property name is <i>jaz-jvm.&lt;instance name&gt;.server</i>.
     *
     * @param instance Instance name (which is an item from instance's names
     * list.
     *
     * @return Server name
     */
    public String getJvmServer(String instance) {
        return this.config.getString("jaz-jvm." + instance + ".server");
    }

    /**
     * Get login to connect for a specific instance. Corresponding property name
     * is <i>jaz-jvm.&lt;instance&gt;.login</i>. This property is optional.
     *
     * @param instance
     * @return
     */
    public String getJvmLogin(String instance) {
        return getOptionalProperty("jaz-jvm." + instance + ".login");
    }

    /**
     * Get password to connect for a specific instance. Corresponding property
     * name is <i>jaz-jvm.&lt;instance&gt;.password</i>. This property is
     * optional.
     *
     * @param instance
     * @return
     */
    public String getJvmPassword(String instance) {
        return getOptionalProperty("jaz-jvm." + instance + ".password");
    }

    private String getOptionalProperty(String name ) {
        try {
            return this.config.getString( name );
        }catch(MissingResourceException mre ) {
            return "";
        }
    }
}
