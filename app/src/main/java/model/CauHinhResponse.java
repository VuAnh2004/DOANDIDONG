package model;

import java.util.List;

public class CauHinhResponse {
    private boolean success;
    private CurrentConfig currentConfig;
    private Options options;

    public boolean isSuccess() { return success; }
    public CurrentConfig getCurrentConfig() { return currentConfig; }
    public Options getOptions() { return options; }

    public static class CurrentConfig {
        private String year;
        private String semester;
        public String getYear() { return year; }
        public String getSemester() { return semester; }
    }

    public static class Options {
        private List<String> years;
        private List<String> semesters;
        public List<String> getYears() { return years; }
        public List<String> getSemesters() { return semesters; }
    }
}