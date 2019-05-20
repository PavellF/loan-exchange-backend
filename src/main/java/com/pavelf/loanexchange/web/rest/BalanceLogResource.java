package com.pavelf.loanexchange.web.rest;

import com.pavelf.loanexchange.domain.BalanceLog;
import com.pavelf.loanexchange.domain.User;
import com.pavelf.loanexchange.repository.BalanceLogRepository;
import com.pavelf.loanexchange.security.SecurityUtils;
import com.pavelf.loanexchange.service.UserService;
import com.pavelf.loanexchange.web.rest.errors.BadRequestAlertException;
import com.pavelf.loanexchange.web.rest.specifications.BalanceLogSpecification;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static com.pavelf.loanexchange.security.AuthoritiesConstants.ADMIN;

/**
 * REST controller for managing {@link com.pavelf.loanexchange.domain.BalanceLog}.
 */
@RestController
@RequestMapping("/api")
public class BalanceLogResource {

    private final Logger log = LoggerFactory.getLogger(BalanceLogResource.class);

    private static final String ENTITY_NAME = "balanceLog";

    private final UserService userService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BalanceLogRepository balanceLogRepository;

    public BalanceLogResource(UserService userService, BalanceLogRepository balanceLogRepository) {
        this.userService = userService;
        this.balanceLogRepository = balanceLogRepository;
    }

    /**
     * {@code POST  /balance-logs} : Create a new balanceLog.
     *
     * @param balanceLog the balanceLog to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new balanceLog, or with status {@code 400 (Bad Request)} if the balanceLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/balance-logs")
    @PreAuthorize("hasRole(\"" + ADMIN + "\")")
    public ResponseEntity<BalanceLog> createBalanceLog(@Valid @RequestBody BalanceLog balanceLog) throws URISyntaxException {
        log.debug("REST request to save BalanceLog : {}", balanceLog);
        if (balanceLog.getId() != null) {
            throw new BadRequestAlertException("A new balanceLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BalanceLog result = balanceLogRepository.save(balanceLog);
        return ResponseEntity.created(new URI("/api/balance-logs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /balance-logs} : Updates an existing balanceLog.
     *
     * @param balanceLog the balanceLog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated balanceLog,
     * or with status {@code 400 (Bad Request)} if the balanceLog is not valid,
     * or with status {@code 500 (Internal Server Error)} if the balanceLog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/balance-logs")
    @PreAuthorize("hasRole(\"" + ADMIN + "\")")
    public ResponseEntity<BalanceLog> updateBalanceLog(@Valid @RequestBody BalanceLog balanceLog) throws URISyntaxException {
        log.debug("REST request to update BalanceLog : {}", balanceLog);
        if (balanceLog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BalanceLog result = balanceLogRepository.save(balanceLog);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, balanceLog.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /balance-logs} : get all the balanceLogs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of balanceLogs in body.
     */
    @GetMapping("/balance-logs")
    public ResponseEntity<List<BalanceLog>> getAllBalanceLogs(Pageable pageable,
                                                              @RequestParam BalanceLogSpecification params,
                                                              @RequestParam MultiValueMap<String, String> queryParams,
                                                              UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get a page of BalanceLogs");

        if (!SecurityUtils.isCurrentUserInRole(ADMIN)) {
            User loggedInUser = userService.getUserWithAuthorities().get();

            if (params.getForUser() != null) {
                params.setForUser(loggedInUser.getId());
            }

            if (params.getForDeal() != null) {
                params.setUserParticipatingInDeal(loggedInUser.getId());
            }

            if (params.getForDeal() == null && params.getForUser() == null) {
                params.setForUser(loggedInUser.getId());
            }
        }

        Page<BalanceLog> page = balanceLogRepository.findAll(params, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /balance-logs/:id} : get the "id" balanceLog.
     *
     * @param id the id of the balanceLog to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the balanceLog, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/balance-logs/{id}")
    @PreAuthorize("hasRole(\"" + ADMIN + "\")")
    public ResponseEntity<BalanceLog> getBalanceLog(@PathVariable Long id) {
        log.debug("REST request to get BalanceLog : {}", id);
        Optional<BalanceLog> balanceLog = balanceLogRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(balanceLog);
    }

    /**
     * {@code DELETE  /balance-logs/:id} : delete the "id" balanceLog.
     *
     * @param id the id of the balanceLog to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/balance-logs/{id}")
    @PreAuthorize("hasRole(\"" + ADMIN + "\")")
    public ResponseEntity<Void> deleteBalanceLog(@PathVariable Long id) {
        log.debug("REST request to delete BalanceLog : {}", id);
        balanceLogRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
