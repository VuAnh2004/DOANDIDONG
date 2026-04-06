package model;

import java.util.List;

public class DiemResponse {

    private boolean success;
    private String message;
    private List<DiemModel> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<DiemModel> getData() {
        return data;
    }
}