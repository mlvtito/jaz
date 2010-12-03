/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.rwx.jaz;

import junit.framework.TestCase;
import net.rwx.jaz.exceptions.ZabbixJMXQueryException;

/**
 * This is JUnit test class for JazQuery.
 * 
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class JazQueryTest extends TestCase {
    
    public JazQueryTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test for a simple query
     */
    public void testSimpleQuery()
    {
        try {
            JazQuery query = new JazQuery(
                    "jmx[java.lang:type=Runtime][VmVersion]");

            assertEquals( "java.lang:type=Runtime", query.getObjectName());
            assertEquals("VmVersion", query.getAttributeName() );
            assertEquals( "VmVersion", query.getCanonicalAttributeName() );
        }catch( ZabbixJMXQueryException zjqe ) {
            fail("Query should be OK");
        }
    }

    /**
     * Test for a simple query with a composite data
     */
    public void testCompositeQuery()
    {
        try {
            JazQuery query = new JazQuery(
                    "jmx[java.lang:type=MemoryPool,name=PS Old Gen][Usage.used]"
                    );

            assertEquals( "java.lang:type=MemoryPool,name=PS Old Gen",
                    query.getObjectName() );
            assertEquals( "Usage.used", query.getAttributeName());
            assertEquals("Usage", query.getCanonicalAttributeName() );
        }catch( ZabbixJMXQueryException zjqe ) {
            fail("Query should be OK");
        }
    }

    /**
     * Test that ZabbixJMXQueryException is thrown when query does not start
     * with "jmx" prefix.
     */
    public void testBadQueryType()
    {
        try {
            JazQuery query = new JazQuery(
                    "rmt[java.lang:type=MemoryPool,name=PS Old Gen][Usage.used]"
                    );

            fail("This should throw an exception");
        }catch( ZabbixJMXQueryException zjqe ) {
            assertNotNull(zjqe);
        }
    }

    /**
     * Test that ZabbixJMXQueryException is thrown when syntax is incorrect.
     */
    public void testBadSyntax()
    {
        try {
            JazQuery query = new JazQuery(
                    "jmx[java.lang:type=MemoryPool,name=PS Old Gen[Usage.used]"
                    );

            fail("This should throw an exception");
        }catch( ZabbixJMXQueryException zjqe ) {
            assertNotNull(zjqe);
        }
    }

    /**
     * Test for query with a variable.
     */
    public void testVariableQuery()
    {
        try {
            JazQuery query = new JazQuery(
                    "jmx[java.lang:var=${1},type=Runtime][VmVersion][VAR1]");

            assertEquals( "java.lang:var=VAR1,type=Runtime",
                    query.getObjectName() );
            assertEquals("VmVersion", query.getAttributeName() );
            assertEquals( "VmVersion", query.getCanonicalAttributeName() );
        }catch( ZabbixJMXQueryException zjqe ) {
            fail("Query should be OK");
        }
    }
}
