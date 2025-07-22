
package com.example.studenttask.dto;

public class VersionWithSubmissionStatus {
    private Integer version;
    private boolean isSubmitted;
    private String displayText;

    public VersionWithSubmissionStatus(Integer version, boolean isSubmitted) {
        this.version = version;
        this.isSubmitted = isSubmitted;
        this.displayText = "Version " + version + (isSubmitted ? " *" : "");
    }

    // Getters and Setters
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public void setSubmitted(boolean submitted) {
        isSubmitted = submitted;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }
}
package com.example.studenttask.dto;

public class VersionWithSubmissionStatus {
    private Integer version;
    private Boolean isSubmitted;
    private String displayText;

    public VersionWithSubmissionStatus() {}

    public VersionWithSubmissionStatus(Integer version, Boolean isSubmitted, String displayText) {
        this.version = version;
        this.isSubmitted = isSubmitted;
        this.displayText = displayText;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getIsSubmitted() {
        return isSubmitted;
    }

    public void setIsSubmitted(Boolean isSubmitted) {
        this.isSubmitted = isSubmitted;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }
}
