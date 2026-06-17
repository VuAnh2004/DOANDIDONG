package model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class KhoaBieuModel {

    @SerializedName("weekNumber") private Integer weekNumber;
    @SerializedName("dayOfWeek") private Integer dayOfWeek;
    @SerializedName("period") private Integer period;
    @SerializedName("subjectName") private String subjectName;
    @SerializedName("teacherName") private String teacherName;
    @SerializedName("startdate") private String startDate;
    @SerializedName("enddate") private String endDate;
    @SerializedName("room") private String room;

    // Constructor mặc định cho Gson
    public KhoaBieuModel() {}

    // Getters với xử lý null an toàn
    public Integer getWeekNumber() { return weekNumber != null ? weekNumber : 0; }
    public Integer getDayOfWeek() { return dayOfWeek != null ? dayOfWeek : 0; }
    public Integer getPeriod() { return period != null ? period : 0; }
    public String getSubjectName() { return subjectName != null ? subjectName : ""; }
    public String getTeacherName() { return teacherName != null ? teacherName : ""; }
    public String getStartDate() { return startDate != null ? startDate : ""; }
    public String getEndDate() { return endDate != null ? endDate : ""; }
    public String getRoom() { return room != null ? room : ""; }

    // Setters
    public void setWeekNumber(Integer weekNumber) { this.weekNumber = weekNumber; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setPeriod(Integer period) { this.period = period; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setRoom(String room) { this.room = room; }

    // ==================== NESTED CLASSES ====================

    public static class IndexResponse {
        @SerializedName("success") public boolean success;
        @SerializedName("studentInfo") public StudentInfo studentInfo;
        @SerializedName("weeksInSemester") public List<AcademicWeek> weeksInSemester;
        @SerializedName("currentWeek") public AcademicWeek currentWeek;
        @SerializedName("schedule") public List<KhoaBieuModel> schedule;
        @SerializedName("message") public String message;

        public IndexResponse() {}
        public boolean isSuccess() { return success; }
        public List<KhoaBieuModel> getSchedule() { return schedule; }
        public StudentInfo getStudentInfo() { return studentInfo; }
        public List<AcademicWeek> getWeeksInSemester() { return weeksInSemester; }
        public AcademicWeek getCurrentWeek() { return currentWeek; }

        public String getMessage() {
            return null;
        }
    }

    public static class CalendarResponse {
        @SerializedName("success") public boolean success;
        @SerializedName("weeks") public List<AcademicWeek> weeks;

        public CalendarResponse() {}
        public boolean isSuccess() { return success; }
        public List<AcademicWeek> getWeeks() { return weeks; }
    }

    public static class StudentInfo {
        @SerializedName("semesterName") public String semesterName;
        @SerializedName("className") public String className;
        @SerializedName("fullName") public String fullName;

        public StudentInfo() {}
        public String getSemesterName() { return semesterName != null ? semesterName : ""; }
        public String getClassName() { return className != null ? className : ""; }
    }

    public static class AcademicWeek {
        @SerializedName("weekNumber") public int weekNumber;
        @SerializedName("startDate") public String startDate;
        @SerializedName("endDate") public String endDate;
        @SerializedName("isCurrentWeek") public boolean currentWeek;

        public AcademicWeek() {}
        public int getWeekNumber() { return weekNumber; }
        public String getStartDate() { return startDate != null ? startDate : ""; }
        public String getEndDate() { return endDate != null ? endDate : ""; }
        public boolean isCurrentWeek() { return currentWeek; }

        public String getDisplayText() {
            return "Tuần " + weekNumber + " (" + getStartDate() + " - " + getEndDate() + ")";
        }
    }
}