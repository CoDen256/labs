package io.github.aljolen.kanban.controller;

import io.github.aljolen.kanban.model.Kanban;
import io.github.aljolen.kanban.model.KanbanDTO;
import io.github.aljolen.kanban.model.Task;
import io.github.aljolen.kanban.model.TaskDTO;
import io.github.aljolen.kanban.model.TaskStatus;
import io.github.aljolen.kanban.repository.KanbanRepository;
import io.github.aljolen.kanban.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Optional;

@TestPropertySource( properties = {
        "spring.datasource.url=jdbc:h2:mem:test",
        "spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.H2Dialect"
})
public class CommonITCase {

    @Autowired
    private KanbanRepository kanbanRepository;

    @Autowired
    private TaskRepository taskRepository;

    protected Kanban createSingleKanban(){
        Kanban kanban = new Kanban();
        int random = (int)(Math.random() * 100 + 1);
        kanban.setTitle("Test Kanban " + random);
        kanban.setTasks(new ArrayList<>());
        return kanban;
    }

    protected Task createSingleTask(){
        Task task = new Task();
        int random = (int)(Math.random() * 100 + 1);
        task.setTitle("Test Task " + random);
        task.setDescription("Description " + random);
        task.setColor("Color " + random);
        task.setStatus(TaskStatus.TODO);
        return task;
    }

    protected KanbanDTO convertKanbanToDTO(Kanban kanban) {
        KanbanDTO kanbanDTO = new KanbanDTO();
        kanban.setTitle(kanban.getTitle());
        return kanbanDTO;
    }

    protected TaskDTO convertTaskToDTO(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle(task.getTitle());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setColor(task.getColor());
        taskDTO.setStatus(task.getStatus());
        return taskDTO;
    }

    protected Kanban saveSingleRandomKanban(){
        return kanbanRepository.save(createSingleKanban());
    }

    protected Kanban saveSingleKanbanWithOneTask(){
        Kanban kanban = createSingleKanban();
        Task task = createSingleTask();
        kanban.addTask(task);
        return kanbanRepository.save(kanban);
    }

    protected Task saveSingleTask(){
        return taskRepository.save(createSingleTask());
    }

    protected Optional<Kanban> findKanbanInDbById(Long id) {
        return kanbanRepository.findById(id);
    }

    protected Optional<Task> findTaskInDbById(Long id) {
        return taskRepository.findById(id);
    }
}
