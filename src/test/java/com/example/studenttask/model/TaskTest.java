package com.example.studenttask.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {

    @Test
    void constructor_keepsTaskViewAndLegacyViewTypeInSync() {
        User teacher = new User();
        TaskView taskView = new TaskView();
        taskView.setId(7L);

        Task task = new Task("Title", "Description", teacher, taskView);

        assertThat(task.getTaskView()).isSameAs(taskView);
        assertThat(task.getViewType()).isSameAs(taskView);
    }

    @Test
    void getTaskView_fallsBackToLegacyViewType() {
        TaskView legacyViewType = new TaskView();
        legacyViewType.setId(9L);

        Task task = new Task();
        setField(task, "taskView", null);
        setField(task, "viewType", legacyViewType);

        assertThat(task.getTaskView()).isSameAs(legacyViewType);
    }

    private void setField(Task task, String fieldName, Object value) {
        try {
            var field = Task.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(task, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
