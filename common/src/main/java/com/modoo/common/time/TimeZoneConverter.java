package com.modoo.common.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;

public final class TimeZoneConverter {

  private TimeZoneConverter() {
  }

  public static ZoneId parseOrUtc(String timeZone) {
    if (Objects.isNull(timeZone) || timeZone.isBlank()) {
      return ZoneOffset.UTC;
    }

    return ZoneId.of(timeZone);
  }

  public static DateTimeRange toUtcDateRange(LocalDate clientDate, ZoneId clientZone) {
    return toUtcRange(clientDate, LocalTime.MIN, LocalTime.MAX, clientZone);
  }

  public static DateTimeRange toUtcRange(
      LocalDate clientDate,
      LocalTime startTime,
      LocalTime endTime,
      ZoneId clientZone
  ) {
    LocalDate date = Objects.requireNonNull(clientDate);
    ZoneId zone = Objects.requireNonNull(clientZone);
    ZonedDateTime start = date.atTime(Objects.requireNonNull(startTime))
        .atZone(zone)
        .withZoneSameInstant(ZoneOffset.UTC);
    ZonedDateTime end = date.atTime(Objects.requireNonNull(endTime))
        .atZone(zone)
        .withZoneSameInstant(ZoneOffset.UTC);

    return new DateTimeRange(start.toLocalDateTime(), end.toLocalDateTime());
  }

  public static boolean isInRange(LocalDate scheduledOn, LocalTime beginAt, DateTimeRange range) {
    if (Objects.isNull(beginAt)) {
      return !scheduledOn.isBefore(range.startDate()) && !scheduledOn.isAfter(range.endDate());
    }

    LocalDateTime scheduledAt = scheduledOn.atTime(beginAt);
    return !scheduledAt.isBefore(range.start()) && !scheduledAt.isAfter(range.end());
  }

}
