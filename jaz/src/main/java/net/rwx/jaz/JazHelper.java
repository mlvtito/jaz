package net.rwx.jaz;

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * JaZ helper know how to use connector and update data in queries manager.
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class JazHelper
{
    /** A logger for this class. */
    private static final Logger log = Logger.getLogger(JazHelper.class);

    /** Connector to use to communicate with monitored server. */
    private JazConnector connector;

    /**
     * Default constructor with a pre-defined connector.
     *
     * @param conn
     */
    public JazHelper( JazConnector conn )
    {
        this.connector = conn;
    }

    /**
     * Execute the current query by using connector to retreive attribute's
     * values and updating statistics in queries manager.
     *
     * @param objectName Object name for this query
     * @param queriesManager Queries manager
     * @throws Exception
     */
    public void query(String objectName, JazQueries queriesManager) throws Exception
    {
        // get all attributes for this object name
        List attributes = queriesManager.getAttributeForObject(objectName);
        String[] attributesArray = new String[attributes.size()];
        for (int i = 0; i < attributes.size(); ++i) {
            attributesArray[i] = ((String)attributes.get(i));
        }
        
        log.info("Requesting JMX for object [" + objectName + "]");

        // retreive attributes values from connector
        List list = connector.getAttributeList(objectName, attributesArray);

        // for each attribute
        for (int i = 0; i < list.size(); ++i)
        {
            JazConnectorAttribute jmxAttr = (JazConnectorAttribute)list.get(i);

            // get attribute canonical name
            String canAttrName = jmxAttr.getName();

            // get queries associate to this attribute
            // in most cases, we should only get 1 except for composite data
            List queries = queriesManager.getQueriesForObject(objectName,
                    canAttrName);

            Iterator it = queries.iterator();
            while ( it.hasNext() ) {
                JazQuery query = (JazQuery)it.next();
                
                // get statistic object with this query
                JazQueryStatistic stat = queriesManager.get(query);
                
                // get full attribute name (with composites if have some)
                String fullAttrName = query.getAttributeName();
                
                // update last value for this attribute into statistic object
                int dot = fullAttrName.indexOf(46);
                if (dot < 0) // if it is not a composite data
                {
                    stat.setValue( jmxAttr.getValue() );
                    stat.setTimeLastValue(stat.getTime());
                } else {
                    String field = fullAttrName.substring(dot + 1);
                    stat.setValue( jmxAttr.getValue( field ) );
                    stat.setTimeLastValue(stat.getTime());
                }
            }
        }
    }
}
