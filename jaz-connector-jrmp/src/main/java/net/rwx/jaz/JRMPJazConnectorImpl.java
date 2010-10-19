/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.rwx.jaz;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.util.ArrayList;
import java.util.List;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.log4j.Logger;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class JRMPJazConnectorImpl extends JazConnector implements NotificationListener {

    /** Logger for this class. */
    private static final Logger log = Logger.getLogger(JRMPJazConnectorImpl.class);

    private MBeanServerConnection mbeanserver;

    /** jmx url to connect to. */
    private JMXServiceURL jmxURL;


    private JMXConnector jmxConnector;
    private boolean isConnected;

    /**
     *
     * @param server
     * @param port
     * @param instance With JRMP instance is not necesary
     */
    public JRMPJazConnectorImpl( String server, String port, String instance, String login, String password )
    {
        super(server, port, instance, login, password);

           if ((server == null) || (port == null))
        {
            this.mbeanserver = ManagementFactory.getPlatformMBeanServer();
        }else {
            try {
                this.jmxURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + server + ":" + port + "/jmxrmi");
                connect( );
            } catch (MalformedURLException mue) {
                mue.printStackTrace();
            }
        }
    }

    /**
     *
     * @param notification
     * @param handback
     */
    public void handleNotification(Notification notification, Object handback) {
        JMXConnectionNotification jmxnotif = (JMXConnectionNotification)notification;
        
        if( jmxnotif.getType().equals("jmx.remote.connection.closed") ) {
            log.warn("JMX connection closed ");
            this.isConnected = false;
        }
    }

    /**
     *
     * @param login
     * @param password
     */
    public void connect(  )
    {
        try {
            log.info("Try to connect to server");
            this.jmxConnector = JMXConnectorFactory.connect(this.jmxURL);
            this.jmxConnector.addConnectionNotificationListener(this, null, null);
            this.mbeanserver = this.jmxConnector.getMBeanServerConnection();
            this.isConnected = true;
        } catch( IOException ioe ) {
            log.warn( "Unable to connect to server", ioe );
            this.isConnected = false;
        } catch( SecurityException se ) {
            log.warn( "Unable to connect to server", se );
            this.isConnected = false;
            // TODO : try to ask for login / password
        }
    }

    public List getAttributeList( String objectName, String[] attributesArray ) throws Exception
    {
        try {
            
            AttributeList attributes = this.mbeanserver.getAttributes( new ObjectName(objectName), attributesArray );
            List attributesList = new ArrayList( attributes.size() );
            for( int i=0; i < attributes.size(); i++ ) {
                Attribute attribute = (Attribute)attributes.get( i );
                attributesList.add( new JRMPJazConnectorAttributeImpl(attribute) );
            }

            return attributesList;

        } catch (ConnectException e) {
            log.warn("Impossible de récupérer les Valeus");
            connect();
            throw e;
        }
    }
}
