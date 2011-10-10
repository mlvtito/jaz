/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jaz;

import junit.framework.TestCase;
import net.rwx.jaz.mocks.JazHelperMock;
import net.rwx.jaz.mocks.JazQueriesMock;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class JazAgentTest extends TestCase {
    
     public JazAgentTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    private JazQuery getQuery() throws Exception {
        return new JazQuery( "jmx[java.lang:type=Runtime][VmVersion]" );
    }
    
    private JazQueryStatistic getStatistic( long value, long get ) {
        JazQueryStatistic stat = new JazQueryStatistic();
        
        stat.setTimeLastValue( value );
        stat.setTimeLastGet( get );
        stat.setValue( "INITIAL" );
        
        return stat;
    }
    
    public void testNewValue()
    {
        JazQueryStatistic statistic = getStatistic( 100, 200 );

        JazHelperMock helper = new JazHelperMock( null );
        helper.setStatistic( statistic );

        JazQueriesMock queries = new JazQueriesMock();
        queries.setStatistic( statistic );

        JazAgent agent = new JazAgent( helper, queries );
        String ret = agent.execute( "jmx[java.lang:type=Runtime][VmVersion]" );
        assertEquals("FROM_HELPER_MOCK", ret);
    }
    
    public void testOldValue()
    {
        JazQueryStatistic statistic = getStatistic( 200, 100 );
        
        JazHelperMock helper = new JazHelperMock( null );
        helper.setStatistic( statistic );

        JazQueriesMock queries = new JazQueriesMock();
        queries.setStatistic( statistic );
        
        JazAgent agent = new JazAgent( helper, queries );
        String ret = agent.execute( "jmx[java.lang:type=Runtime][VmVersion]" );
        assertEquals("INITIAL", ret);
    }
    
    public void testConnectException()
    {
        JazQueryStatistic statistic = getStatistic( 100, 200 );
        
        JazHelperMock helper = new JazHelperMock( null );
        helper.setStatistic( statistic );
        helper.setConnectException(true);

        JazQueriesMock queries = new JazQueriesMock();
        queries.setStatistic( statistic );
        
        JazAgent agent = new JazAgent( helper, queries );
        String ret = agent.execute( "jmx[java.lang:type=Runtime][VmVersion]" );
        assertEquals("", ret);
    }
    
    public void testException()
    {
        JazQueryStatistic statistic = getStatistic( 100, 200 );
        
        JazHelperMock helper = new JazHelperMock( null );
        helper.setStatistic( statistic );
        helper.setException(true);

        JazQueriesMock queries = new JazQueriesMock();
        queries.setStatistic( statistic );
        
        JazAgent agent = new JazAgent( helper, queries );
        String ret = agent.execute( "jmx[java.lang:type=Runtime][VmVersion]" );
        assertEquals("ZBX_NOTSUPPORTED", ret);
    }
}
