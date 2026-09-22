package com.mehmert.hope4

import com.mehmert.hope4.ui.theme.Hope4Theme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import java.time.LocalDate
import java.time.MonthDay
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Hope4Theme {
                MainScreen()
            }
        }
    }
}

val font2Family = FontFamily(
    Font(R.font.font2)
)
val jork = Color.hsl(
    hue = 264f,
    saturation = 1f,
    lightness = .5f)
@Preview
@Composable
fun MainScreenPreview(){
    MainScreen()
}
@Composable
fun MainScreen() {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(24) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(24) } // Adjust as needed
    val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfRow
    )
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val textDate = selectedDate?.let {
        val day = when (it.dayOfMonth) {
            1 -> "1st"
            2 -> "2nd"
            3 -> "3rd"
            else -> "${it.dayOfMonth}th"
        }
        "$day of ${it.format(DateTimeFormatter.ofPattern("MMMM"))}"
    } ?: ""
    val visibleMonth by remember {
        derivedStateOf {
            state.firstVisibleMonth.yearMonth
        }
    }
    val rows = visibleMonth.calculateWeeks(DayOfWeek.MONDAY)
    val title = visibleMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    val calendarHeight  = when (rows) {
        4 -> .48f
        5 -> .6f
        6 -> .72f
        else -> .84f
    }
    Column(Modifier.background(Color.Black)){
        Spacer(modifier = Modifier.height(48.dp))
        Row {
            Text(
                title,
                fontSize = 24.sp,
                fontFamily = font2Family,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height((24).dp))
        DaysOfWeekTitle(daysOfWeek=daysOfWeek)
        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                Day(day,
                    isSelected = selectedDate == day.date,
                    size = calendarHeight,
                    plink = MonthDay.now().toString().takeLast(2),
                    plunk = currentMonth.toString(),
                    tom = day.date.toString(),
                    butch = day.date.dayOfMonth.toString()
                ) { day ->
                    selectedDate = if (selectedDate == day.date) null else day.date
                }
            },
        )
        Column {
            Text(
                textDate,
                fontFamily = font2Family,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .fillMaxHeight()
            )
        }
    }
}
@Composable
fun Day(day: CalendarDay,
        isSelected:Boolean,
        size: Float,
        plink: String,
        plunk: String,
        tom: String,
        butch: String,
        onClick: (CalendarDay) -> Unit)
{
    Box(
        modifier = Modifier
            .aspectRatio(size)
            .clickable(
                enabled = true,
                onClick = { onClick(day) }
            )
            .border(
                BorderStroke(
                    width = if (isSelected) 2.dp else Dp.Hairline,
                    color = if (isSelected) jork else if (tom == "$plunk-$plink") Color.Blue else Color.DarkGray
                )
            ),
        contentAlignment = Alignment.TopStart
    ) {
        Column {
            Text(
                text = butch,
                fontFamily = font2Family,
                color = if (day.position == DayPosition.MonthDate) Color.White else Color.Gray,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
            )
        }
    }
}
@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                fontFamily = font2Family,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 4.dp, start = 5.dp),
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.current.platformLocale),
                color = Color.White
            )
        }
    }
}

fun YearMonth.calculateWeeks(firstDayOfWeek: DayOfWeek): Int {
    val firstDayOfMonth = this.atDay(1)

    val leadingDays =
        (7 + (firstDayOfMonth.dayOfWeek.value - firstDayOfWeek.value)) % 7

    val totalDays = this.lengthOfMonth()

    return ceil((leadingDays + totalDays) / 7.0).toInt()
}

