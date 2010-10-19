/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.rwx.jaz;

import java.util.List;

/**
 * A JaZ connector define the way JaZ will comunicate with monitored server.
 *
 * This abstract class must be totally independant from communication objects.
 * 
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public abstract class JazConnector {

    /**
     * Number of parameters to construct a JazConnector object type.
     */
    public static final int CONSTRUCTOR_NB_PARAMS = 5;
    
    /**
     * Default constructor which connect to monitored server.
     *
     * @param server
     * @param port
     * @param instance
     */
    public JazConnector( String server, String port, String instance, String login, String password ) {
        super();
    }

    /**
     * Try to connect to monitored server.
     * 
     * @param server
     * @param port
     * @param instance
     * @param login
     * @param password
     */
    //public abstract void connect( String login, char[] password );

    /**
     * Define how to read attribute's value from the monitored server.
     *
     * @param objectName Object name
     * @param attributesArray Attribute's names
     * @return a list of JaZ attribute
     * @throws Exception
     */
    public abstract List getAttributeList( String objectName, String[] attributesArray ) throws Exception;
}
