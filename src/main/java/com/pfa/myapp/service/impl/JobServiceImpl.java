package com.pfa.myapp.service.impl;

import com.pfa.myapp.domain.Job;
import com.pfa.myapp.repository.JobRepository;
import com.pfa.myapp.service.JobService;
import com.pfa.myapp.service.dto.JobDTO;
import com.pfa.myapp.service.mapper.JobMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Job}.
 */
@Service
public class JobServiceImpl implements JobService {

    private final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

    private final JobRepository jobRepository;

    private final JobMapper jobMapper;

    public JobServiceImpl(JobRepository jobRepository, JobMapper jobMapper) {
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
    }

    @Override
    public JobDTO save(JobDTO jobDTO) {
        log.debug("Request to save Job : {}", jobDTO);
        Job job = jobMapper.toEntity(jobDTO);
        job = jobRepository.save(job);
        return jobMapper.toDto(job);
    }

    @Override
    public JobDTO update(JobDTO jobDTO) {
        log.debug("Request to update Job : {}", jobDTO);
        Job job = jobMapper.toEntity(jobDTO);
        job = jobRepository.save(job);
        return jobMapper.toDto(job);
    }

    @Override
    public Optional<JobDTO> partialUpdate(JobDTO jobDTO) {
        log.debug("Request to partially update Job : {}", jobDTO);

        return jobRepository
            .findById(jobDTO.getId())
            .map(existingJob -> {
                jobMapper.partialUpdate(existingJob, jobDTO);

                return existingJob;
            })
            .map(jobRepository::save)
            .map(jobMapper::toDto);
    }

    @Override
    public Page<JobDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Jobs");
        return jobRepository.findAll(pageable).map(jobMapper::toDto);
    }

    public Page<JobDTO> findAllWithEagerRelationships(Pageable pageable) {
        return jobRepository.findAllWithEagerRelationships(pageable).map(jobMapper::toDto);
    }

    @Override
    public Optional<JobDTO> findOne(String id) {
        log.debug("Request to get Job : {}", id);
        return jobRepository.findOneWithEagerRelationships(id).map(jobMapper::toDto);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete Job : {}", id);
        jobRepository.deleteById(id);
    }
}
