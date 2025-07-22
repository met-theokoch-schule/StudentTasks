
package com.example.studenttask.dto;

public class VersionWithSubmissionStatus {
    private Integer version;
    private Boolean isSubmitted;
    private String displayText;
    private Boolean selected = false; // Default to false

    public VersionWithSubmissionStatus() {}

    public VersionWithSubmissionStatus(Integer version, Boolean isSubmitted, String displayText) {
        this.version = version;
        this.isSubmitted = isSubmitted;
        this.displayText = displayText;
        this.selected = false;
    }

    // Constructor with selection parameter
    public VersionWithSubmissionStatus(Integer version, String displayText, Boolean isSubmitted, Boolean selected) {
        this.version = version;
        this.displayText = displayText;
        this.isSubmitted = isSubmitted;
        this.selected = selected;
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

    public Boolean isSubmitted() {
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

    public Boolean getSelected() {
        return selected;
    }

    public Boolean isSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
