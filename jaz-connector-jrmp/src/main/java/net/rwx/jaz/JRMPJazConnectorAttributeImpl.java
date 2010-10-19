/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.rwx.jaz;

import javax.management.Attribute;
import javax.management.openmbean.CompositeData;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class JRMPJazConnectorAttributeImpl extends JazConnectorAttribute {

    private Attribute attribute;

    public JRMPJazConnectorAttributeImpl( Attribute attribute ) {
        this.attribute = attribute;
    }

    public String getName() {
        return attribute.getName();
    }

    public String getValue() {
        return attribute.getValue().toString();
    }

    public String getValue( String field ) {
        return resolveFields( (CompositeData)attribute.getValue(), field);
    }

    private String resolveFields( CompositeData attribute, String field )
    {
        int dot = field.indexOf(46);
        if (dot < 0) {
            Object ret = attribute.get(field);
            return ((ret == null) ? null : ret.toString());
        }

        CompositeData data = (CompositeData)attribute.get(field.substring(0, dot));
        return resolveFields(data, field.substring(dot + 1));
  }
}
