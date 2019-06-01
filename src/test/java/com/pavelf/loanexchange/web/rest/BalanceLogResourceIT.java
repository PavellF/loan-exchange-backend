package com.pavelf.loanexchange.web.rest;

import com.pavelf.loanexchange.LoanExchangeBackendApp;
import com.pavelf.loanexchange.domain.BalanceLog;
import com.pavelf.loanexchange.domain.enumeration.BalanceLogEvent;
import com.pavelf.loanexchange.repository.BalanceLogRepository;
import com.pavelf.loanexchange.repository.DealRepository;
import com.pavelf.loanexchange.service.UserService;
import com.pavelf.loanexchange.web.rest.errors.ExceptionTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.pavelf.loanexchange.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Integration tests for the {@Link BalanceLogResource} REST controller.
 */
@SpringBootTest(classes = LoanExchangeBackendApp.class)
public class BalanceLogResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_OLD_VALUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_OLD_VALUE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_AMOUNT_CHANGED = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT_CHANGED = new BigDecimal(2);

    private static final BalanceLogEvent DEFAULT_TYPE = BalanceLogEvent.NEW_DEAL_OPEN;
    private static final BalanceLogEvent UPDATED_TYPE = BalanceLogEvent.LOAN_TAKEN;

    @Autowired
    private BalanceLogRepository balanceLogRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private DealRepository dealRepository;

    @Autowired
    private Validator validator;

    private MockMvc restBalanceLogMockMvc;

    private BalanceLog balanceLog;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BalanceLogResource balanceLogResource =
            new BalanceLogResource(userService, balanceLogRepository, dealRepository);
        this.restBalanceLogMockMvc = MockMvcBuilders.standaloneSetup(balanceLogResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BalanceLog createEntity(EntityManager em) {
        BalanceLog balanceLog = new BalanceLog()
            .date(DEFAULT_DATE)
            .oldValue(DEFAULT_OLD_VALUE)
            .amountChanged(DEFAULT_AMOUNT_CHANGED)
            .type(DEFAULT_TYPE);
        return balanceLog;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BalanceLog createUpdatedEntity(EntityManager em) {
        BalanceLog balanceLog = new BalanceLog()
            .date(UPDATED_DATE)
            .oldValue(UPDATED_OLD_VALUE)
            .amountChanged(UPDATED_AMOUNT_CHANGED)
            .type(UPDATED_TYPE);
        return balanceLog;
    }

    @BeforeEach
    public void initTest() {
        balanceLog = createEntity(em);
    }

    @Test
    @Transactional
    public void createBalanceLog() throws Exception {
        int databaseSizeBeforeCreate = balanceLogRepository.findAll().size();

        // Create the BalanceLog
        restBalanceLogMockMvc.perform(post("/api/balance-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(balanceLog)))
            .andExpect(status().isCreated());

        // Validate the BalanceLog in the database
        List<BalanceLog> balanceLogList = balanceLogRepository.findAll();
        assertThat(balanceLogList).hasSize(databaseSizeBeforeCreate + 1);
        BalanceLog testBalanceLog = balanceLogList.get(balanceLogList.size() - 1);
        assertThat(testBalanceLog.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testBalanceLog.getOldValue()).isEqualTo(DEFAULT_OLD_VALUE);
        assertThat(testBalanceLog.getAmountChanged()).isEqualTo(DEFAULT_AMOUNT_CHANGED);
        assertThat(testBalanceLog.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    public void createBalanceLogWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = balanceLogRepository.findAll().size();

        // Create the BalanceLog with an existing ID
        balanceLog.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBalanceLogMockMvc.perform(post("/api/balance-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(balanceLog)))
            .andExpect(status().isBadRequest());

        // Validate the BalanceLog in the database
        List<BalanceLog> balanceLogList = balanceLogRepository.findAll();
        assertThat(balanceLogList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = balanceLogRepository.findAll().size();
        // set the field null
        balanceLog.setDate(null);

        // Create the BalanceLog, which fails.

        restBalanceLogMockMvc.perform(post("/api/balance-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(balanceLog)))
            .andExpect(status().isBadRequest());

        List<BalanceLog> balanceLogList = balanceLogRepository.findAll();
        assertThat(balanceLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOldValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = balanceLogRepository.findAll().size();
        // set the field null
        balanceLog.setOldValue(null);

        // Create the BalanceLog, which fails.

        restBalanceLogMockMvc.perform(post("/api/balance-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(balanceLog)))
            .andExpect(status().isBadRequest());

        List<BalanceLog> balanceLogList = balanceLogRepository.findAll();
        assertThat(balanceLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAmountChangedIsRequired() throws Exception {
        int databaseSizeBeforeTest = balanceLogRepository.findAll().size();
        // set the field null
        balanceLog.setAmountChanged(null);

        // Create the BalanceLog, which fails.

        restBalanceLogMockMvc.perform(post("/api/balance-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(balanceLog)))
            .andExpect(status().isBadRequest());

        List<BalanceLog> balanceLogList = balanceLogRepository.findAll();
        assertThat(balanceLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = balanceLogRepository.findAll().size();
        // set the field null
        balanceLog.setType(null);

        // Create the BalanceLog, which fails.

        restBalanceLogMockMvc.perform(post("/api/balance-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(balanceLog)))
            .andExpect(status().isBadRequest());

        List<BalanceLog> balanceLogList = balanceLogRepository.findAll();
        assertThat(balanceLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBalanceLogs() throws Exception {
        // Initialize the database
        balanceLogRepository.saveAndFlush(balanceLog);

        // Get all the balanceLogList
        restBalanceLogMockMvc.perform(get("/api/balance-logs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(balanceLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].oldValue").value(hasItem(DEFAULT_OLD_VALUE.intValue())))
            .andExpect(jsonPath("$.[*].amountChanged").value(hasItem(DEFAULT_AMOUNT_CHANGED.intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
    
    @Test
    @Transactional
    public void getBalanceLog() throws Exception {
        // Initialize the database
        balanceLogRepository.saveAndFlush(balanceLog);

        // Get the balanceLog
        restBalanceLogMockMvc.perform(get("/api/balance-logs/{id}", balanceLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(balanceLog.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.oldValue").value(DEFAULT_OLD_VALUE.intValue()))
            .andExpect(jsonPath("$.amountChanged").value(DEFAULT_AMOUNT_CHANGED.intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBalanceLog() throws Exception {
        // Get the balanceLog
        restBalanceLogMockMvc.perform(get("/api/balance-logs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBalanceLog() throws Exception {
        // Initialize the database
        balanceLogRepository.saveAndFlush(balanceLog);

        int databaseSizeBeforeUpdate = balanceLogRepository.findAll().size();

        // Update the balanceLog
        BalanceLog updatedBalanceLog = balanceLogRepository.findById(balanceLog.getId()).get();
        // Disconnect from session so that the updates on updatedBalanceLog are not directly saved in db
        em.detach(updatedBalanceLog);
        updatedBalanceLog
            .date(UPDATED_DATE)
            .oldValue(UPDATED_OLD_VALUE)
            .amountChanged(UPDATED_AMOUNT_CHANGED)
            .type(UPDATED_TYPE);

        restBalanceLogMockMvc.perform(put("/api/balance-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBalanceLog)))
            .andExpect(status().isOk());

        // Validate the BalanceLog in the database
        List<BalanceLog> balanceLogList = balanceLogRepository.findAll();
        assertThat(balanceLogList).hasSize(databaseSizeBeforeUpdate);
        BalanceLog testBalanceLog = balanceLogList.get(balanceLogList.size() - 1);
        assertThat(testBalanceLog.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testBalanceLog.getOldValue()).isEqualTo(UPDATED_OLD_VALUE);
        assertThat(testBalanceLog.getAmountChanged()).isEqualTo(UPDATED_AMOUNT_CHANGED);
        assertThat(testBalanceLog.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingBalanceLog() throws Exception {
        int databaseSizeBeforeUpdate = balanceLogRepository.findAll().size();

        // Create the BalanceLog

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBalanceLogMockMvc.perform(put("/api/balance-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(balanceLog)))
            .andExpect(status().isBadRequest());

        // Validate the BalanceLog in the database
        List<BalanceLog> balanceLogList = balanceLogRepository.findAll();
        assertThat(balanceLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBalanceLog() throws Exception {
        // Initialize the database
        balanceLogRepository.saveAndFlush(balanceLog);

        int databaseSizeBeforeDelete = balanceLogRepository.findAll().size();

        // Delete the balanceLog
        restBalanceLogMockMvc.perform(delete("/api/balance-logs/{id}", balanceLog.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<BalanceLog> balanceLogList = balanceLogRepository.findAll();
        assertThat(balanceLogList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BalanceLog.class);
        BalanceLog balanceLog1 = new BalanceLog();
        balanceLog1.setId(1L);
        BalanceLog balanceLog2 = new BalanceLog();
        balanceLog2.setId(balanceLog1.getId());
        assertThat(balanceLog1).isEqualTo(balanceLog2);
        balanceLog2.setId(2L);
        assertThat(balanceLog1).isNotEqualTo(balanceLog2);
        balanceLog1.setId(null);
        assertThat(balanceLog1).isNotEqualTo(balanceLog2);
    }
}
