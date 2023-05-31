package com.pfa.myapp.service.mapper;

import com.pfa.myapp.domain.Department;
import com.pfa.myapp.domain.Employee;
import com.pfa.myapp.domain.Job;
import com.pfa.myapp.domain.JobHistory;
import com.pfa.myapp.service.dto.DepartmentDTO;
import com.pfa.myapp.service.dto.EmployeeDTO;
import com.pfa.myapp.service.dto.JobDTO;
import com.pfa.myapp.service.dto.JobHistoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link JobHistory} and its DTO {@link JobHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface JobHistoryMapper extends EntityMapper<JobHistoryDTO, JobHistory> {
    @Mapping(target = "job", source = "job", qualifiedByName = "jobId")
    @Mapping(target = "department", source = "department", qualifiedByName = "departmentId")
    @Mapping(target = "employee", source = "employee", qualifiedByName = "employeeId")
    JobHistoryDTO toDto(JobHistory s);

    @Named("jobId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    JobDTO toDtoJobId(Job job);

    @Named("departmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DepartmentDTO toDtoDepartmentId(Department department);

    @Named("employeeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EmployeeDTO toDtoEmployeeId(Employee employee);
}
