package com.pfa.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.pfa.myapp.IntegrationTest;
import com.pfa.myapp.domain.JobHistory;
import com.pfa.myapp.domain.enumeration.Language;
import com.pfa.myapp.repository.JobHistoryRepository;
import com.pfa.myapp.service.dto.JobHistoryDTO;
import com.pfa.myapp.service.mapper.JobHistoryMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link JobHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class JobHistoryResourceIT {

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Language DEFAULT_LANGUAGE = Language.FRENCH;
    private static final Language UPDATED_LANGUAGE = Language.ENGLISH;

    private static final String ENTITY_API_URL = "/api/job-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private JobHistoryRepository jobHistoryRepository;

    @Autowired
    private JobHistoryMapper jobHistoryMapper;

    @Autowired
    private MockMvc restJobHistoryMockMvc;

    private JobHistory jobHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JobHistory createEntity() {
        JobHistory jobHistory = new JobHistory().startDate(DEFAULT_START_DATE).endDate(DEFAULT_END_DATE).language(DEFAULT_LANGUAGE);
        return jobHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JobHistory createUpdatedEntity() {
        JobHistory jobHistory = new JobHistory().startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).language(UPDATED_LANGUAGE);
        return jobHistory;
    }

    @BeforeEach
    public void initTest() {
        jobHistoryRepository.deleteAll();
        jobHistory = createEntity();
    }

    @Test
    void createJobHistory() throws Exception {
        int databaseSizeBeforeCreate = jobHistoryRepository.findAll().size();
        // Create the JobHistory
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(jobHistory);
        restJobHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobHistoryDTO)))
            .andExpect(status().isCreated());

        // Validate the JobHistory in the database
        List<JobHistory> jobHistoryList = jobHistoryRepository.findAll();
        assertThat(jobHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        JobHistory testJobHistory = jobHistoryList.get(jobHistoryList.size() - 1);
        assertThat(testJobHistory.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testJobHistory.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testJobHistory.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
    }

    @Test
    void createJobHistoryWithExistingId() throws Exception {
        // Create the JobHistory with an existing ID
        jobHistory.setId("existing_id");
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(jobHistory);

        int databaseSizeBeforeCreate = jobHistoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restJobHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the JobHistory in the database
        List<JobHistory> jobHistoryList = jobHistoryRepository.findAll();
        assertThat(jobHistoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllJobHistories() throws Exception {
        // Initialize the database
        jobHistoryRepository.save(jobHistory);

        // Get all the jobHistoryList
        restJobHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobHistory.getId())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())));
    }

    @Test
    void getJobHistory() throws Exception {
        // Initialize the database
        jobHistoryRepository.save(jobHistory);

        // Get the jobHistory
        restJobHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, jobHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(jobHistory.getId()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()));
    }

    @Test
    void getNonExistingJobHistory() throws Exception {
        // Get the jobHistory
        restJobHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingJobHistory() throws Exception {
        // Initialize the database
        jobHistoryRepository.save(jobHistory);

        int databaseSizeBeforeUpdate = jobHistoryRepository.findAll().size();

        // Update the jobHistory
        JobHistory updatedJobHistory = jobHistoryRepository.findById(jobHistory.getId()).get();
        updatedJobHistory.startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).language(UPDATED_LANGUAGE);
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(updatedJobHistory);

        restJobHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, jobHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jobHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the JobHistory in the database
        List<JobHistory> jobHistoryList = jobHistoryRepository.findAll();
        assertThat(jobHistoryList).hasSize(databaseSizeBeforeUpdate);
        JobHistory testJobHistory = jobHistoryList.get(jobHistoryList.size() - 1);
        assertThat(testJobHistory.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testJobHistory.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testJobHistory.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
    }

    @Test
    void putNonExistingJobHistory() throws Exception {
        int databaseSizeBeforeUpdate = jobHistoryRepository.findAll().size();
        jobHistory.setId(UUID.randomUUID().toString());

        // Create the JobHistory
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(jobHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJobHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, jobHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jobHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the JobHistory in the database
        List<JobHistory> jobHistoryList = jobHistoryRepository.findAll();
        assertThat(jobHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchJobHistory() throws Exception {
        int databaseSizeBeforeUpdate = jobHistoryRepository.findAll().size();
        jobHistory.setId(UUID.randomUUID().toString());

        // Create the JobHistory
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(jobHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jobHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the JobHistory in the database
        List<JobHistory> jobHistoryList = jobHistoryRepository.findAll();
        assertThat(jobHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamJobHistory() throws Exception {
        int databaseSizeBeforeUpdate = jobHistoryRepository.findAll().size();
        jobHistory.setId(UUID.randomUUID().toString());

        // Create the JobHistory
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(jobHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the JobHistory in the database
        List<JobHistory> jobHistoryList = jobHistoryRepository.findAll();
        assertThat(jobHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateJobHistoryWithPatch() throws Exception {
        // Initialize the database
        jobHistoryRepository.save(jobHistory);

        int databaseSizeBeforeUpdate = jobHistoryRepository.findAll().size();

        // Update the jobHistory using partial update
        JobHistory partialUpdatedJobHistory = new JobHistory();
        partialUpdatedJobHistory.setId(jobHistory.getId());

        restJobHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJobHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJobHistory))
            )
            .andExpect(status().isOk());

        // Validate the JobHistory in the database
        List<JobHistory> jobHistoryList = jobHistoryRepository.findAll();
        assertThat(jobHistoryList).hasSize(databaseSizeBeforeUpdate);
        JobHistory testJobHistory = jobHistoryList.get(jobHistoryList.size() - 1);
        assertThat(testJobHistory.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testJobHistory.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testJobHistory.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
    }

    @Test
    void fullUpdateJobHistoryWithPatch() throws Exception {
        // Initialize the database
        jobHistoryRepository.save(jobHistory);

        int databaseSizeBeforeUpdate = jobHistoryRepository.findAll().size();

        // Update the jobHistory using partial update
        JobHistory partialUpdatedJobHistory = new JobHistory();
        partialUpdatedJobHistory.setId(jobHistory.getId());

        partialUpdatedJobHistory.startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).language(UPDATED_LANGUAGE);

        restJobHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJobHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJobHistory))
            )
            .andExpect(status().isOk());

        // Validate the JobHistory in the database
        List<JobHistory> jobHistoryList = jobHistoryRepository.findAll();
        assertThat(jobHistoryList).hasSize(databaseSizeBeforeUpdate);
        JobHistory testJobHistory = jobHistoryList.get(jobHistoryList.size() - 1);
        assertThat(testJobHistory.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testJobHistory.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testJobHistory.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
    }

    @Test
    void patchNonExistingJobHistory() throws Exception {
        int databaseSizeBeforeUpdate = jobHistoryRepository.findAll().size();
        jobHistory.setId(UUID.randomUUID().toString());

        // Create the JobHistory
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(jobHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJobHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, jobHistoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jobHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the JobHistory in the database
        List<JobHistory> jobHistoryList = jobHistoryRepository.findAll();
        assertThat(jobHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchJobHistory() throws Exception {
        int databaseSizeBeforeUpdate = jobHistoryRepository.findAll().size();
        jobHistory.setId(UUID.randomUUID().toString());

        // Create the JobHistory
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(jobHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jobHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the JobHistory in the database
        List<JobHistory> jobHistoryList = jobHistoryRepository.findAll();
        assertThat(jobHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamJobHistory() throws Exception {
        int databaseSizeBeforeUpdate = jobHistoryRepository.findAll().size();
        jobHistory.setId(UUID.randomUUID().toString());

        // Create the JobHistory
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(jobHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(jobHistoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the JobHistory in the database
        List<JobHistory> jobHistoryList = jobHistoryRepository.findAll();
        assertThat(jobHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteJobHistory() throws Exception {
        // Initialize the database
        jobHistoryRepository.save(jobHistory);

        int databaseSizeBeforeDelete = jobHistoryRepository.findAll().size();

        // Delete the jobHistory
        restJobHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, jobHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<JobHistory> jobHistoryList = jobHistoryRepository.findAll();
        assertThat(jobHistoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
