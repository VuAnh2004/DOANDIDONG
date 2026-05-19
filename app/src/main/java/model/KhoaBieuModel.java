package model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class KhoaBieuModel {
    @SerializedName(value = "period", alternate = {"Period", "tiet_bat_dau", "TietBD", "tiet_bd", "tiet"})
    private Integer period;

    @SerializedName(value = "so_tiet", alternate = {"SoTiet", "Duration", "so_tiet_hoc", "So_Tiet", "tiet_kt"})
    private Integer so_tiet;
    
    @SerializedName(value = "dayOfWeek", alternate = {"DayOfWeek", "thu", "Thu", "thu_trong_tuan"})
    private Integer dayOfWeek;
    
    @SerializedName(value = "subjectName", alternate = {"SubjectName", "ten_mon_hoc", "subject", "Subject", "ten_mon"})
    private String subjectName;
    
    @SerializedName(value = "teacherName", alternate = {"TeacherName", "giang_vien", "teacher", "Teacher", "ten_gv"})
    private String teacherName;
    
    @SerializedName(value = "weekNumber", alternate = {"WeekNumber", "tuan", "tuan_hoc", "Tuan", "tuan_day"})
    private String weekNumber; 

    @SerializedName(value = "location", alternate = {"Location", "phong_hoc", "dia_diem", "Phong", "PhongHoc"})
    private String location;

    @SerializedName(value = "trainingSystem", alternate = {"TrainingSystem", "he_dao_tao", "HeDaoTao"})
    private String trainingSystem;

    @SerializedName(value = "courseCode", alternate = {"CourseCode", "ma_hoc_phan", "hoc_phan", "MaMon"})
    private String courseCode;

    public Integer getPeriod() { return period != null ? period : 0; }
    public Integer getSoTiet() { return so_tiet != null ? so_tiet : 1; }
    public Integer getDayOfWeek() { return dayOfWeek != null ? dayOfWeek : 0; }
    public String getSubjectName() { return subjectName != null ? subjectName : "Chưa có tên môn"; }
    public String getTeacherName() { return teacherName != null ? teacherName : "N/A"; }
    public String getWeekNumberString() { return weekNumber; }
    public String getLocation() { return location != null ? location : "Đang cập nhật"; }
    public String getTrainingSystem() { return trainingSystem != null ? trainingSystem : "Hệ Đại học chính quy"; }
    public String getCourseCode() { return courseCode != null ? courseCode : subjectName; }

    public static class IndexResponse {
        @SerializedName(value = "studentInfo", alternate = {"StudentInfo", "info"})
        public StudentInfo studentInfo;
        
        @SerializedName(value = "currentWeek", alternate = {"CurrentWeek", "tuan_hien_tai"})
        public AcademicWeek currentWeek;
        
        @SerializedName(value = "weeksInSemester", alternate = {"WeeksInSemester", "danh_sach_tuan"})
        public List<AcademicWeek> weeksInSemester;
        
        @SerializedName(value = "schedule", alternate = {"Schedule", "danh_sach_tkb", "data", "list", "TKB"})
        public List<KhoaBieuModel> schedule;
    }

    public static class StudentInfo {
        public String fullName;
        public String className;
        public String semesterName;
    }

    public static class AcademicWeek {
        public int weekNumber;
        public String startDate;
        public String endDate;
        public boolean isCurrentWeek;
    }
}