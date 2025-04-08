package com.server.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthAttendance {
    private String month;
    private List<DailyAttendance> attendence;
    private int totalPresent;
}
