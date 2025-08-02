package com.epsilon.metadater.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.epsilon.metadater.domain.CvarVariable;
import com.epsilon.metadater.domain.CvarVariableDTO;

/**
 * Spring Data SQL repository for CvarVariable models.
 */
@Repository
public interface CvarVariableRepository extends JpaRepository<CvarVariable, Long>, JpaSpecificationExecutor<CvarVariable> {

  @Query(name="CvarVariable.findByProgrammeId", nativeQuery = true)
  public List<CvarVariableDTO> findByProgrammeId(@Param("programmeId") int programmeId);

  @Query(name="CvarVariable.findByProgrammeId", countQuery = "CvarVariable.findByProgrammeId.count", nativeQuery = true)
  public List<CvarVariableDTO> findByProgrammeId(@Param("programmeId") int programmeId, @Param("page") int page, @Param("size") int size);

  @Query(name="CvarVariable.findByProgrammeId", countQuery = "CvarVariable.findByProgrammeId.count", nativeQuery = true)
  public Page<CvarVariableDTO> findByProgrammeId(@Param("programmeId") int programmeId, @Param("pageable") Pageable pageable);

}
