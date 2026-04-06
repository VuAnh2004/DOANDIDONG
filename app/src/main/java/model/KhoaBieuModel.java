package model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class KhoaBieuModel {
    @SerializedName(value = "Period", alternate = {"period"})
    private Integer period;
    
    @SerializedName(value = "DayOfWeek", alternate = {"dayOfWeek"})
    private Integer dayOfWeek;
    
    @SerializedName(value = "SubjectName", alternate = {"subjectName", "Subject", "subject"})
    private String subjectName;
    
    @SerializedName(value = "TeacherName", alternate = {"teacherName", "Teacher", "teacher"})
    private String teacherName;
    
    @SerializedName(value = "WeekNumber", alternate = {"weekNumber"})
    private Integer weekNumber;

    public Integer getPeriod() { return period; }
    public Integer getDayOfWeek() { return dayOfWeek; }
    public String getSubjectName() { return subjectName; }
    public String getTeacherName() { return teacherName; }
    public Integer getWeekNumber() { return weekNumber; }

    public static class IndexResponse {
        @SerializedName("StudentInfo")
        public StudentInfo studentInfo;
        
        @SerializedName("CurrentWeek")
        public AcademicWeek currentWeek;
        
        @SerializedName("WeeksInSemester")
        public List<AcademicWeek> weeksInSemester;
        
        @SerializedName("Schedule")
        public List<KhoaBieuModel> schedule;
    }

    public static class StudentInfo {
        @SerializedName("FullName")
        public String fullName;
        @SerializedName("ClassName")
        public String className;
        @SerializedName("SemesterName")
        public String semesterName;
    }

    public static class AcademicWeek {
        @SerializedName("WeekNumber")
        public int weekNumber;
        @SerializedName("StartDate")
        public String startDate;
        @SerializedName("EndDate")
        public String endDate;
        @SerializedName("SemesterCode")
        public String semesterCode;
        @SerializedName("IsCurrentWeek")
        public boolean isCurrentWeek;
    }
}