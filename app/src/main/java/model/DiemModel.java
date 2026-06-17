package model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
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

    @SerializedName("Year")
    private String Year;

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

    // ================== GETTERS ==================
    public String getSubjectName() { return SubjectName; }
    public String getSemesterName() { return SemesterName; }
    public String getSemesterCode() { return SemesterCode; }
    public String getYear() { return Year; }

    public Double getMidtermScore() { return MidtermScore; }
    public Double getFinal_score() { return Final_score; }
    public Double getAverageScore() { return AverageScore; }

    public OralScores getOralScores() { return oralScores; }
    public Quizzes getQuizzes() { return quizzes; }

    // ================== STRING METHODS ==================
    public String getOralScoresString() {
        if (oralScores == null) return "---";
        List<String> s = new ArrayList<>();
        if (oralScores.OralScore1 != null) s.add(String.valueOf(oralScores.OralScore1));
        if (oralScores.OralScore2 != null) s.add(String.valueOf(oralScores.OralScore2));
        if (oralScores.OralScore3 != null) s.add(String.valueOf(oralScores.OralScore3));
        return s.isEmpty() ? "---" : String.join(", ", s);
    }

    public String getQuizzesString() {
        if (quizzes == null) return "---";
        List<String> s = new ArrayList<>();
        if (quizzes.Quiz15Min1 != null) s.add(String.valueOf(quizzes.Quiz15Min1));
        if (quizzes.Quiz15Min2 != null) s.add(String.valueOf(quizzes.Quiz15Min2));
        return s.isEmpty() ? "---" : String.join(", ", s);
    }

    public int getTotalScoresCount() {
        int count = 0;
        if (oralScores != null) {
            if (oralScores.OralScore1 != null) count++;
            if (oralScores.OralScore2 != null) count++;
            if (oralScores.OralScore3 != null) count++;
        }
        if (quizzes != null) {
            if (quizzes.Quiz15Min1 != null) count++;
            if (quizzes.Quiz15Min2 != null) count++;
        }
        if (MidtermScore != null) count++;
        if (Final_score != null) count++;
        return count;
    }

    public Double getHighestScore() {
        Double max = null;
        if (oralScores != null) {
            max = updateMax(max, oralScores.OralScore1);
            max = updateMax(max, oralScores.OralScore2);
            max = updateMax(max, oralScores.OralScore3);
        }
        if (quizzes != null) {
            max = updateMax(max, quizzes.Quiz15Min1);
            max = updateMax(max, quizzes.Quiz15Min2);
        }
        max = updateMax(max, MidtermScore);
        max = updateMax(max, Final_score);
        return max;
    }

    private Double updateMax(Double currentMax, Double newValue) {
        if (newValue == null) return currentMax;
        if (currentMax == null) return newValue;
        return Math.max(currentMax, newValue);
    }

    // ================== INNER CLASSES ==================
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