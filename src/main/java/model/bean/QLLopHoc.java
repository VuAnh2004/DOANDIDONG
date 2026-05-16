package model.bean;

public class QLLopHoc {
    private int classID;
    private String className;
    private int gradeLevelId;
    private Integer courseID;
    private int maxStudents;
    private Integer currentStudents;
    private String schoolYear;
    private boolean isActive;

    // Quan hệ
    private QLKhoi khois;        // Khối
    private QLKhoaHoc khoaHoc;   // Khóa học

    // Getter & Setter
    public int getClassID() { return classID; }
    public void setClassID(int classID) { this.classID = classID; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public int getGradeLevelId() { return gradeLevelId; }
    public void setGradeLevelId(int gradeLevelId) { this.gradeLevelId = gradeLevelId; }

    public Integer getCourseID() { return courseID; }
    public void setCourseID(Integer courseID) { this.courseID = courseID; }

    public int getMaxStudents() { return maxStudents; }
    public void setMaxStudents(int maxStudents) { this.maxStudents = maxStudents; }

    public Integer getCurrentStudents() { return currentStudents; }
    public void setCurrentStudents(Integer currentStudents) { this.currentStudents = currentStudents; }

    public String getSchoolYear() { return schoolYear; }
    public void setSchoolYear(String schoolYear) { this.schoolYear = schoolYear; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public QLKhoi getKhois() { return khois; }
    public void setKhois(QLKhoi khois) { this.khois = khois; }

    public QLKhoaHoc getKhoaHoc() { return khoaHoc; }
    public void setKhoaHoc(QLKhoaHoc khoaHoc) { this.khoaHoc = khoaHoc; }
}
