package model.bean;

public class QLKhoaHoc {
    private int courseID;
    private Integer startYear;
    private Integer endYear;
    private Integer cohort;
    private boolean active;

    public int getCourseID() { return courseID; }
    public void setCourseID(int courseID) { this.courseID = courseID; }

    public Integer getStartYear() { return startYear; }
    public void setStartYear(Integer startYear) { this.startYear = startYear; }

    public Integer getEndYear() { return endYear; }
    public void setEndYear(Integer endYear) { this.endYear = endYear; }

    public Integer getCohort() { return cohort; }
    public void setCohort(Integer cohort) { this.cohort = cohort; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
