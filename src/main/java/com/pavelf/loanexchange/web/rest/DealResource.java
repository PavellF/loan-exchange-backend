package com.pavelf.loanexchange.web.rest;

import com.pavelf.loanexchange.domain.Deal;
import com.pavelf.loanexchange.domain.User;
import com.pavelf.loanexchange.repository.DealRepository;
import com.pavelf.loanexchange.security.SecurityUtils;
import com.pavelf.loanexchange.service.DealService;
import com.pavelf.loanexchange.service.UserService;
import com.pavelf.loanexchange.web.rest.errors.BadRequestAlertException;
import com.pavelf.loanexchange.web.rest.specifications.DealSpecification;
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

import static com.pavelf.loanexchange.security.AuthoritiesConstants.*;

/**
 * REST controller for managing {@link com.pavelf.loanexchange.domain.Deal}.
 */
@RestController
@RequestMapping("/api")
public class DealResource {

    private final Logger log = LoggerFactory.getLogger(DealResource.class);

    private static final String ENTITY_NAME = "deal";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DealRepository dealRepository;

    private final DealService dealService;

    private final UserService userService;

    public DealResource(DealRepository dealRepository, DealService dealService, UserService userService) {
        this.dealRepository = dealRepository;
        this.dealService = dealService;
        this.userService = userService;
    }

    /**
     * {@code POST  /deals} : Create a new deal.
     *
     * @param deal the deal to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new deal,
     * or with status {@code 400 (Bad Request)} if the deal has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/deals")
    @PreAuthorize("hasRole(\"" + ADMIN + "\") OR hasRole(\"" + CREDITOR + "\")")
    public ResponseEntity<Deal> createDeal(@Valid @RequestBody Deal deal) throws URISyntaxException {
        log.debug("REST request to save Deal : {}", deal);
        if (deal.getId() != null) {
            throw new BadRequestAlertException("A new deal cannot already have an ID", ENTITY_NAME, "idexists");
        }

        Deal result = null;

        if (SecurityUtils.isCurrentUserInRole(ADMIN)) {
            result = dealRepository.save(deal);
        } else {
            result = dealService.createDealForCurrentUser(deal);
        }

        return ResponseEntity.created(new URI("/api/deals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /deals} : Updates an existing deal.
     *
     * @param deal the deal to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deal,
     * or with status {@code 400 (Bad Request)} if the deal is not valid,
     * or with status {@code 500 (Internal Server Error)} if the deal couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/deals")
    public ResponseEntity<Deal> updateDeal(@Valid @RequestBody Deal deal) throws URISyntaxException {
        log.debug("REST request to update Deal : {}", deal);
        if (deal.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        Deal result = null;

        if (SecurityUtils.isCurrentUserInRole(ADMIN)) {
            result = dealRepository.save(deal);
        } else if (SecurityUtils.isCurrentUserInRole(DEBTOR)) {
            result = dealService.acceptDeal(deal);
        } else if (SecurityUtils.isCurrentUserInRole(CREDITOR)) {
            result = dealService.updateDeal(deal);
        }

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, deal.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /deals} : get all the deals.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of deals in body.
     */
    @GetMapping("/deals")
    public ResponseEntity<List<Deal>> getAllDeals(Pageable pageable,
                                                  @RequestParam DealSpecification params,
                                                  @RequestParam MultiValueMap<String, String> queryParams,
                                                  UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get a page of Deals");

        if (SecurityUtils.isCurrentUserInRole(DEBTOR)) {

            if (params.getForRecipient() != null) {
                User loggedInUser = userService.getUserWithAuthorities().get();
                params.setForRecipient(loggedInUser.getId());
            }

        } else if (SecurityUtils.isCurrentUserInRole(CREDITOR)) {
            User loggedInUser = userService.getUserWithAuthorities().get();
            params.setForEmitter(loggedInUser.getId());
        }

        Page<Deal> page = dealRepository.findAll(params, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /deals/:id} : get the "id" deal.
     *
     * @param id the id of the deal to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the deal, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/deals/{id}")
    @PreAuthorize("hasRole(\"" + ADMIN + "\")")
    public ResponseEntity<Deal> getDeal(@PathVariable Long id) {
        log.debug("REST request to get Deal : {}", id);
        Optional<Deal> deal = dealRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(deal);
    }

    /**
     * {@code DELETE  /deals/:id} : delete the "id" deal.
     *
     * @param id the id of the deal to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/deals/{id}")
    @PreAuthorize("hasRole(\"" + ADMIN + "\")")
    public ResponseEntity<Void> deleteDeal(@PathVariable Long id) {
        log.debug("REST request to delete Deal : {}", id);
        dealRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName,
            true, ENTITY_NAME, id.toString())).build();
    }
}
