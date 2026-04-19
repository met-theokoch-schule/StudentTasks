package com.example.studenttask.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {

    @Test
    void constructor_assignsTaskView() {
        User teacher = new User();
        TaskView taskView = new TaskView();
        taskView.setId(7L);

        Task task = new Task("Title", "Description", teacher, taskView);

        assertThat(task.getTaskView()).isSameAs(taskView);
    }

    @Test
    void setTaskView_updatesTaskViewReference() {
        TaskView taskView = new TaskView();
        taskView.setId(9L);

        Task task = new Task();
        task.setTaskView(taskView);

        assertThat(task.getTaskView()).isSameAs(taskView);
    }
}
