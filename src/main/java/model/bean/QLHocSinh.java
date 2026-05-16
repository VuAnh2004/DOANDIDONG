package model.bean;

import java.util.Date;

public class QLHocSinh {
    private int ID;
    private String StudentID;
    private String FullName;
    private Date Birth;
    private String Gender;
    private String Address;
    private String Nation;
    private String Religion;
    private String StatusStudent;
    private String NumberPhone;
    private String Images;
    private boolean IsActive;

    // Getters & Setters
    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public String getStudentID() { return StudentID; }
    public void setStudentID(String studentID) { StudentID = studentID; }

    public String getFullName() { return FullName; }
    public void setFullName(String fullName) { FullName = fullName; }

    public Date getBirth() { return Birth; }
    public void setBirth(Date birth) { Birth = birth; }

    public String getGender() { return Gender; }
    public void setGender(String gender) { Gender = gender; }

    public String getAddress() { return Address; }
    public void setAddress(String address) { Address = address; }

    public String getNation() { return Nation; }
    public void setNation(String nation) { Nation = nation; }

    public String getReligion() { return Religion; }
    public void setReligion(String religion) { Religion = religion; }

    public String getStatusStudent() { return StatusStudent; }
    public void setStatusStudent(String statusStudent) { StatusStudent = statusStudent; }

    public String getNumberPhone() { return NumberPhone; }
    public void setNumberPhone(String numberPhone) { NumberPhone = numberPhone; }

    public String getImages() { return Images; }
    public void setImages(String images) { Images = images; }

    public boolean isIsActive() { return IsActive; }
    public void setIsActive(boolean isActive) { IsActive = isActive; }
}
