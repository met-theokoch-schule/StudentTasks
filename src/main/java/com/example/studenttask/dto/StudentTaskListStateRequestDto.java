package com.example.studenttask.dto;

import java.util.ArrayList;
import java.util.List;

public class StudentTaskListStateRequestDto {
    private List<String> expandedUnitIds = new ArrayList<>();

    public List<String> getExpandedUnitIds() {
        return expandedUnitIds;
    }

    public void setExpandedUnitIds(List<String> expandedUnitIds) {
        this.expandedUnitIds = expandedUnitIds != null ? expandedUnitIds : new ArrayList<>();
    }
}
