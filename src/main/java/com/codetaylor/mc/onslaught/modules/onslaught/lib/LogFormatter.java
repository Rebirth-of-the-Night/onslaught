package com.codetaylor.mc.onslaught.modules.onslaught.lib;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter
    extends Formatter {

  private static final String LOG_FORMAT = "%1$s %2$s%n%4$s: %5$s%6$s%n";
  SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss.SSS]");
  private final Date date = new Date();

  @Override
  public synchronized String format(LogRecord record) {

    this.date.setTime(record.getMillis());
    String source;

    if (record.getSourceClassName() != null) {
      source = record.getSourceClassName();

      if (record.getSourceMethodName() != null) {
        source += " " + record.getSourceMethodName();
      }

    } else {
      source = record.getLoggerName();
    }

    String message = formatMessage(record);
    String throwable = "";

    if (record.getThrown() != null) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      pw.println();
      record.getThrown().printStackTrace(pw);
      pw.close();
      throwable = sw.toString();
    }

    return String.format(
        LOG_FORMAT,
        this.dateFormat.format(this.date),
        source,
        record.getLoggerName(),
        record.getLevel(),
        message,
        throwable
    );
  }
}
