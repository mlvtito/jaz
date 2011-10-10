/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jaz.mocks;

import java.rmi.ConnectException;

import net.rwx.jaz.JazConnector;
import net.rwx.jaz.JazHelper;
import net.rwx.jaz.JazQueries;
import net.rwx.jaz.JazQueryStatistic;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class JazHelperMock extends JazHelper {
    
    private JazQueryStatistic statistic;
    private boolean isException = false;
    private boolean isConnectException = false;
    
    public JazHelperMock( JazConnector conn ) {
        super( conn );
    }

    public void setStatistic( JazQueryStatistic stat ) 
    {
        this.statistic = stat;
    }
    
    public void setException( boolean isException )
    {
        this.isException = isException;
    }
    
    public void setConnectException( boolean isConnectException ) 
    {
        this.isConnectException = isConnectException;
    }
    
    public void query(String objectName, JazQueries queriesManager) throws Exception 
    {
        if( isException ) {
            throw new Exception("FROM_HELPER_MOCK");
        }
        
        if( isConnectException ) {
            throw new ConnectException("FROM_HELPER_MOCK");
        }
        statistic.setValue( "FROM_HELPER_MOCK" );
    }
}
