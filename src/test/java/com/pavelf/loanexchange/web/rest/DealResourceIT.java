package com.pavelf.loanexchange.web.rest;

import com.pavelf.loanexchange.LoanExchangeBackendApp;
import com.pavelf.loanexchange.domain.Deal;
import com.pavelf.loanexchange.domain.enumeration.DealStatus;
import com.pavelf.loanexchange.domain.enumeration.Period;
import com.pavelf.loanexchange.repository.DealRepository;
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
 * Integration tests for the {@Link DealResource} REST controller.
 */
@SpringBootTest(classes = LoanExchangeBackendApp.class)
public class DealResourceIT {

    private static final Instant DEFAULT_DATE_OPEN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_OPEN = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATE_BECOME_ACTIVE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_BECOME_ACTIVE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_START_BALANCE = new BigDecimal(0);
    private static final BigDecimal UPDATED_START_BALANCE = new BigDecimal(1);

    private static final BigDecimal DEFAULT_PERCENT = new BigDecimal(0);
    private static final BigDecimal UPDATED_PERCENT = new BigDecimal(1);

    private static final BigDecimal DEFAULT_FINE = new BigDecimal(0);
    private static final BigDecimal UPDATED_FINE = new BigDecimal(1);

    private static final Integer DEFAULT_SUCCESS_RATE = 1;
    private static final Integer UPDATED_SUCCESS_RATE = 2;

    private static final Integer DEFAULT_TERM = 0;
    private static final Integer UPDATED_TERM = 1;

    private static final Period DEFAULT_PAYMENT_EVERY = Period.DAY;
    private static final Period UPDATED_PAYMENT_EVERY = Period.MONTH;

    private static final DealStatus DEFAULT_STATUS = DealStatus.PENDING;
    private static final DealStatus UPDATED_STATUS = DealStatus.ACTIVE;

    private static final Boolean DEFAULT_AUTO_PAYMENT_ENABLED = false;
    private static final Boolean UPDATED_AUTO_PAYMENT_ENABLED = true;

    private static final Boolean DEFAULT_CAPITALIZATION = false;
    private static final Boolean UPDATED_CAPITALIZATION = true;

    private static final Boolean DEFAULT_EARLY_PAYMENT = false;
    private static final Boolean UPDATED_EARLY_PAYMENT = true;

    @Autowired
    private DealRepository dealRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restDealMockMvc;

