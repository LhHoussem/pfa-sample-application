package com.pfa.myapp.service.impl;

import com.pfa.myapp.domain.Employee;
import com.pfa.myapp.repository.EmployeeRepository;
import com.pfa.myapp.service.EmployeeService;
import com.pfa.myapp.service.dto.EmployeeDTO;
import com.pfa.myapp.service.mapper.EmployeeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Employee}.
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public EmployeeDTO save(EmployeeDTO employeeDTO) {
        log.debug("Request to save Employee : {}", employeeDTO);
        Employee employee = employeeMapper.toEntity(employeeDTO);
        employee = employeeRepository.save(employee);
        return employeeMapper.toDto(employee);
    }

    @Override
    public EmployeeDTO update(EmployeeDTO employeeDTO) {
        log.debug("Request to update Employee : {}", employeeDTO);
        Employee employee = employeeMapper.toEntity(employeeDTO);
        employee = employeeRepository.save(employee);
        return employeeMapper.toDto(employee);
    }

    @Override
    public Optional<EmployeeDTO> partialUpdate(EmployeeDTO employeeDTO) {
        log.debug("Request to partially update Employee : {}", employeeDTO);

        return employeeRepository
            .findById(employeeDTO.getId())
            .map(existingEmployee -> {
                employeeMapper.partialUpdate(existingEmployee, employeeDTO);

                return existingEmployee;
            })
            .map(employeeRepository::save)
            .map(employeeMapper::toDto);
    }

    @Override
    public Page<EmployeeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Employees");
        return employeeRepository.findAll(pageable).map(employeeMapper::toDto);
    }

    @Override
    public Optional<EmployeeDTO> findOne(String id) {
        log.debug("Request to get Employee : {}", id);
        return employeeRepository.findById(id).map(employeeMapper::toDto);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete Employee : {}", id);
        employeeRepository.deleteById(id);
    }
}
