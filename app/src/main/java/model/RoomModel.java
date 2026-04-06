package model;

public class RoomModel {
    private int RoomID;
    private String RoomName;
    private boolean IsActive;

    public int getRoomID() { return RoomID; }
    public String getRoomName() { return RoomName; }
    public boolean isActive() { return IsActive; }

    @Override
    public String toString() {
        return RoomName; // Dùng cho Spinner/AutoCompleteTextView
    }
}