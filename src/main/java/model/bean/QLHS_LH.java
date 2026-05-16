package model.bean;

import java.util.Objects;

public class QLHS_LH {

    private int HocSinhLopHocID;
    private String StudentID; // tương ứng QLHocSinh
    private int ClassID;
    private Boolean IsActive;

    private Integer SemesterID; // có thể null
    private Integer CourseID;   // có thể null

    // Quan hệ
    private QLHocSinh hocsinh;  // học sinh
    private QLHocKy hocKy;      // học kỳ
    private QLKhoaHoc khoaHoc;  // khóa học
    private QLLopHoc lopHoc;    // lớp học

    // Getter & Setter
    public int getHocSinhLopHocID() { return HocSinhLopHocID; }
    public void setHocSinhLopHocID(int hocSinhLopHocID) { HocSinhLopHocID = hocSinhLopHocID; }

    public String getStudentID() { return StudentID; }
    public void setStudentID(String studentID) { StudentID = studentID; }

    public int getClassID() { return ClassID; }
    public void setClassID(int classID) { ClassID = classID; }

    public Boolean getIsActive() { return IsActive; }
    public void setIsActive(Boolean isActive) { IsActive = isActive; }

    public Integer getSemesterID() { return SemesterID; }
    public void setSemesterID(Integer semesterID) { SemesterID = semesterID; }

    public Integer getCourseID() { return CourseID; }
    public void setCourseID(Integer courseID) { CourseID = courseID; }

    public QLHocSinh getHocsinh() { return hocsinh; }
    public void setHocsinh(QLHocSinh hocsinh) { this.hocsinh = hocsinh; }

    public QLHocKy getHocKy() { return hocKy; }
    public void setHocKy(QLHocKy hocKy) { this.hocKy = hocKy; }

    public QLKhoaHoc getKhoaHoc() { return khoaHoc; }
    public void setKhoaHoc(QLKhoaHoc khoaHoc) { this.khoaHoc = khoaHoc; }

    public QLLopHoc getLopHoc() { return lopHoc; }
    public void setLopHoc(QLLopHoc lopHoc) { this.lopHoc = lopHoc; }

    @Override
    public String toString() {
        return "QLHocSinhLopHoc{" +
                "HocSinhLopHocID=" + HocSinhLopHocID +
                ", StudentID='" + StudentID + '\'' +
                ", ClassID=" + ClassID +
                ", IsActive=" + IsActive +
                ", SemesterID=" + SemesterID +
                ", CourseID=" + CourseID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QLHS_LH)) return false;
        QLHS_LH that = (QLHS_LH) o;
        return HocSinhLopHocID == that.HocSinhLopHocID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(HocSinhLopHocID);
    }
}
