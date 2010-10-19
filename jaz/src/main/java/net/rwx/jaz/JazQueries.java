package net.rwx.jaz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a queries manager
 * 
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class JazQueries
{
    /**
     * Store attribute's canonical names which have been queried with this
     * object name.
     */
    private Map mapObjectAttribute; // Map<String, List<String>>

    /**
     * Store jaz queries for this object name and canonical attribute name.
     *
     * In most cases, there is only one query except for composite data because
     * canonical name is different from full name.
     */
    private Map mapObjectAttributeQueries; // Map<String, List<JazQuery>>
    
    /**
     * Store statistic for a query.
     */
    private Map mapQueryStatistic; // Map<JazQuery, JazQueryStatistic>

    /**
     * Default constructor
     */
    public JazQueries()
    {
        this.mapObjectAttribute = new HashMap();
        this.mapObjectAttributeQueries = new HashMap();
        this.mapQueryStatistic = new HashMap();
    }

    /**
     * Get statistic object for a query.
     *
     * This method initialize all lists for this query so, for a query, it must
     * be the first method to call on queries manager.
     *
     * @param query Query to get statistics
     * @return
     */
    public JazQueryStatistic get(JazQuery query)
    {
        // get or create statistic object
        JazQueryStatistic stat = (JazQueryStatistic)this.mapQueryStatistic.get(query);
        if (stat == null)
        {
            stat = new JazQueryStatistic();
            this.mapQueryStatistic.put(query, stat);
        }

        // if doesn't exist, create attributes list for this object name
        List attributeNameForObject = (List)this.mapObjectAttribute.get(query.getObjectName());
        if( attributeNameForObject == null )
        {
            attributeNameForObject = new ArrayList();
            this.mapObjectAttribute.put(query.getObjectName(), attributeNameForObject);
        }
        
        // if this attribute name is not in the list, add it
        if( attributeNameForObject.indexOf( query.getCanonicalAttributeName() ) < 0 )
        {
            attributeNameForObject.add( query.getCanonicalAttributeName() );
            Collections.sort( attributeNameForObject );
        }
        
        // if doesn't exist, create queries list for this object and attribute name
        String key = query.getObjectName() + query.getCanonicalAttributeName();
        List queriesForObjectAttribute = (List)this.mapObjectAttributeQueries.get(key);
        if (queriesForObjectAttribute == null)
        {
            queriesForObjectAttribute = new ArrayList();
            this.mapObjectAttributeQueries.put(key, queriesForObjectAttribute);
        }

        // if this query not in the list, add it
        if (queriesForObjectAttribute.indexOf(query) < 0)
        {
            queriesForObjectAttribute.add(query);
        }
        
        // return statistic
        return stat;
    }

    /**
     * Get attribute's canonical names for an object name.
     *
     * @param objectName The object name.
     * @return
     */
    public List getAttributeForObject(String objectName)
    {
        List attributeNameForObject = (List)this.mapObjectAttribute.get(objectName);
        if (attributeNameForObject == null)
        {
            return null;
        }
        
        return attributeNameForObject;
    }

    /**
     * Get JaZ queries list for an object name and a canonical attribute name.
     *
     * @param objectName The object name.
     * @param canonicalAttributeName Attribute's canonica name.
     * @return
     */
    public List getQueriesForObject(String objectName, String canonicalAttributeName)
    {
        String key = objectName + canonicalAttributeName;
        List queriesForObject = (List)this.mapObjectAttributeQueries.get(key);
        if (queriesForObject == null)
        {
            return null;
        }
        
        return queriesForObject;
    }
}
