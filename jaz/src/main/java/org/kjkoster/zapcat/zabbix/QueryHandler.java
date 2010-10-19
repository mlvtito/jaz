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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import net.rwx.jaz.JazAgent;
import net.rwx.jaz.JazHelper;
import net.rwx.jaz.JazQueries;
import org.apache.log4j.Logger;

/**
 * A JMX query handler for Zabbix. The query handler reads the query from the
 * socket, parses the request and constructs and sends a response.
 * <p>
 * You can configure the protocol version to use and set it to either
 * &quot;1.1&quot; or &quot;1.4&quot;.
 *
 * @author Kees Jan Koster &lt;kjkoster@kjkoster.org&gt;
 */
final class QueryHandler implements Runnable
{
  private static final Logger log = Logger.getLogger(QueryHandler.class);

  private final Socket socket;
  private final StringBuffer hexdump = new StringBuffer();

  /**
   * The return value that Zabbix interprets as the agent not supporting the
   * item.
   */
  private static final String NOTSUPPORTED = "ZBX_NOTSUPPORTED";

  /** The JMX Helper that is used with this agent. */
  private JazHelper jmxHelper;

  /** The agent that keep track of zabbix requests. */
  private JazAgent agent;

  /**
   * Create a new query handler.
   *
   * @param socket
   *            The socket that was accepted.
   */
  public QueryHandler(Socket socket, JazHelper helper, JazQueries manager)
  {
    this.socket = socket;
    this.jmxHelper = helper;
    this.agent = new JazAgent(this.jmxHelper, manager);
  }

  /**
   * @see java.lang.Runnable#run()
   */
  public void run()
  {
    try
    {
      try {
        handleQuery();
        if (this.socket.getInputStream().available() > 0);
      }
      finally {
        if (this.socket != null)
          this.socket.close();
      }
    }
    catch (Exception e)
    {
      log.error("dropping exception", e);
    }
  }

  private void handleQuery() throws Exception
  {
    String request = receive(this.socket.getInputStream());

    String response = response(request);
    log.info("{" + request + "} => {" + response + "}");

    if (response == null) {
      response = "";
    }

    send(response, this.socket.getOutputStream());
  }

  private String receive(InputStream in) throws IOException
  {
    String line = "";
    int b = in.read();
    while ((b != -1) && (b != 10)) {
      line = line + (char)b;

      b = in.read();
    }

    return line;
  }

  private String response(String query) throws Exception
  {
    int lastOpen = query.lastIndexOf(91);
    int lastClose = query.lastIndexOf(93);
    String attribute = null;
    if ((lastOpen >= 0) && (lastClose >= 0)) {
      attribute = query.substring(lastOpen + 1, lastClose);
    }

    if (query.startsWith("jmx"))
      return this.agent.execute(query);
    if (query.startsWith("system.property"))
      return querySystemProperty(attribute);
    if (query.startsWith("system.env"))
      return queryEnvironment(attribute);
    if (query.equals("agent.ping"))
      return "1";
    if (query.equals("agent.version")) {
      return "JaZ 1.1";
    }

    return "ZBX_NOTSUPPORTED";
  }

  /*
   * This method will go away once I have added collection support to the
   * query handler.
   */
  private String querySystemProperty( String key )
  {
    return System.getProperty(key);
  }

  private String queryEnvironment(String key)
  {
    return System.getenv(key);
  }

  private void send(String response, OutputStream outputStream) throws IOException
  {
    BufferedOutputStream out = new BufferedOutputStream(outputStream);

    if( isProtocol14() ) {
      write(out, (byte)90 );
      write(out, (byte)66 );
      write(out, (byte)88 );
      write(out, (byte)68 );

      write(out, (byte)1 );

      long length = response.length();
      for (int i = 0; i < 8; ++i) {
        write(out, (byte)(int)(length & 0xFF));

        length >>= 8;
      }

    }

    for (int i = 0; i < response.length(); ++i) {
      write(out, (byte)response.charAt(i));
    }

    out.flush();
  }

  private boolean isProtocol14()
  {
    String protocolProperty = System.getProperty("org.kjkoster.zapcat.zabbix.protocol");

    if ((protocolProperty == null) || ("1.4".equals(protocolProperty))) {
      return true;
    }

    return (!("1.1".equals(protocolProperty)));
  }

  private void write(BufferedOutputStream out, byte b) throws IOException
  {
    String hex = Integer.toHexString(b);
    if (hex.length() < 2) {
      this.hexdump.append("0");
    }
    
    this.hexdump.append(hex).append(" ");

    out.write(b);
  }
}
