/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.rwx.jaz;

import java.util.ArrayList;
import java.util.List;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.Context;
import javax.naming.NamingException;
import weblogic.jndi.Environment;
import weblogic.management.MBeanHome;
import weblogic.management.RemoteNotificationListener;
import weblogic.management.logging.WebLogicLogNotification;
import weblogic.management.runtime.LogBroadcasterRuntimeMBean;

/**
 * Define how to connect a Weblogic 8 instance and read properties
 * from MBean.
 *
 * It connects through t3 protocol.
 * 
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class WeblogicJazConnectorImpl extends JazConnector implements RemoteNotificationListener  {

    /** Provider's url. */
    private String providerUrl;

    /** Instance name. */
    private String instance;
    
    /** Login to connect. */
    private String login;

    /** Encrypted password. */
    private String password;
    
    /** Connection to Weblogic Server. */
    private MBeanServer connection;

    /**
     * Default constructor which connect to Weblogic's instance
     * 
     * @param server Weblogic server's name
     * @param port Weblogic server's t3 port
     * @param instance Weblogic instance name
     */
    public WeblogicJazConnectorImpl( String server, String port, String instance, String login, String password )
    {
        super( server, port, instance, login, password );

        providerUrl = "t3://" + server + ":" + port;
        this.instance = instance;
        this.login = login;
        this.password = password;
        
        connect( );
    }

    /**
     *
     * @param login
     * @param password
     */
    public void connect( )
    {
        // Set environnement to connect
        Environment environment = new Environment();
        environment.setProviderUrl( providerUrl );
        environment.setSecurityPrincipal( login );
        environment.setSecurityCredentials( password );

        MBeanHome localHome = null;
        try {
            Context ctx = environment.getInitialContext();
            localHome = (MBeanHome)ctx.lookup( MBeanHome.LOCAL_JNDI_NAME );
        }catch( NamingException ne ) {
            // TODO : it is not quite clean, we need a better react to exception
            System.out.println("IMPOSSIBLE de SE CONNECTER");
        }

        connection = (MBeanServer)localHome.getMBeanServer();

        // TODO : a terminer => http://download.oracle.com/docs/cd/E13222_01/wls/docs81b/logging/listening.html#1126901
        /*try {
            ObjectName logBCOname = new ObjectName("mydomain:Location="+instance+",Name=TheLogBroadcaster,Type=LogBroadcasterRuntime");
            connection.addNotificationListener(logBCOname, this, null, null);
        }catch( InstanceNotFoundException infe ) {
            System.out.println("Impossible d'attacher le listener a la connexion");
        }catch( MalformedObjectNameException mfone ) {
            System.out.println("Impossible d'attacher le listener a la connexion");
        }*/
    }

    public void handleNotification(Notification notification, Object handback) {
        WebLogicLogNotification wln = (WebLogicLogNotification)notification;
    System.out.println("WebLogicLogNotification");
    System.out.println(" type = " + wln.getType());
    System.out.println(" message id = " + wln.getMessageId());
    System.out.println(" server name = " + wln.getServername());
    System.out.println(" timestamp = " + wln.getTimeStamp());
    System.out.println(" message = " + wln.getMessage() + "\n");
    }
    /**
     * This function define how to read attribute from Weblogi instance.
     *
     * @param objectName MBean object's name which contain the attribute
     * @param attributesArray Attibute's names to read
     * @return a list of JazAttribtueConnector
     * @throws Exception
     */
    public List getAttributeList( String objectName, String[] attributesArray ) throws Exception
    {
        try {
        ObjectName on = new ObjectName( objectName );
        AttributeList attributes = connection.getAttributes( on, attributesArray );

        List attributesList = new ArrayList( attributes.size() );

        for( int i=0; i < attributes.size(); i++ ) {
            Attribute attribute = (Attribute)attributes.get( i );
            attributesList.add( new WeblogicJazConnectorAttributeImpl(attribute) );
        }

        return attributesList;
        }catch(MalformedObjectNameException mfone ) {
            connect();
            throw mfone;
        }catch(InstanceNotFoundException infe ) {
            connect();
            throw infe;
        }catch(ReflectionException re ) {
            connect();
            throw re;
        }
    }
}
