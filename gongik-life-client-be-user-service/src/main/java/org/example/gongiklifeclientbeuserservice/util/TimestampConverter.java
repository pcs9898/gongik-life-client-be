package org.example.gongiklifeclientbeuserservice.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimestampConverter {

  private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd HH:mm:ss.SSSXXX");

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

  public static Timestamp convertStringToTimestamp(String timestampString) {
    OffsetDateTime offsetDateTime = OffsetDateTime.parse(timestampString, TIMESTAMP_FORMATTER);
    return Timestamp.from(offsetDateTime.toInstant());
  }

  public static Date convertStringToDate(String dateString) {
    try {
      return dateFormat.parse(dateString);
    } catch (ParseException e) {
      throw new RuntimeException("Invalid date format. Please use yyyy-MM-dd.", e);
    }
  }
}