package org.example.gongiklifeclientbeauthservice.utils;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampConverter {

  private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd HH:mm:ss.SSSXXX");

  public static Timestamp convertStringToTimestamp(String timestampString) {
    OffsetDateTime offsetDateTime = OffsetDateTime.parse(timestampString, TIMESTAMP_FORMATTER);
    return Timestamp.from(offsetDateTime.toInstant());
  }
}