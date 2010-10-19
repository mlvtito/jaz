package net.rwx.jaz;

import net.rwx.jaz.exceptions.ZabbixJMXQueryException;

/**
 * Represents a JaZ query.
 *
 * A JaZ query translate message received as a request by JaZ agent to extract
 * object name and property name.
 * 
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class JazQuery implements Comparable
{
    /** Object name extracted from jaz request. */
    private String objectName;

    /** Attribute name extracted from jaz request. */
    private String attributeName;

    /**
     * Constructor which parse jaz request to extract component of the query.
     * The request syntax is : "jmx[objectName][attributeName]".
     *
     * @param jazQuery JaZ request
     * @throws ZabbixJMXQueryException if syntax is not ok
     */
    public JazQuery(String jazQuery) throws ZabbixJMXQueryException
    {
        // jaz request should start with 'jmx'
        if (!(jazQuery.startsWith("jmx"))) {
            throw new ZabbixJMXQueryException(jazQuery, "This query is not a good type");
        }

        // search for last bracket
        int bracketOpen = jazQuery.lastIndexOf(91);
        int bracketClose = jazQuery.lastIndexOf(93);

        // check bracket position
        if ((bracketOpen >= 0) && (bracketClose >= 0) && (bracketClose > bracketOpen + 1))
        {
            attributeName = jazQuery.substring(bracketOpen + 1, bracketClose);
        }else {
            throw new ZabbixJMXQueryException(jazQuery, "This query has no attribute name");
        }

        // search for first bracket
        bracketClose = jazQuery.lastIndexOf(93, bracketOpen);
        bracketOpen = jazQuery.indexOf(91);
        if ((bracketOpen >= 0) && (bracketClose >= 0) && (bracketClose > bracketOpen + 1))
        {
            objectName = jazQuery.substring(bracketOpen + 1, bracketClose);
        }else {
            throw new ZabbixJMXQueryException(jazQuery, "This query has no object name");
        }
  }

    /**
     * Implements equals method, so JazQuery is comparable to another JazQuery.
     * 
     * @param other
     * @return
     */
    public boolean equals(Object other)
    {
        if( other == null ) {
            return false;
        }
        
        if( other == this ) {
            return true;
        }
        
        if( super.getClass() != other.getClass() ) {
            return false;
        }
        
        JazQuery query = (JazQuery)other;
        boolean isEqual = true;
        if( (getAttributeName() == null) && (query.getAttributeName() != null) ) {
            return false;
        }
        
        if( (getAttributeName() != null) && (query.getAttributeName() == null) ) {
            return false;
        }
        
        if( (getObjectName() == null) && (query.getObjectName() != null) ) {
            return false;
        }
        
        if( (getObjectName() != null) && (query.getObjectName() == null) ) {
            return false;
        }
        
        if( ! ( getAttributeName().equals( query.getAttributeName() ) ) ) {
            return false;
        }
        
        return ( getObjectName().equals( query.getObjectName() ) );
    }

    /**
     * Implements hashCode method, so JazQuery is comparable to another JazQuery.
     * 
     * @return
     */
    public int hashCode()
    {
        int PRIME = 31;
        int result = 1;
        result = 31 * result + this.attributeName.hashCode() + this.objectName.hashCode();
        return result;
    }
    
    /**
     * Implements compareTo method, so JazQuery is comparable to another JazQuery.
     * 
     * @return
     */
    public int compareTo( Object obj )
    {
      JazQuery query = (JazQuery)obj;
      
      String thiskey = getObjectName() + getAttributeName();
      String otherkey = query.getObjectName() + query.getAttributeName();
      
      return thiskey.compareTo( otherkey );
    }
    
    /**
     * Getter method for attribute name.
     * 
     * @return
     */
    public String getAttributeName()
    {
        return this.attributeName;
    }
    
    /**
     * Setter method for attribute name.
     * 
     * @param attributeName
     */
    public void setAttributeName(String attributeName)
    {
        this.attributeName = attributeName;
    }
    
    /**
     * Get canonical name for attribute name.
     * 
     * Canonical name is the first atomic name for composite data which exist 
     * in JMX 1.2.
     * 
     * @return
     */
    public String getCanonicalAttributeName()
    {
        int plot = this.attributeName.indexOf(".");
        if (plot > 0)
        {
            return this.attributeName.substring(0, plot);
        }

        return this.attributeName;
    }
    
    /**
     * Getter method for object name.
     * 
     * @return
     */
    public String getObjectName()
    {
        return this.objectName;
    }
    
    /**
     * Setter method for object name.
     * 
     * @param objectName
     */
    public void setObjectName(String objectName)
    {
        this.objectName = objectName;
    }
}
