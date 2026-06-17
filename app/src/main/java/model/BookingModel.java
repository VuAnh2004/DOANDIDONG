package model;

public class BookingModel {
    private int RequestID;
    private String RoomName;
    private String StartTime;
    private String EndTime;
    private String Status;
    private String Purpose;
    private String UserName;
    private String CreatedAt;

    public int getRequestID() { return RequestID; }
    public String getRoomName() { return RoomName; }
    public String getStartTime() { return StartTime; }
    public String getEndTime() { return EndTime; }
    public String getStatus() { return Status; }
    public String getPurpose() { return Purpose; }
    public String getUserName() { return UserName; }
    public String getCreatedAt() { return CreatedAt; }

    public String getStatusText() {
        switch (Status) {
            case "Pending": return "Chờ duyệt";
            case "Approved": return "Đã duyệt";
            case "Returned": return "Đã trả";
            case "Cancelled": return "Đã hủy";
            default: return Status;
        }
    }
}