package com.epsilon.metadater.web.api;

import java.lang.Integer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.Query;
import javax.persistence.ColumnResult;
import javax.persistence.EntityManager;
import javax.persistence.ConstructorResult;
import javax.persistence.PersistenceContext;
import javax.persistence.SqlResultSetMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.*;
import org.hibernate.cfg.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import com.epsilon.metadater.domain.CvarVariableDTO;
import com.epsilon.metadater.domain.InvalidResponse;
import com.epsilon.metadater.repository.CvarVariableRepository;
import com.epsilon.metadater.web.api.CvarVariableApiDelegate;

/**
 * A delegate to be called by the {@link CvarVariableApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Service
public class CvarVariableApiDelegateImpl implements CvarVariableApiDelegate {

  private final Logger log = LoggerFactory.getLogger(CvarVariableApiDelegateImpl.class);

    private CvarVariableRepository cvarRepository;
  
    public CvarVariableApiDelegateImpl(CvarVariableRepository cvarRepository) {
      this.cvarRepository = cvarRepository;
    }

    /**
     * GET /cvar-variable/
     * Returns a list of CvarVariables
     *
     * @param id Programme ID to get cvars for (required)
     * @return Successfully returned a list of CvarVariables (status code 200)
     *         or Invalid request (status code 400)
     * @see CvarVariableApi#getCvarVariableList
     * 
    public ResponseEntity<List<CvarVariableDTO>> getCvarVariableList(Integer id) {
      log.debug("REST request to get CvarVariables for: id={}", id);
      return ResponseEntity.ok(cvarRepository.findByProgrammeId(id));
    }
     */

    /**
     * GET /cvar-variable/
     * Returns a list of CvarVariables
     *
     * @param id Programme ID to get cvars for (required)
     * @param pageable Pageable query parameters (optional)
     * @return Successfully returned a list of CvarVariables (status code 200)
     *         or Invalid request (status code 400)
     * @see CvarVariableApi#getCvarVariableList
     */
    @Override
    public ResponseEntity<List<CvarVariableDTO>> getCvarVariableList(Integer id, Pageable pageable) {
      log.debug("REST request to get CvarVariables for: id={}, pageable={}", id,  pageable);
      Page<CvarVariableDTO> page = cvarRepository.findByProgrammeId(id, pageable);
      //Page<CvarVariableDTO> page = cvarRepository.findByProgrammeId(id, pageable.getPageNumber(), pageable.getPageSize());
      HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
      return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET /cvar-variable/
     * Returns a list of CvarVariables
     *
     * @param id Programme ID to get cvars for (required)
     * @param page Page number of the requested page (optional)
     * @param size Size of a page (optional)
     * @return Successfully returned a list of CvarVariables (status code 200)
     *         or Invalid request (status code 400)
     * @see CvarVariableApi#getCvarVariableList
     * 
    public ResponseEntity<List<CvarVariableDTO>> getCvarVariableList(Integer id, Integer pageNumber, Integer pageSize) {
      log.debug("REST request to get CvarVariables for: id={}, pageNumber={}, pageSize={}", id, pageNumber, pageSize);
      Page<CvarVariableDTO> page = cvarRepository.findByProgrammeId(id, pageNumber, pageSize);
      HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
      return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
     */    

    /**
     * {@code GET  /collection-vehicles} : get all the collectionVehicles.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of collectionVehicles in body.
     * 
    @GetMapping("/collection-vehicles")
    public ResponseEntity<List<CollectionVehicle>> getAllCollectionVehicles(CollectionVehicleCriteria criteria, Pageable pageable) {
        log.debug("REST request to get CollectionVehicles by criteria: {}", criteria);
        Page<CollectionVehicle> page = collectionVehicleQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
     */

}
