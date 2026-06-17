package model;

import com.google.gson.annotations.SerializedName;

public class VietDonModel {
    private Integer ID;
    private String StudentID;
    private String RequestDate;
    private String StartDate;
    private String EndDate;
    private String Reason;
    private Integer IsActive; // 2: Chờ duyệt, 1: Đã duyệt, 0: Từ chối
    private String StatusText;
    private String AttachedFile;
    private String StudentName;
    private String Notes;
    private String FileUrl;

    // Getters and Setters
    public Integer getID() { return ID; }
    public void setID(Integer ID) { this.ID = ID; }

    public String getStudentID() { return StudentID; }
    public void setStudentID(String studentID) { this.StudentID = studentID; }

    public String getRequestDate() { return RequestDate; }
    public void setRequestDate(String requestDate) { this.RequestDate = requestDate; }

    public String getStartDate() { return StartDate; }
    public void setStartDate(String startDate) { this.StartDate = startDate; }

    public String getEndDate() { return EndDate; }
    public void setEndDate(String endDate) { this.EndDate = endDate; }

    public String getReason() { return Reason; }
    public void setReason(String reason) { this.Reason = reason; }

    public Integer getIsActive() { return IsActive; }
    public void setIsActive(Integer isActive) { this.IsActive = isActive; }

    public String getStatusText() { return StatusText; }
    public void setStatusText(String statusText) { this.StatusText = statusText; }

    public String getAttachedFile() { return AttachedFile; }
    public void setAttachedFile(String attachedFile) { this.AttachedFile = attachedFile; }

    public String getStudentName() { return StudentName; }
    public void setStudentName(String studentName) { this.StudentName = studentName; }

    public String getNotes() { return Notes; }
    public void setNotes(String notes) { this.Notes = notes; }

    public String getFileUrl() { return FileUrl; }
    public void setFileUrl(String fileUrl) { this.FileUrl = fileUrl; }
}