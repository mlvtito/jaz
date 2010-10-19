/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.rwx.jaz;

import javax.management.Attribute;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class WeblogicJazConnectorAttributeImpl extends JazConnectorAttribute {

    private Attribute attribute;

    public WeblogicJazConnectorAttributeImpl( Attribute attribute ) {
        this.attribute = attribute;
    }

    public String getName() {
        return attribute.getName();
    }

    public String getValue() {
        return attribute.getValue().toString();
    }

    // Composite Data doesn't seem to exist in weblogic.jar
    public String getValue( String field ) {
        return attribute.getValue().toString();
    }

}
