package org.kjkoster.zapcat.zabbix;

/* This file is part of Zapcat.
 *
 * Zapcat is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Zapcat is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Zapcat. If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
/*import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;*/
import net.rwx.jaz.JazHelper;
import net.rwx.jaz.JazQueries;
import org.kjkoster.zapcat.Agent;

/* This file is part of Zapcat.
 *
 * Zapcat is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Zapcat is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Zapcat. If not, see <http://www.gnu.org/licenses/>.
 */
public final class ZabbixAgent implements Agent, Runnable
{
  private JazQueries queriesManager;

   /**
    * The default port that the Zapcat agents listens on.
    */
  public static final int DEFAULT_PORT = 10052;

  /**
   * The property key indicating the port number.
   */
  public static final String PORT_PROPERTY = "org.kjkoster.zapcat.zabbix.port";

  /**
   * The property key indicating the bind address.
   */
  public static final String ADDRESS_PROPERTY = "org.kjkoster.zapcat.zabbix.address";

  /**
   * The property key indicating the protocol version to use.
   */
  public static final String PROTOCOL_PROPERTY = "org.kjkoster.zapcat.zabbix.protocol";

  // the address to bind to (or 'null' to bind to any available interface).
  private final InetAddress address;

  // the port to bind to.
  private final int port;
  private final Thread daemon;
  private ServerSocket serverSocket;
  private volatile boolean stopping;
  private JazHelper jmxHelper;

  /**
   * Configure a new Zabbix agent. Each agent needs the local port number to
   * run. This constructor configures the port number by checking for a system
   * property named "org.kjkoster.zapcat.zabbix.port". If it is not there, the
   * port number defaults to 10052.
   * <p>
   * An optional address on which the agent will listen can be specified by
   * setting a property named "org.kjkoster.zapcat.zabbix.address". If this
   * property is not set, this Zabbix agent will listen on any available
   * address.
   */
  public ZabbixAgent(JazHelper helper)
  {
    this(null, 10052, helper);
  }

  /**
   * Configure a new Zabbix agent to listen on a specific address and port
   * number.
   * <p>
   * Use of this method is discouraged, since it places address and port
   * number configuration in the hands of developers. That is a task that
   * should normally be left to system administrators and done through system
   * properties.
   *
   * @param address
   *            The address to listen on, or 'null' to listen on any available
   *            address.
   * @param port
   *            The port number to listen on.
   */
  public ZabbixAgent(InetAddress address, int port, JazHelper helper)
  {
    this.queriesManager = new JazQueries();

    this.serverSocket = null;

    this.stopping = false;

    String propertyAddress = System.getProperty("org.kjkoster.zapcat.zabbix.address");
    InetAddress resolved = null;
    if (propertyAddress != null)
      try {
        resolved = InetAddress.getByName(propertyAddress);
      }
      catch (UnknownHostException e)
      {
      }
    this.address = ((resolved == null) ? address : resolved);

    String propertyPort = System.getProperty("org.kjkoster.zapcat.zabbix.port");
    this.port = ((propertyPort == null) ? port : Integer.parseInt(propertyPort));

    this.daemon = new Thread(this, "Zabbix-agent");
    this.daemon.setDaemon(true);

    this.jmxHelper = helper;
  }

  public void start() {
    this.daemon.start();
  }

  /**
   * Stop and clean up the used resources.
   *
   * @see org.kjkoster.zapcat.Agent#stop()
   */
  public void stop()
  {
    this.stopping = true;
    try
    {
      if (this.serverSocket != null) {
        this.serverSocket.close();
        this.serverSocket = null;
      }
    }
    catch (IOException e)
    {
    }
    try {
      this.daemon.join();
    }
    catch (InterruptedException e)
    {
    }
  }

  /**
   * @see java.lang.Runnable#run()
   */
  public void run()
  {
    
      // TODO : I should find a way to limit thread exceution like this line code 
      // but for jdk 1.4
      // ExecutorService handlers = new ThreadPoolExecutor(1, 5, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
    try
    {
      this.serverSocket = new ServerSocket(this.port, 0, this.address);

      while (!(this.stopping)) {
        Socket accepted = this.serverSocket.accept();

        Thread t = new Thread( new QueryHandler(accepted, this.jmxHelper, this.queriesManager) );
        t.start();
        //handlers.execute(new QueryHandler(accepted, this.jmxHelper, this.queriesManager));
      }
    } catch (IOException e) {
      if (this.stopping);
    }
    finally
    {
      try {
        if (this.serverSocket != null) {
          this.serverSocket.close();
          this.serverSocket = null;
        }
      }
      catch (IOException e)
      {
      }
     /* try {
        handlers.shutdown();
        handlers.awaitTermination(10L, TimeUnit.SECONDS);
      }
      catch (InterruptedException e)
      {
      }*/
    }
  }

 
}
