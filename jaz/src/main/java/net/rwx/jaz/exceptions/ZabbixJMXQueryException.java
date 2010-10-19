package net.rwx.jaz.exceptions;

public class ZabbixJMXQueryException extends ZabbixJMXException
{
  private String query;
  private String message;

  public ZabbixJMXQueryException(String query, String message)
  {
    this.query = query;
    this.message = message;
  }

  public String getMessage() {
    StringBuffer strbuf = new StringBuffer();
    strbuf.append(this.message);
    strbuf.append(" [");
    strbuf.append(this.query);
    strbuf.append("]");

    return strbuf.toString();
  }
}
