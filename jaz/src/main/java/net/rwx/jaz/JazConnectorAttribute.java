/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.rwx.jaz;

/**
 * Define a generic way to store attribute in JaZ agent.
 *
 * The goal is to hide real attribute which is dependant with connector type.
 *
 * In a generic way, an attribute is name/value pair.
 * 
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public abstract class JazConnectorAttribute {

    /**
     * Getter method for attribute's name.
     *
     * @return attribute's name
     */
    public abstract String getName();

    /**
     * Getter method for attribute's value.
     *
     * @return attribute's value
     */
    public abstract String getValue();

    /**
     * Getter method for attribute value but for a composite data.
     *
     * @param field
     * @return
     */
    // TODO : this method should be removed becaus it is specific to jrmp connector
    public abstract String getValue( String field );
    
}
