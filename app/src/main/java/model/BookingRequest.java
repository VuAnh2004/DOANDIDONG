package model;

public class BookingRequest {
    private int RoomId;
    private String UserName;
    private String StartTime; // YYYY-MM-DD HH:mm:ss
    private String EndTime;
    private String Purpose;

    public BookingRequest(int roomId, String userName, String startTime, String endTime, String purpose) {
        this.RoomId = roomId;
        this.UserName = userName;
        this.StartTime = startTime;
        this.EndTime = endTime;
        this.Purpose = purpose;
    }

    public int getRoomId() { return RoomId; }
    public String getUserName() { return UserName; }
    public String getStartTime() { return StartTime; }
    public String getEndTime() { return EndTime; }
    public String getPurpose() { return Purpose; }
}