    private Deal deal;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DealResource dealResource = new DealResource(dealRepository, userService, dealService);
        this.restDealMockMvc = MockMvcBuilders.standaloneSetup(dealResource)
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
    public static Deal createEntity(EntityManager em) {
        Deal deal = new Deal()
            .dateOpen(DEFAULT_DATE_OPEN)
            .dateBecomeActive(DEFAULT_DATE_BECOME_ACTIVE)
            .startBalance(DEFAULT_START_BALANCE)
            .percent(DEFAULT_PERCENT)
            .fine(DEFAULT_FINE)
            .successRate(DEFAULT_SUCCESS_RATE)
            .term(DEFAULT_TERM)
            .paymentEvery(DEFAULT_PAYMENT_EVERY)
            .status(DEFAULT_STATUS)
            .autoPaymentEnabled(DEFAULT_AUTO_PAYMENT_ENABLED)
            .capitalization(DEFAULT_CAPITALIZATION)
            .earlyPayment(DEFAULT_EARLY_PAYMENT);
        return deal;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Deal createUpdatedEntity(EntityManager em) {
        Deal deal = new Deal()
            .dateOpen(UPDATED_DATE_OPEN)
            .dateBecomeActive(UPDATED_DATE_BECOME_ACTIVE)
            .startBalance(UPDATED_START_BALANCE)
            .percent(UPDATED_PERCENT)
            .fine(UPDATED_FINE)
            .successRate(UPDATED_SUCCESS_RATE)
            .term(UPDATED_TERM)
            .paymentEvery(UPDATED_PAYMENT_EVERY)
            .status(UPDATED_STATUS)
            .autoPaymentEnabled(UPDATED_AUTO_PAYMENT_ENABLED)
            .capitalization(UPDATED_CAPITALIZATION)
            .earlyPayment(UPDATED_EARLY_PAYMENT);
        return deal;
    }

    @BeforeEach
    public void initTest() {
        deal = createEntity(em);
    }

    @Test
    @Transactional
    public void createDeal() throws Exception {
        int databaseSizeBeforeCreate = dealRepository.findAll().size();

        // Create the Deal
        restDealMockMvc.perform(post("/api/deals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deal)))
            .andExpect(status().isCreated());

        // Validate the Deal in the database
        List<Deal> dealList = dealRepository.findAll();
        assertThat(dealList).hasSize(databaseSizeBeforeCreate + 1);
        Deal testDeal = dealList.get(dealList.size() - 1);
        assertThat(testDeal.getDateOpen()).isEqualTo(DEFAULT_DATE_OPEN);
        assertThat(testDeal.getDateBecomeActive()).isEqualTo(DEFAULT_DATE_BECOME_ACTIVE);
        assertThat(testDeal.getStartBalance()).isEqualTo(DEFAULT_START_BALANCE);
        assertThat(testDeal.getPercent()).isEqualTo(DEFAULT_PERCENT);
        assertThat(testDeal.getFine()).isEqualTo(DEFAULT_FINE);
        assertThat(testDeal.getSuccessRate()).isEqualTo(DEFAULT_SUCCESS_RATE);
        assertThat(testDeal.getTerm()).isEqualTo(DEFAULT_TERM);
        assertThat(testDeal.getPaymentEvery()).isEqualTo(DEFAULT_PAYMENT_EVERY);
        assertThat(testDeal.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testDeal.isAutoPaymentEnabled()).isEqualTo(DEFAULT_AUTO_PAYMENT_ENABLED);
        assertThat(testDeal.isCapitalization()).isEqualTo(DEFAULT_CAPITALIZATION);
        assertThat(testDeal.isEarlyPayment()).isEqualTo(DEFAULT_EARLY_PAYMENT);
    }

    @Test
    @Transactional
    public void createDealWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = dealRepository.findAll().size();

        // Create the Deal with an existing ID
        deal.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDealMockMvc.perform(post("/api/deals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deal)))
            .andExpect(status().isBadRequest());

        // Validate the Deal in the database
        List<Deal> dealList = dealRepository.findAll();
        assertThat(dealList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkDateOpenIsRequired() throws Exception {
        int databaseSizeBeforeTest = dealRepository.findAll().size();
        // set the field null
        deal.setDateOpen(null);

        // Create the Deal, which fails.

        restDealMockMvc.perform(post("/api/deals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deal)))
            .andExpect(status().isBadRequest());

        List<Deal> dealList = dealRepository.findAll();
        assertThat(dealList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStartBalanceIsRequired() throws Exception {
        int databaseSizeBeforeTest = dealRepository.findAll().size();
        // set the field null
        deal.setStartBalance(null);

        // Create the Deal, which fails.

        restDealMockMvc.perform(post("/api/deals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deal)))
            .andExpect(status().isBadRequest());

        List<Deal> dealList = dealRepository.findAll();
        assertThat(dealList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPercentIsRequired() throws Exception {
        int databaseSizeBeforeTest = dealRepository.findAll().size();
        // set the field null
        deal.setPercent(null);

        // Create the Deal, which fails.

        restDealMockMvc.perform(post("/api/deals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deal)))
            .andExpect(status().isBadRequest());

        List<Deal> dealList = dealRepository.findAll();
        assertThat(dealList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSuccessRateIsRequired() throws Exception {
        int databaseSizeBeforeTest = dealRepository.findAll().size();
        // set the field null
        deal.setSuccessRate(null);

        // Create the Deal, which fails.

        restDealMockMvc.perform(post("/api/deals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deal)))
            .andExpect(status().isBadRequest());

        List<Deal> dealList = dealRepository.findAll();
        assertThat(dealList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTermIsRequired() throws Exception {
        int databaseSizeBeforeTest = dealRepository.findAll().size();
        // set the field null
        deal.setTerm(null);

        // Create the Deal, which fails.

        restDealMockMvc.perform(post("/api/deals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deal)))
            .andExpect(status().isBadRequest());

        List<Deal> dealList = dealRepository.findAll();
        assertThat(dealList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPaymentEveryIsRequired() throws Exception {
        int databaseSizeBeforeTest = dealRepository.findAll().size();
        // set the field null
        deal.setPaymentEvery(null);

        // Create the Deal, which fails.

        restDealMockMvc.perform(post("/api/deals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deal)))
            .andExpect(status().isBadRequest());

        List<Deal> dealList = dealRepository.findAll();
        assertThat(dealList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = dealRepository.findAll().size();
        // set the field null
        deal.setStatus(null);

        // Create the Deal, which fails.

        restDealMockMvc.perform(post("/api/deals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deal)))
            .andExpect(status().isBadRequest());

        List<Deal> dealList = dealRepository.findAll();
        assertThat(dealList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAutoPaymentEnabledIsRequired() throws Exception {
        int databaseSizeBeforeTest = dealRepository.findAll().size();
        // set the field null
        deal.setAutoPaymentEnabled(null);

        // Create the Deal, which fails.

        restDealMockMvc.perform(post("/api/deals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deal)))
            .andExpect(status().isBadRequest());

        List<Deal> dealList = dealRepository.findAll();
        assertThat(dealList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCapitalizationIsRequired() throws Exception {
        int databaseSizeBeforeTest = dealRepository.findAll().size();
        // set the field null
        deal.setCapitalization(null);

        // Create the Deal, which fails.

        restDealMockMvc.perform(post("/api/deals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deal)))
            .andExpect(status().isBadRequest());

        List<Deal> dealList = dealRepository.findAll();
        assertThat(dealList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEarlyPaymentIsRequired() throws Exception {
        int databaseSizeBeforeTest = dealRepository.findAll().size();
        // set the field null
        deal.setEarlyPayment(null);

        // Create the Deal, which fails.

        restDealMockMvc.perform(post("/api/deals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deal)))
            .andExpect(status().isBadRequest());

        List<Deal> dealList = dealRepository.findAll();
        assertThat(dealList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDeals() throws Exception {
        // Initialize the database
        dealRepository.saveAndFlush(deal);

        // Get all the dealList
        restDealMockMvc.perform(get("/api/deals?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deal.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateOpen").value(hasItem(DEFAULT_DATE_OPEN.toString())))
            .andExpect(jsonPath("$.[*].dateBecomeActive").value(hasItem(DEFAULT_DATE_BECOME_ACTIVE.toString())))
            .andExpect(jsonPath("$.[*].startBalance").value(hasItem(DEFAULT_START_BALANCE.intValue())))
            .andExpect(jsonPath("$.[*].percent").value(hasItem(DEFAULT_PERCENT.intValue())))
            .andExpect(jsonPath("$.[*].fine").value(hasItem(DEFAULT_FINE.intValue())))
            .andExpect(jsonPath("$.[*].successRate").value(hasItem(DEFAULT_SUCCESS_RATE)))
            .andExpect(jsonPath("$.[*].term").value(hasItem(DEFAULT_TERM)))
            .andExpect(jsonPath("$.[*].paymentEvery").value(hasItem(DEFAULT_PAYMENT_EVERY.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].autoPaymentEnabled").value(hasItem(DEFAULT_AUTO_PAYMENT_ENABLED.booleanValue())))
            .andExpect(jsonPath("$.[*].capitalization").value(hasItem(DEFAULT_CAPITALIZATION.booleanValue())))
            .andExpect(jsonPath("$.[*].earlyPayment").value(hasItem(DEFAULT_EARLY_PAYMENT.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getDeal() throws Exception {
        // Initialize the database
        dealRepository.saveAndFlush(deal);

        // Get the deal
        restDealMockMvc.perform(get("/api/deals/{id}", deal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(deal.getId().intValue()))
            .andExpect(jsonPath("$.dateOpen").value(DEFAULT_DATE_OPEN.toString()))
            .andExpect(jsonPath("$.dateBecomeActive").value(DEFAULT_DATE_BECOME_ACTIVE.toString()))
            .andExpect(jsonPath("$.startBalance").value(DEFAULT_START_BALANCE.intValue()))
            .andExpect(jsonPath("$.percent").value(DEFAULT_PERCENT.intValue()))
            .andExpect(jsonPath("$.fine").value(DEFAULT_FINE.intValue()))
            .andExpect(jsonPath("$.successRate").value(DEFAULT_SUCCESS_RATE))
            .andExpect(jsonPath("$.term").value(DEFAULT_TERM))
            .andExpect(jsonPath("$.paymentEvery").value(DEFAULT_PAYMENT_EVERY.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.autoPaymentEnabled").value(DEFAULT_AUTO_PAYMENT_ENABLED.booleanValue()))
            .andExpect(jsonPath("$.capitalization").value(DEFAULT_CAPITALIZATION.booleanValue()))
            .andExpect(jsonPath("$.earlyPayment").value(DEFAULT_EARLY_PAYMENT.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingDeal() throws Exception {
        // Get the deal
        restDealMockMvc.perform(get("/api/deals/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDeal() throws Exception {
        // Initialize the database
        dealRepository.saveAndFlush(deal);

        int databaseSizeBeforeUpdate = dealRepository.findAll().size();

        // Update the deal
        Deal updatedDeal = dealRepository.findById(deal.getId()).get();
        // Disconnect from session so that the updates on updatedDeal are not directly saved in db
        em.detach(updatedDeal);
        updatedDeal
            .dateOpen(UPDATED_DATE_OPEN)
            .dateBecomeActive(UPDATED_DATE_BECOME_ACTIVE)
            .startBalance(UPDATED_START_BALANCE)
            .percent(UPDATED_PERCENT)
            .fine(UPDATED_FINE)
            .successRate(UPDATED_SUCCESS_RATE)
            .term(UPDATED_TERM)
            .paymentEvery(UPDATED_PAYMENT_EVERY)
            .status(UPDATED_STATUS)
            .autoPaymentEnabled(UPDATED_AUTO_PAYMENT_ENABLED)
            .capitalization(UPDATED_CAPITALIZATION)
            .earlyPayment(UPDATED_EARLY_PAYMENT);

        restDealMockMvc.perform(put("/api/deals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedDeal)))
            .andExpect(status().isOk());

        // Validate the Deal in the database
        List<Deal> dealList = dealRepository.findAll();
        assertThat(dealList).hasSize(databaseSizeBeforeUpdate);
        Deal testDeal = dealList.get(dealList.size() - 1);
        assertThat(testDeal.getDateOpen()).isEqualTo(UPDATED_DATE_OPEN);
        assertThat(testDeal.getDateBecomeActive()).isEqualTo(UPDATED_DATE_BECOME_ACTIVE);
        assertThat(testDeal.getStartBalance()).isEqualTo(UPDATED_START_BALANCE);
        assertThat(testDeal.getPercent()).isEqualTo(UPDATED_PERCENT);
        assertThat(testDeal.getFine()).isEqualTo(UPDATED_FINE);
        assertThat(testDeal.getSuccessRate()).isEqualTo(UPDATED_SUCCESS_RATE);
        assertThat(testDeal.getTerm()).isEqualTo(UPDATED_TERM);
        assertThat(testDeal.getPaymentEvery()).isEqualTo(UPDATED_PAYMENT_EVERY);
        assertThat(testDeal.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testDeal.isAutoPaymentEnabled()).isEqualTo(UPDATED_AUTO_PAYMENT_ENABLED);
        assertThat(testDeal.isCapitalization()).isEqualTo(UPDATED_CAPITALIZATION);
        assertThat(testDeal.isEarlyPayment()).isEqualTo(UPDATED_EARLY_PAYMENT);
    }

    @Test
    @Transactional
    public void updateNonExistingDeal() throws Exception {
        int databaseSizeBeforeUpdate = dealRepository.findAll().size();

        // Create the Deal

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDealMockMvc.perform(put("/api/deals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deal)))
            .andExpect(status().isBadRequest());

        // Validate the Deal in the database
        List<Deal> dealList = dealRepository.findAll();
        assertThat(dealList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteDeal() throws Exception {
        // Initialize the database
        dealRepository.saveAndFlush(deal);

        int databaseSizeBeforeDelete = dealRepository.findAll().size();

        // Delete the deal
        restDealMockMvc.perform(delete("/api/deals/{id}", deal.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<Deal> dealList = dealRepository.findAll();
        assertThat(dealList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Deal.class);
        Deal deal1 = new Deal();
        deal1.setId(1L);
        Deal deal2 = new Deal();
        deal2.setId(deal1.getId());
        assertThat(deal1).isEqualTo(deal2);
        deal2.setId(2L);
        assertThat(deal1).isNotEqualTo(deal2);
        deal1.setId(null);
        assertThat(deal1).isNotEqualTo(deal2);
    }
}
