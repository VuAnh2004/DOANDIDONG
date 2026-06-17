package model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class KhoaBieuModel {
    @SerializedName(value = "period", alternate = {"Period"})
    private Integer period;
    
    @SerializedName(value = "dayOfWeek", alternate = {"DayOfWeek"})
    private Integer dayOfWeek;
    
    @SerializedName(value = "subjectName", alternate = {"SubjectName", "subject", "Subject"})
    private String subjectName;
    
    @SerializedName(value = "teacherName", alternate = {"TeacherName", "teacher", "Teacher"})
    private String teacherName;
    
    @SerializedName(value = "weekNumber", alternate = {"WeekNumber"})
    private Integer weekNumber;

    public Integer getPeriod() { return period; }
    public Integer getDayOfWeek() { return dayOfWeek; }
    public String getSubjectName() { return subjectName; }
    public String getTeacherName() { return teacherName; }
    public Integer getWeekNumber() { return weekNumber; }

    public static class IndexResponse {
        @SerializedName(value = "studentInfo", alternate = {"StudentInfo"})
        public StudentInfo studentInfo;
        
        @SerializedName(value = "currentWeek", alternate = {"CurrentWeek"})
        public AcademicWeek currentWeek;
        
        @SerializedName(value = "weeksInSemester", alternate = {"WeeksInSemester"})
        public List<AcademicWeek> weeksInSemester;
        
        @SerializedName(value = "schedule", alternate = {"Schedule"})
        public List<KhoaBieuModel> schedule;
    }

    public static class StudentInfo {
        @SerializedName(value = "fullName", alternate = {"FullName"})
        public String fullName;
        @SerializedName(value = "className", alternate = {"ClassName"})
        public String className;
        @SerializedName(value = "semesterName", alternate = {"SemesterName"})
        public String semesterName;
    }

    public static class AcademicWeek {
        @SerializedName(value = "weekNumber", alternate = {"WeekNumber"})
        public int weekNumber;
        @SerializedName(value = "startDate", alternate = {"StartDate"})
        public String startDate;
        @SerializedName(value = "endDate", alternate = {"EndDate"})
        public String endDate;
        @SerializedName(value = "semesterCode", alternate = {"SemesterCode"})
        public String semesterCode;
        @SerializedName(value = "isCurrentWeek", alternate = {"IsCurrentWeek"})
        public boolean isCurrentWeek;
    }
}