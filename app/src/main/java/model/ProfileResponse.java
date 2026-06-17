package model;

import java.util.List;

public class ProfileResponse {
    private ProfileData Profile;
    private AccountData Account;
    private List<HosohoaData> Hosohoas;

    public ProfileData getProfile() { return Profile; }
    public AccountData getAccount() { return Account; }
    public List<HosohoaData> getHosohoas() { return Hosohoas; }

    public static class ProfileData {
        private int ID;
        private String StudentID;
        private String FullName;
        private String Birth;
        private String Gender;
        private String Address;
        private String NumberPhone;
        private String Province;
        private String Commune;
        private String Hamlet;
        private String Images;
        private String LopHoc;
        private String KhoaHoc;

        public int getID() { return ID; }
        public String getStudentID() { return StudentID; }
        public String getFullName() { return FullName; }
        public String getBirth() { return Birth; }
        public String getGender() { return Gender; }
        public String getAddress() { return Address; }
        public String getNumberPhone() { return NumberPhone; }
        public String getProvince() { return Province; }
        public String getCommune() { return Commune; }
        public String getHamlet() { return Hamlet; }
        public String getImages() { return Images; }
        public String getLopHoc() { return LopHoc; }
        public String getKhoaHoc() { return KhoaHoc; }
    }

    public static class AccountData {
        private String Email;
        private String UserName;
        public String getEmail() { return Email; }
        public String getUserName() { return UserName; }
    }

    public static class HosohoaData {
        private int ID;
        private String DocumentType;
        private String Attachment;
        private String Notes;
        public int getID() { return ID; }
        public String getDocumentType() { return DocumentType; }
        public String getAttachment() { return Attachment; }
        public String getNotes() { return Notes; }
    }
}