package net.rwx.jaz;

import java.rmi.ConnectException;
import net.rwx.jaz.exceptions.ZabbixJMXQueryException;

/**
 * JazAgent class is a Zabbix agent equivalent. It means that 1 JazAgent listen
 * for Zabbix server request on 1 port and is connected to 1 JVM.
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class JazAgent
{
    /** queriesManager keep track of queries from zabbix server. */
    private JazQueries queriesManager;
    
    /** Helper to connect and request JVM metrics. */
    private JazHelper jazHelper;
    
    /**
     * Default constructor with a pre-defined helper and queries manager.
     * 
     * @param helper
     * @param manager
     */
    public JazAgent(JazHelper helper, JazQueries manager)
    {
        this.jazHelper = helper;
        this.queriesManager = manager;
    }

    /**
     * Execute a Zabbix query to get JVM metric.
     * 
     * @param query The Zabbix query.
     * @return
     */
    public String execute(String query)
    {
        // translate zabbix query to a jaz query
        JazQuery jazquery = null;
        try {
            jazquery = new JazQuery(query);
        } catch (ZabbixJMXQueryException zjqe) {
            return "ZBX_NOTSUPPORTED";
        }

        return getStatValue( jazquery );
    }
    
    /**
     * Get value for query if last value is out of date.
     * 
     * @param jazquery A parsed jaz query.
     * @return Value for this query.
     */
    private String getStatValue( JazQuery jazquery ) 
    {
        // get statistic object for this query
        JazQueryStatistic stat = queriesManager.get(jazquery);

        // if last value is outdated, get a new one
        if( stat.getTimeLastValue() <= stat.getTimeLastGet() )
        {
            try 
            {
                String objectName = jazquery.getObjectName();
                jazHelper.query( objectName, queriesManager );
            } catch (ConnectException ce) {
                return "";
            } catch (Exception e) {
                return "ZBX_NOTSUPPORTED";
            }
        }

        // update last get value
        stat.setTimeLastGet( stat.getTime() );
        return stat.getValue();
    }
}
