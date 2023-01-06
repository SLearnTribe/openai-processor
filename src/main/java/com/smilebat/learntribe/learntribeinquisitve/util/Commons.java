package com.smilebat.learntribe.learntribeinquisitve.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * General Purpose Utility functions
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Slf4j
@Component
public class Commons {
  public static final String PATTERN_FORMAT = "yyyy-MM-dd";
  private final SimpleDateFormat dateFormatter = new SimpleDateFormat(PATTERN_FORMAT);
  private final DateTimeFormatter instantFormatter =
      DateTimeFormatter.ofPattern(PATTERN_FORMAT).withZone(ZoneId.systemDefault());

  /**
   * Converts input date string to {@link Instant}.
   *
   * @param date the input
   * @return the {@link Instant}
   */
  // public Function<String,Instant> toInstant = (dateStr) ->
  // dateFormatter.parse(dateStr).toInstant();
  public Instant toInstant(String date) {
    try {
      return dateFormatter.parse(date).toInstant();
    } catch (ParseException e) {
      log.info("Unable to parse the input date {}", date);
      throw new RuntimeException(e);
    }
  }

  /**
   * Converts {@link Instant} to String format.
   *
   * @param instant the date from entity.
   * @return the string representation.
   */
  public Function<Instant, String> formatInstant = (instant) -> instantFormatter.format(instant);

  @Override
  public String toString() {
    return "Commons{"
        + "dateFormatter="
        + dateFormatter
        + ", instantFormatter="
        + instantFormatter
        + ", formatInstant="
        + formatInstant
        + '}';
  }
}
