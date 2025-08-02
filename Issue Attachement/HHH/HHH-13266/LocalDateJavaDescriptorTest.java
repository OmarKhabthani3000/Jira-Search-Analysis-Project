package org.hibernate.type.descriptor.java;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalDateJavaDescriptorTest {

  private LocalDateJavaDescriptor ldd = LocalDateJavaDescriptor.INSTANCE;

  @Test
  public void wrap_null() {
    assertThat(ldd.wrap(null, null)).isNull();
  }

  @Test
  public void wrap_localDate() {
    assertThat(ldd.wrap(LocalDate.of(1600, Month.JANUARY, 1), null))
        .isEqualTo(LocalDate.of(1600, Month.JANUARY, 1));
  }

  @Test
  public void wrap_date() {
    assertThat(ldd.wrap(getDate(1600, 1, 1), null))
        .isEqualTo(LocalDate.of(1600, Month.JANUARY, 1));
  }

  @Test
  public void wrap_sqlDate() {
    assertThat(ldd.wrap(getSqlDate(1600, 1, 1), null))
        .isEqualTo(LocalDate.of(1600, Month.JANUARY, 1));
  }

  @Test
  public void wrap_timestamp() {
    assertThat(ldd.wrap(getTimestamp(1600, 1, 1), null))
        .isEqualTo(LocalDate.of(1600, Month.JANUARY, 1));
  }

  @Test
  public void wrap_calendar() {
    assertThat(ldd.wrap(getCalendar(1600, 1, 1), null))
        .isEqualTo(LocalDate.of(1600, Month.JANUARY, 1));
  }

  @Test
  public void wrap_long() {
    assertThat(ldd.wrap(getDateAsLong(1600, 1, 1), null))
        .isEqualTo(LocalDate.of(1600, Month.JANUARY, 1));
  }

  @Test
  public void unwrap_null() {
    assertThat(ldd.unwrap(null, Date.class, null)).isNull();
  }

  @Test
  public void unwrap_localDate() {
    assertThat(ldd.unwrap(LocalDate.of(1600, Month.JANUARY, 1), LocalDate.class, null))
        .isEqualTo(LocalDate.of(1600, Month.JANUARY, 1));
  }

  @Test
  public void unwrap_date() {
    assertThat(ldd.unwrap(LocalDate.of(1600, Month.JANUARY, 1), Date.class, null))
        .isEqualTo(getDate(1600, 1, 1));
  }

  @Test
  public void unwrap_sqlDate() {
    java.sql.Date result = ldd.unwrap(LocalDate.of(1600, Month.JANUARY, 1), java.sql.Date.class, null);

    SoftAssertions softly = new SoftAssertions();
    softly.assertThat(result).isInstanceOf(java.sql.Date.class);
    softly.assertThat(result)
        .isEqualTo(getSqlDate(1600, 1, 1));
    softly.assertAll();
  }

  @Test
  public void unwrap_timestamp() {
    assertThat(ldd.unwrap(LocalDate.of(1600, Month.JANUARY, 1), Timestamp.class, null))
        .isEqualTo(getTimestamp(1600, 1, 1));
  }

  @Test
  public void unwrap_calendar() {
    assertThat(ldd.unwrap(LocalDate.of(1600, Month.JANUARY, 1), Calendar.class, null))
        .isEqualTo(getCalendar(1600, 1, 1));
  }

  @Test
  public void unwrap_long() {
    assertThat(ldd.unwrap(LocalDate.of(1600, Month.JANUARY, 1), Long.class, null))
        .isEqualTo(getDateAsLong(1600, 1, 1));
  }

  private Date getDate(int year, int month, int day) {
    return new Date(year - 1900, month - 1, day);
  }

  private java.sql.Date getSqlDate(int year, int month, int day) {
    return new java.sql.Date(year - 1900, month - 1, day);
  }

  private Timestamp getTimestamp(int year, int month, int day) {
    return new Timestamp(year - 1900, month - 1, day, 0 ,0, 0, 0);
  }

  private Long getDateAsLong(int year, int month, int day) {
    return getDate(year, month, day).getTime();
  }

  private Calendar getCalendar(int year, int month, int day) {
    return new GregorianCalendar(year, month - 1, day);
  }

}