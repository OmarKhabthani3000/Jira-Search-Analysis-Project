package org.hibernate.type.descriptor.java;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalDateTimeJavaDescriptorTest {

    private LocalDateTimeJavaDescriptor ldtd = LocalDateTimeJavaDescriptor.INSTANCE;

    @Test
    public void unwrap_null() {
        assertThat(ldtd.unwrap(null, Timestamp.class, null)).isNull();
    }

    @Test
    public void unwrap_localDateTime() {
        assertThat(ldtd.unwrap(LocalDateTime.of(1600, Month.JANUARY, 1, 0, 0, 5), LocalDateTime.class, null))
            .isEqualTo(LocalDateTime.of(1600, Month.JANUARY, 1, 0, 0, 5));
    }

    @Test
    public void unwrap_timestamp() {
        assertThat(ldtd.unwrap(LocalDateTime.of(1600, Month.JANUARY, 1, 0, 0, 5), Timestamp.class, null))
                .isEqualTo(getTimestamp(1600, 1, 1, 0, 0, 5));
    }

    @Test
    public void unwrap_calendar() {
        assertThat(ldtd.unwrap(LocalDateTime.of(1600, Month.JANUARY, 1, 0, 0, 5), Calendar.class, null))
                .isEqualTo(getCalendar(1600, 1, 1, 0, 0, 5));
    }

    @Test
    public void unwrap_date() {
        assertThat(ldtd.unwrap(LocalDateTime.of(1600, Month.JANUARY, 1, 0, 0, 5), Date.class, null))
                .isEqualTo(getDate(1600, 1, 1, 0, 0, 5));
    }

    @Test
    public void unwrap_sqlDate() {
        java.sql.Date result = ldtd
            .unwrap(LocalDateTime.of(1600, Month.JANUARY, 1, 0, 0, 5), java.sql.Date.class, null);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result).isInstanceOf(java.sql.Date.class);
        softly.assertThat(result).isEqualTo(getSqlDate(1600, 1, 1, 0, 0, 5));
        softly.assertAll();
    }

    @Test
    public void unwrap_long() {
        assertThat(ldtd.unwrap(LocalDateTime.of(1600, Month.JANUARY, 1, 0, 0, 5), Long.class, null))
                .isEqualTo(getTimestampAsLong(1600, 1, 1, 0, 0, 5));
    }

    @Test
    public void wrap_null() {
        assertThat(ldtd.wrap(null, null)).isNull();
    }

    @Test
    public void wrap_localDateTime() {
        assertThat(ldtd.wrap(LocalDateTime.of(1600, Month.JANUARY, 1, 0, 0, 5), null))
            .isEqualTo(LocalDateTime.of(1600, Month.JANUARY, 1, 0, 0, 5));
    }

    @Test
    public void wrap_timestamp() {
        assertThat(ldtd.wrap(getTimestamp(1600, 1, 1, 0, 0, 5), null))
                .isEqualTo(LocalDateTime.of(1600, Month.JANUARY, 1, 0, 0, 5));
    }

    @Test
    public void wrap_calendar() {
        assertThat(ldtd.wrap(getCalendar(1600, 1, 1, 0, 0, 5), null))
                .isEqualTo(LocalDateTime.of(1600, Month.JANUARY, 1, 0, 0, 5));
    }

    @Test
    public void wrap_date() {
        assertThat(ldtd.wrap(getDate(1600, 1, 1, 0, 0, 5), null))
                .isEqualTo(LocalDateTime.of(1600, Month.JANUARY, 1, 0, 0, 5));
    }

    @Test
    public void wrap_sqlDate() {
        assertThat(ldtd.wrap(getSqlDate(1600, 1, 1, 0, 0, 5), null))
                .isEqualTo(LocalDateTime.of(1600, Month.JANUARY, 1, 0, 0, 5));
    }

    @Test
    public void wrap_long() {
        assertThat(ldtd.wrap(getTimestampAsLong(1600, 1, 1, 0, 0, 5), null))
                .isEqualTo(LocalDateTime.of(1600, Month.JANUARY, 1, 0, 0, 5));
    }

    private Date getDate(int year, int month, int day, int hours, int min, int sec) {
        return new Date(year - 1900, month - 1, day, hours, min, sec);
    }

    private Calendar getCalendar(int year, int month, int day, int hours, int min, int sec) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(getDate(year, month, day, hours, min, sec));

        System.out.println(calendar);

        return calendar;
    }

    private Long getTimestampAsLong(int year, int month, int day, int hours, int min, int sec) {
        return getDate(year, month, day, hours, min, sec).getTime();
    }

    private java.sql.Date getSqlDate(int year, int month, int day, int hours, int min, int sec) {
        return new java.sql.Date(getTimestampAsLong(year, month, day, hours, min, sec));
    }

    private Timestamp getTimestamp(int year, int month, int day, int hours, int min, int sec) {
        return new Timestamp(year - 1900, month - 1, day, hours, min, sec, 0);
    }

}