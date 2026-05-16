package model.bean;

public class QLHocKy {

    private int semesterId;
    private String semesterName;
    private String semesterCode;
    private boolean isActive;

    public int getSemesterId() {
        return semesterId;
    }
    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public String getSemesterName() {
        return semesterName;
    }
    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getSemesterCode() {
        return semesterCode;
    }
    public void setSemesterCode(String semesterCode) {
        this.semesterCode = semesterCode;
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        this.isActive = active;
    }
}
