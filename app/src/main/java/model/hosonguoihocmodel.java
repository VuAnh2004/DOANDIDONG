package model;

import java.util.List;

public class hosonguoihocmodel {

    private int ID;
    private String StudentID;
    private String FullName;
    private String Birth;
    private String Gender;
    private String Address;
    private String Nation;
    private String Religion;
    private String StatusStudent;
    private String NumberPhone;
    private String Images;
    private String Nationality;
    private String FullAddress;

    private LopHoc LopHoc;
    private List<Relative> Relatives;
    private List<Document> Documents;

    // ======= Getter & Setter =======
    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public String getStudentID() {
        return StudentID;
    }
    public void setStudentID(String studentID) {
        StudentID = studentID;
    }
    public String getFullName() {
        return FullName;
    }
    public void setFullName(String fullName) {
        FullName = fullName;
    }
    public String getBirth() {
        return Birth;
    }
    public void setBirth(String birth) {
        Birth = birth;
    }
    public String getGender() {
        return Gender;
    }
    public void setGender(String gender) {
        Gender = gender;
    }
    public String getAddress() {
        return Address;
    }
    public void setAddress(String address) {
        Address = address;
    }
    public String getNation() {
        return Nation;
    }
    public void setNation(String nation) {
        Nation = nation;
    }
    public String getReligion() {
        return Religion;
    }
    public void setReligion(String religion) {
        Religion = religion;
    }
    public String getStatusStudent() {
        return StatusStudent;
    }
    public void setStatusStudent(String statusStudent) {
        StatusStudent = statusStudent;
    }
    public String getNumberPhone() {
        return NumberPhone;
    }
    public void setNumberPhone(String numberPhone) {
        NumberPhone = numberPhone;
    }
    public String getImages() {
        return Images;
    }
    public void setImages(String images) {
        Images = images;
    }
    public String getNationality() {
        return Nationality;
    }
    public void setNationality(String nationality) {
        Nationality = nationality;
    }
    public String getFullAddress() {
        return FullAddress;
    }
    public void setFullAddress(String fullAddress) {
        FullAddress = fullAddress;
    }
    public LopHoc getLopHoc() {
        return LopHoc;
    }
    public void setLopHoc(LopHoc lopHoc) {
        LopHoc = lopHoc;
    }
    public List<Relative> getRelatives() {
        return Relatives;
    }
    public void setRelatives(List<Relative> relatives) {
        Relatives = relatives;
    }
    public List<Document> getDocuments() {
        return Documents;
    }
    public void setDocuments(List<Document> documents) {
        Documents = documents;
    }

    // ======= Inner Classes =======
    public static class LopHoc {
        private int ClassID;
        private String ClassName;
        private String SchoolYear;

        public int getClassID() {
            return ClassID;
        }
        public void setClassID(int classID) {
            ClassID = classID;
        }
        public String getClassName() {
            return ClassName;
        }
        public void setClassName(String className) {
            ClassName = className;
        }
        public String getSchoolYear() {
            return SchoolYear;
        }
        public void setSchoolYear(String schoolYear) {
            SchoolYear = schoolYear;
        }
    }

    public static class Relative {
        private String FullName;
        private String Relationship;
        private String Occupation;
        private String Phone;
        private String Address;

        public String getFullName() {
            return FullName;
        }
        public void setFullName(String fullName) {
            FullName = fullName;
        }
        public String getRelationship() {
            return Relationship;
        }
        public void setRelationship(String relationship) {
            Relationship = relationship;
        }
        public String getOccupation() {
            return Occupation;
        }
        public void setOccupation(String occupation) {
            Occupation = occupation;
        }
        public String getPhone() {
            return Phone;
        }
        public void setPhone(String phone) {
            Phone = phone;
        }
        public String getAddress() {
            return Address;
        }
        public void setAddress(String address) {
            Address = address;
        }
    }

    public static class Document {
        private int ID;
        private String DocumentType;
        private String Attachment;
        private String Notes;

        public int getID() {
            return ID;
        }
        public void setID(int ID) {
            this.ID = ID;
        }
        public String getDocumentType() {
            return DocumentType;
        }
        public void setDocumentType(String documentType) {
            DocumentType = documentType;
        }
        public String getAttachment() {
            return Attachment;
        }
        public void setAttachment(String attachment) {
            Attachment = attachment;
        }
        public String getNotes() {
            return Notes;
        }
        public void setNotes(String notes) {
            Notes = notes;
        }
    }
}