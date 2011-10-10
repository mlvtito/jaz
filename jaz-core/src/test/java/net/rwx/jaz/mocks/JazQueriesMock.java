/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jaz.mocks;

import net.rwx.jaz.JazQueries;
import net.rwx.jaz.JazQuery;
import net.rwx.jaz.JazQueryStatistic;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class JazQueriesMock extends JazQueries {
    
    private JazQueryStatistic statistic;
    
    public void setStatistic( JazQueryStatistic stat )
    {
        this.statistic = stat;
    }
    
    public JazQueryStatistic get(JazQuery query)
    {
        return statistic;
    }
}
