package pl.edu.ur.dc131419.manageandsave.util

import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
data class MonthRange(val start: Long, val end: Long)

fun currentMonthRange(zoneId: ZoneId = ZoneId.systemDefault()): MonthRange {
    val startDate = LocalDate.now(zoneId).withDayOfMonth(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
    val endDate = LocalDate.now(zoneId).withDayOfMonth(1).plusMonths(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
    return MonthRange(start = startDate, end = endDate)
}
fun monthRangeForDate(date: Date, zoneId: ZoneId = ZoneId.systemDefault()): MonthRange {
    val localDate = date.toInstant().atZone(zoneId).toLocalDate()
    val startDate = localDate.withDayOfMonth(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
    val endDate = localDate.withDayOfMonth(1).plusMonths(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
    return MonthRange(start = startDate, end = endDate)
}