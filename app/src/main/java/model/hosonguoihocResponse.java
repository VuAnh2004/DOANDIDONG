package model;

public class hosonguoihocResponse {
    private boolean success;
    private hosonguoihocmodel student;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public hosonguoihocmodel getStudent() {
        return student;
    }

    public void setStudent(hosonguoihocmodel student) {
        this.student = student;
    }
}