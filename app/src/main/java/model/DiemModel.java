package model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DiemModel {

    @SerializedName("GradeID")
    private int GradeID;

    @SerializedName("StudentID")
    private String StudentID;

    @SerializedName("SubjectName")
    private String SubjectName;

    @SerializedName("SemesterName")
    private String SemesterName;

    @SerializedName("SemesterCode")
    private String SemesterCode;

    @SerializedName("OralScores")
    private OralScores oralScores;

    @SerializedName("Quizzes")
    private Quizzes quizzes;

    @SerializedName("MidtermScore")
    private Double MidtermScore;

    @SerializedName("Final_score")
    private Double Final_score;

    @SerializedName("AverageScore")
    private Double AverageScore;

    // ===== GETTER (BỔ SUNG ĐẦY ĐỦ ĐỂ HẾT LỖI) =====
    public String getSubjectName() { return SubjectName; }

    // Hai dòng quan trọng nhất để fix lỗi trong Activity:
    public String getSemesterName() { return SemesterName; }
    public String getSemesterCode() { return SemesterCode; }

    public OralScores getOralScores() { return oralScores; }
    public Quizzes getQuizzes() { return quizzes; }
    public Double getMidtermScore() { return MidtermScore; }
    public Double getFinal_score() { return Final_score; }
    public Double getAverageScore() { return AverageScore; }

    // ===== INNER CLASSES =====
    public static class OralScores {
        @SerializedName("OralScore1")
        private Double OralScore1;
        @SerializedName("OralScore2")
        private Double OralScore2;
        @SerializedName("OralScore3")
        private Double OralScore3;

        public Double getOralScore1() { return OralScore1; }
        public Double getOralScore2() { return OralScore2; }
        public Double getOralScore3() { return OralScore3; }
    }

    public static class Quizzes {
        @SerializedName("Quiz15Min1")
        private Double Quiz15Min1;
        @SerializedName("Quiz15Min2")
        private Double Quiz15Min2;

        public Double getQuiz15Min1() { return Quiz15Min1; }
        public Double getQuiz15Min2() { return Quiz15Min2; }
    }

    // ===== SUBJECT OPTION =====
    public static class SubjectOption {
        @SerializedName("SubjectID")
        public int SubjectID;

        @SerializedName("SubjectName")
        public String SubjectName;

        @Override
        public String toString() {
            return SubjectName;
        }
    }
}