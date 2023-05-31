package com.pfa.myapp.service.mapper;

import com.pfa.myapp.domain.Task;
import com.pfa.myapp.service.dto.TaskDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper extends EntityMapper<TaskDTO, Task> {}
