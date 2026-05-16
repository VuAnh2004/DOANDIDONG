package model.bean;

import java.util.Date;

public class QLDiem {
    private int gradeID;
    private String studentID;
    private int subjectID;
    private int semesterID;
    private String teacherID;

    private Double oralScore1;
    private Double oralScore2;
    private Double oralScore3;
    private Double quiz15Min1;
    private Double quiz15Min2;
    private Double midtermScore;
    private Double finalScore;
    private Double averageScore;

    private String gradeCategory;
    private String notes;
    private Date createDate;
    private Date updatedDate;
    private boolean isActive;

    public int getGradeID() { return gradeID; }
    public void setGradeID(int gradeID) { this.gradeID = gradeID; }

    public String getStudentID() { return studentID; }
    public void setStudentID(String studentID) { this.studentID = studentID; }

    public int getSubjectID() { return subjectID; }
    public void setSubjectID(int subjectID) { this.subjectID = subjectID; }

    public int getSemesterID() { return semesterID; }
    public void setSemesterID(int semesterID) { this.semesterID = semesterID; }

    public String getTeacherID() { return teacherID; }
    public void setTeacherID(String teacherID) { this.teacherID = teacherID; }

    public Double getOralScore1() { return oralScore1; }
    public void setOralScore1(Double oralScore1) { this.oralScore1 = oralScore1; }

    public Double getOralScore2() { return oralScore2; }
    public void setOralScore2(Double oralScore2) { this.oralScore2 = oralScore2; }

    public Double getOralScore3() { return oralScore3; }
    public void setOralScore3(Double oralScore3) { this.oralScore3 = oralScore3; }

    public Double getQuiz15Min1() { return quiz15Min1; }
    public void setQuiz15Min1(Double quiz15Min1) { this.quiz15Min1 = quiz15Min1; }

    public Double getQuiz15Min2() { return quiz15Min2; }
    public void setQuiz15Min2(Double quiz15Min2) { this.quiz15Min2 = quiz15Min2; }

    public Double getMidtermScore() { return midtermScore; }
    public void setMidtermScore(Double midtermScore) { this.midtermScore = midtermScore; }

    public Double getFinalScore() { return finalScore; }
    public void setFinalScore(Double finalScore) { this.finalScore = finalScore; }

    public Double getAverageScore() { return averageScore; }
    public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }

    public String getGradeCategory() { return gradeCategory; }
    public void setGradeCategory(String gradeCategory) { this.gradeCategory = gradeCategory; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Date getCreateDate() { return createDate; }
    public void setCreateDate(Date createDate) { this.createDate = createDate; }

    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }
    
    private QLHocSinh hocsinh;
    private QLMonHoc monHoc;
    private QLHocKy hocKy;
    private QLLopHoc lopHoc;
    private QLKhoaHoc khoaHoc;
    public QLHocSinh getHocsinh() { return hocsinh; }
    public void setHocsinh(QLHocSinh hocsinh) { this.hocsinh = hocsinh; }

    public QLMonHoc getMonHoc() { return monHoc; }
    public void setMonHoc(QLMonHoc monHoc) { this.monHoc = monHoc; }

    public QLHocKy getHocKy() { return hocKy; }
    public void setHocKy(QLHocKy hocKy) { this.hocKy = hocKy; }

    public QLLopHoc getLopHoc() { return lopHoc; }
    public void setLopHoc(QLLopHoc lopHoc) { this.lopHoc = lopHoc; }

    public QLKhoaHoc getKhoaHoc() { return khoaHoc; }
    public void setKhoaHoc(QLKhoaHoc khoahoc) { 
        this.khoaHoc = khoahoc; 
    }
}
