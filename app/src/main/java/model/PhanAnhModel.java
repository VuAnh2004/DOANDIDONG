package model;

public class PhanAnhModel {
    private int ID;
    private String StudentID;
    private String SubmitDate;
    private String Content;
    private String AttachedFile;

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public String getStudentID() { return StudentID; }
    public void setStudentID(String studentId) { this.StudentID = studentId; }

    public String getSubmitDate() { return SubmitDate; }
    public void setSubmitDate(String submitDate) { this.SubmitDate = submitDate; }

    public String getContent() { return Content; }
    public void setContent(String content) { this.Content = content; }

    public String getAttachedFile() { return AttachedFile; }
    public void setAttachedFile(String attachedFile) { this.AttachedFile = attachedFile; }
}