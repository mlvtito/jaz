package net.rwx.jaz;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import org.kjkoster.zapcat.Agent;
import org.kjkoster.zapcat.zabbix.ZabbixAgent;

/**
 * JaZ is a JMX agent for Zabbix.
 *
 * This agent connect to remote JVM and listen to Zabbix requests to retreive
 * JVM attributes.
 *
 * It uses a JaZ connector to communicate with remote JVM. Connectors work as
 * plugin which is dynamically choosen in properties file.
 *
 * It is a multi-instances agent. It means that 1 JaZ agent can handle several
 * Zabbix agent (which is named listener) connected to several JVM via JMX.
 *
 * This class parse configuration file and start each listener.
 * 
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class JazMain extends Thread
{
    private static final Logger log = Logger.getLogger(JazMain.class);

    /** Listeners list : each listener is an independent zabbix agent. */
    private List agents;

    /**
     * Parse configuration file, initialize and start each listener.
     */
    public JazMain()
    {
        this.agents = new ArrayList();

        // get connector type
        String connectorImpl = JazConfig.getInstance().getConnectorImpl();

        // parameter for constructor is 3 String
        Class[] parametersType = new Class[ JazConnector.CONSTRUCTOR_NB_PARAMS ];
        for(int i=0; i < parametersType.length; i++ ) {
            parametersType[i] = String.class;
        }

        // trying to get reflexive class for connector
        Constructor constructor = null;
        try {
            Class connectorClass = Class.forName( connectorImpl );
            constructor = connectorClass.getConstructor( parametersType );
        }catch( ClassNotFoundException cnfe ) {
            log.fatal("Connector implementation is unknown", cnfe);
            System.exit(-5);
        }catch( NoSuchMethodException nsme ) {
            log.fatal("Connector implementation constructor is unknown", nsme);
            System.exit(-6);
        }


        String instancesList = JazConfig.getInstance().getInstanceList();
        StringTokenizer strtok = new StringTokenizer(instancesList, ",");

        // for each listener
        while (strtok.hasMoreTokens()) {

            // read config from properties file
            String instance = strtok.nextToken();
            String server = JazConfig.getInstance().getJvmServer(instance);
            String portjvm = JazConfig.getInstance().getJvmPort(instance);
            String login = JazConfig.getInstance().getJvmLogin(instance);
            String password = JazConfig.getInstance().getJvmPassword(instance);

            // populate connector constructor parameters
            Object[] parameters = new Object[ JazConnector.CONSTRUCTOR_NB_PARAMS ];
            parameters[0] = server;
            parameters[1] = portjvm;
            parameters[2] = instance;
            parameters[3] = login;
            parameters[4] = password;

            try {
                // instciate connector
                Object obj = constructor.newInstance(parameters);
                JazConnector connector = (JazConnector)obj;
                JazHelper helper = new JazHelper( connector );

                // start new zabbix agent
                String port = JazConfig.getInstance().getAgentPort(instance);
                int iPort = Integer.parseInt(port);
                Agent agent = new ZabbixAgent(null, iPort, helper);
                this.agents.add(agent);
                agent.start();

                log.info("Start instance " + instance + " [" + port +
                        "] is mapped to " + server + "[" + portjvm + "]");
            }catch( InstantiationException ie ) {
                log.error("Unable to initialize connector for instance "
                        + instance, ie);
            }catch( IllegalAccessException iae ) {
                log.error("Unable to initialize connector for instance "
                        + instance, iae);
            }catch( InvocationTargetException ite ) {
                log.error("Unable to initialize connector for instance "
                        + instance, ite);
            }
        }
    }


    /**
     * JaZ main function. It initializes configuration file, instanciate main
     * class and wait for receiving a SIGSTOP.
     *
     * @param args
     */
    public static void main(String[] args)
    {
        // initialize configuration file
        JazConfig.init();

        // start each listener
        JazMain jmx = new JazMain();

        // catch SIGSTOP signal
        Runtime.getRuntime().addShutdownHook(jmx);

        // wait for the end
        while (true)
        {
            try
            {
                Thread.sleep( Long.MAX_VALUE );
            }
            catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}
