package com.epsilon.metadater.domain;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * CvarVariableDTO
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-09-24T23:44:41.996003200-04:00[America/New_York]")
public class CvarVariableDTO   {
  @JsonProperty("programmeName")
  private String programmeName;

  @JsonProperty("programmeId")
  private Long programmeId;

  @JsonProperty("catId")
  private Long catId;

  @JsonProperty("catName")
  private String catName;

  @JsonProperty("qvRoot")
  private String qvRoot;

  @JsonProperty("qvRootUpper")
  private String qvRootUpper;

  @JsonProperty("scatId")
  private Long scatId;

  @JsonProperty("scatName")
  private String scatName;

  @JsonProperty("scatSeqNo")
  private Integer scatSeqNo;

  @JsonProperty("groupId")
  private Long groupId;

  @JsonProperty("groupName")
  private String groupName;

  @JsonProperty("groupSeqNo")
  private Integer groupSeqNo;

  @JsonProperty("questionId")
  private Long questionId;

  @JsonProperty("questionName")
  private String questionName;

  @JsonProperty("questionSeqNo")
  private Integer questionSeqNo;

  @JsonProperty("responseId")
  private Long responseId;

  @JsonProperty("responseName")
  private String responseName;

  @JsonProperty("responseSeqNo")
  private Integer responseSeqNo;

  @JsonProperty("tkWord")
  private Integer tkWord;

  @JsonProperty("tkBit")
  private Integer tkBit;

  @JsonProperty("qvSuffix")
  private String qvSuffix;

  @JsonProperty("qvSuffixUpper")
  private String qvSuffixUpper;

  @JsonProperty("sasName")
  private String sasName;

  @JsonProperty("sasPName")
  private String sasPName;

  @JsonProperty("answerId")
  private Long answerId;

  public CvarVariableDTO programmeName(String programmeName) {
    this.programmeName = programmeName;
    return this;
  }

  /**
   * Get programmeName
   * @return programmeName
  */
  @ApiModelProperty(value = "")


  public String getProgrammeName() {
    return programmeName;
  }

  public void setProgrammeName(String programmeName) {
    this.programmeName = programmeName;
  }

  public CvarVariableDTO programmeId(Long programmeId) {
    this.programmeId = programmeId;
    return this;
  }

  /**
   * Get programmeId
   * @return programmeId
  */
  @ApiModelProperty(value = "")


  public Long getProgrammeId() {
    return programmeId;
  }

  public void setProgrammeId(Long programmeId) {
    this.programmeId = programmeId;
  }

  public CvarVariableDTO catId(Long catId) {
    this.catId = catId;
    return this;
  }

  /**
   * Get catId
   * @return catId
  */
  @ApiModelProperty(value = "")


  public Long getCatId() {
    return catId;
  }

  public void setCatId(Long catId) {
    this.catId = catId;
  }

  public CvarVariableDTO catName(String catName) {
    this.catName = catName;
    return this;
  }

  /**
   * Get catName
   * @return catName
  */
  @ApiModelProperty(value = "")


  public String getCatName() {
    return catName;
  }

  public void setCatName(String catName) {
    this.catName = catName;
  }

  public CvarVariableDTO qvRoot(String qvRoot) {
    this.qvRoot = qvRoot;
    return this;
  }

  /**
   * Get qvRoot
   * @return qvRoot
  */
  @ApiModelProperty(value = "")


  public String getQvRoot() {
    return qvRoot;
  }

  public void setQvRoot(String qvRoot) {
    this.qvRoot = qvRoot;
  }

  public CvarVariableDTO qvRootUpper(String qvRootUpper) {
    this.qvRootUpper = qvRootUpper;
    return this;
  }

  /**
   * Get qvRootUpper
   * @return qvRootUpper
  */
  @ApiModelProperty(value = "")


  public String getQvRootUpper() {
    return qvRootUpper;
  }

  public void setQvRootUpper(String qvRootUpper) {
    this.qvRootUpper = qvRootUpper;
  }

  public CvarVariableDTO scatId(Long scatId) {
    this.scatId = scatId;
    return this;
  }

  /**
   * Get scatId
   * @return scatId
  */
  @ApiModelProperty(value = "")


  public Long getScatId() {
    return scatId;
  }

  public void setScatId(Long scatId) {
    this.scatId = scatId;
  }

  public CvarVariableDTO scatName(String scatName) {
    this.scatName = scatName;
    return this;
  }

  /**
   * Get scatName
   * @return scatName
  */
  @ApiModelProperty(value = "")


  public String getScatName() {
    return scatName;
  }

  public void setScatName(String scatName) {
    this.scatName = scatName;
  }

  public CvarVariableDTO scatSeqNo(Integer scatSeqNo) {
    this.scatSeqNo = scatSeqNo;
    return this;
  }

  /**
   * Get scatSeqNo
   * @return scatSeqNo
  */
  @ApiModelProperty(value = "")


  public Integer getScatSeqNo() {
    return scatSeqNo;
  }

  public void setScatSeqNo(Integer scatSeqNo) {
    this.scatSeqNo = scatSeqNo;
  }

  public CvarVariableDTO groupId(Long groupId) {
    this.groupId = groupId;
    return this;
  }

  /**
   * Get groupId
   * @return groupId
  */
  @ApiModelProperty(value = "")


  public Long getGroupId() {
    return groupId;
  }

  public void setGroupId(Long groupId) {
    this.groupId = groupId;
  }

  public CvarVariableDTO groupName(String groupName) {
    this.groupName = groupName;
    return this;
  }

  /**
   * Get groupName
   * @return groupName
  */
  @ApiModelProperty(value = "")


  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public CvarVariableDTO groupSeqNo(Integer groupSeqNo) {
    this.groupSeqNo = groupSeqNo;
    return this;
  }

  /**
   * Get groupSeqNo
   * @return groupSeqNo
  */
  @ApiModelProperty(value = "")


  public Integer getGroupSeqNo() {
    return groupSeqNo;
  }

  public void setGroupSeqNo(Integer groupSeqNo) {
    this.groupSeqNo = groupSeqNo;
  }

  public CvarVariableDTO questionId(Long questionId) {
    this.questionId = questionId;
    return this;
  }

  /**
   * Get questionId
   * @return questionId
  */
  @ApiModelProperty(value = "")


  public Long getQuestionId() {
    return questionId;
  }

  public void setQuestionId(Long questionId) {
    this.questionId = questionId;
  }

  public CvarVariableDTO questionName(String questionName) {
    this.questionName = questionName;
    return this;
  }

  /**
   * Get questionName
   * @return questionName
  */
  @ApiModelProperty(value = "")


  public String getQuestionName() {
    return questionName;
  }

  public void setQuestionName(String questionName) {
    this.questionName = questionName;
  }

  public CvarVariableDTO questionSeqNo(Integer questionSeqNo) {
    this.questionSeqNo = questionSeqNo;
    return this;
  }

  /**
   * Get questionSeqNo
   * @return questionSeqNo
  */
  @ApiModelProperty(value = "")


  public Integer getQuestionSeqNo() {
    return questionSeqNo;
  }

  public void setQuestionSeqNo(Integer questionSeqNo) {
    this.questionSeqNo = questionSeqNo;
  }

  public CvarVariableDTO responseId(Long responseId) {
    this.responseId = responseId;
    return this;
  }

  /**
   * Get responseId
   * @return responseId
  */
  @ApiModelProperty(value = "")


  public Long getResponseId() {
    return responseId;
  }

  public void setResponseId(Long responseId) {
    this.responseId = responseId;
  }

  public CvarVariableDTO responseName(String responseName) {
    this.responseName = responseName;
    return this;
  }

  /**
   * Get responseName
   * @return responseName
  */
  @ApiModelProperty(value = "")


  public String getResponseName() {
    return responseName;
  }

  public void setResponseName(String responseName) {
    this.responseName = responseName;
  }

  public CvarVariableDTO responseSeqNo(Integer responseSeqNo) {
    this.responseSeqNo = responseSeqNo;
    return this;
  }

  /**
   * Get responseSeqNo
   * @return responseSeqNo
  */
  @ApiModelProperty(value = "")


  public Integer getResponseSeqNo() {
    return responseSeqNo;
  }

  public void setResponseSeqNo(Integer responseSeqNo) {
    this.responseSeqNo = responseSeqNo;
  }

  public CvarVariableDTO tkWord(Integer tkWord) {
    this.tkWord = tkWord;
    return this;
  }

  /**
   * Get tkWord
   * @return tkWord
  */
  @ApiModelProperty(value = "")


  public Integer getTkWord() {
    return tkWord;
  }

  public void setTkWord(Integer tkWord) {
    this.tkWord = tkWord;
  }

  public CvarVariableDTO tkBit(Integer tkBit) {
    this.tkBit = tkBit;
    return this;
  }

  /**
   * Get tkBit
   * @return tkBit
  */
  @ApiModelProperty(value = "")


  public Integer getTkBit() {
    return tkBit;
  }

  public void setTkBit(Integer tkBit) {
    this.tkBit = tkBit;
  }

  public CvarVariableDTO qvSuffix(String qvSuffix) {
    this.qvSuffix = qvSuffix;
    return this;
  }

  /**
   * Get qvSuffix
   * @return qvSuffix
  */
  @ApiModelProperty(value = "")


  public String getQvSuffix() {
    return qvSuffix;
  }

  public void setQvSuffix(String qvSuffix) {
    this.qvSuffix = qvSuffix;
  }

  public CvarVariableDTO qvSuffixUpper(String qvSuffixUpper) {
    this.qvSuffixUpper = qvSuffixUpper;
    return this;
  }

  /**
   * Get qvSuffixUpper
   * @return qvSuffixUpper
  */
  @ApiModelProperty(value = "")


  public String getQvSuffixUpper() {
    return qvSuffixUpper;
  }

  public void setQvSuffixUpper(String qvSuffixUpper) {
    this.qvSuffixUpper = qvSuffixUpper;
  }

  public CvarVariableDTO sasName(String sasName) {
    this.sasName = sasName;
    return this;
  }

  /**
   * Get sasName
   * @return sasName
  */
  @ApiModelProperty(value = "")


  public String getSasName() {
    return sasName;
  }

  public void setSasName(String sasName) {
    this.sasName = sasName;
  }

  public CvarVariableDTO sasPName(String sasPName) {
    this.sasPName = sasPName;
    return this;
  }

  /**
   * Get sasPName
   * @return sasPName
  */
  @ApiModelProperty(value = "")


  public String getSasPName() {
    return sasPName;
  }

  public void setSasPName(String sasPName) {
    this.sasPName = sasPName;
  }

  public CvarVariableDTO answerId(Long answerId) {
    this.answerId = answerId;
    return this;
  }

  /**
   * Get answerId
   * @return answerId
  */
  @ApiModelProperty(value = "")


  public Long getAnswerId() {
    return answerId;
  }

  public void setAnswerId(Long answerId) {
    this.answerId = answerId;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CvarVariableDTO cvarVariableDTO = (CvarVariableDTO) o;
    return Objects.equals(this.programmeName, cvarVariableDTO.programmeName) &&
        Objects.equals(this.programmeId, cvarVariableDTO.programmeId) &&
        Objects.equals(this.catId, cvarVariableDTO.catId) &&
        Objects.equals(this.catName, cvarVariableDTO.catName) &&
        Objects.equals(this.qvRoot, cvarVariableDTO.qvRoot) &&
        Objects.equals(this.qvRootUpper, cvarVariableDTO.qvRootUpper) &&
        Objects.equals(this.scatId, cvarVariableDTO.scatId) &&
        Objects.equals(this.scatName, cvarVariableDTO.scatName) &&
        Objects.equals(this.scatSeqNo, cvarVariableDTO.scatSeqNo) &&
        Objects.equals(this.groupId, cvarVariableDTO.groupId) &&
        Objects.equals(this.groupName, cvarVariableDTO.groupName) &&
        Objects.equals(this.groupSeqNo, cvarVariableDTO.groupSeqNo) &&
        Objects.equals(this.questionId, cvarVariableDTO.questionId) &&
        Objects.equals(this.questionName, cvarVariableDTO.questionName) &&
        Objects.equals(this.questionSeqNo, cvarVariableDTO.questionSeqNo) &&
        Objects.equals(this.responseId, cvarVariableDTO.responseId) &&
        Objects.equals(this.responseName, cvarVariableDTO.responseName) &&
        Objects.equals(this.responseSeqNo, cvarVariableDTO.responseSeqNo) &&
        Objects.equals(this.tkWord, cvarVariableDTO.tkWord) &&
        Objects.equals(this.tkBit, cvarVariableDTO.tkBit) &&
        Objects.equals(this.qvSuffix, cvarVariableDTO.qvSuffix) &&
        Objects.equals(this.qvSuffixUpper, cvarVariableDTO.qvSuffixUpper) &&
        Objects.equals(this.sasName, cvarVariableDTO.sasName) &&
        Objects.equals(this.sasPName, cvarVariableDTO.sasPName) &&
        Objects.equals(this.answerId, cvarVariableDTO.answerId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(programmeName, programmeId, catId, catName, qvRoot, qvRootUpper, scatId, scatName, scatSeqNo, groupId, groupName, groupSeqNo, questionId, questionName, questionSeqNo, responseId, responseName, responseSeqNo, tkWord, tkBit, qvSuffix, qvSuffixUpper, sasName, sasPName, answerId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CvarVariableDTO {\n");
    
    sb.append("    programmeName: ").append(toIndentedString(programmeName)).append("\n");
    sb.append("    programmeId: ").append(toIndentedString(programmeId)).append("\n");
    sb.append("    catId: ").append(toIndentedString(catId)).append("\n");
    sb.append("    catName: ").append(toIndentedString(catName)).append("\n");
    sb.append("    qvRoot: ").append(toIndentedString(qvRoot)).append("\n");
    sb.append("    qvRootUpper: ").append(toIndentedString(qvRootUpper)).append("\n");
    sb.append("    scatId: ").append(toIndentedString(scatId)).append("\n");
    sb.append("    scatName: ").append(toIndentedString(scatName)).append("\n");
    sb.append("    scatSeqNo: ").append(toIndentedString(scatSeqNo)).append("\n");
    sb.append("    groupId: ").append(toIndentedString(groupId)).append("\n");
    sb.append("    groupName: ").append(toIndentedString(groupName)).append("\n");
    sb.append("    groupSeqNo: ").append(toIndentedString(groupSeqNo)).append("\n");
    sb.append("    questionId: ").append(toIndentedString(questionId)).append("\n");
    sb.append("    questionName: ").append(toIndentedString(questionName)).append("\n");
    sb.append("    questionSeqNo: ").append(toIndentedString(questionSeqNo)).append("\n");
    sb.append("    responseId: ").append(toIndentedString(responseId)).append("\n");
    sb.append("    responseName: ").append(toIndentedString(responseName)).append("\n");
    sb.append("    responseSeqNo: ").append(toIndentedString(responseSeqNo)).append("\n");
    sb.append("    tkWord: ").append(toIndentedString(tkWord)).append("\n");
    sb.append("    tkBit: ").append(toIndentedString(tkBit)).append("\n");
    sb.append("    qvSuffix: ").append(toIndentedString(qvSuffix)).append("\n");
    sb.append("    qvSuffixUpper: ").append(toIndentedString(qvSuffixUpper)).append("\n");
    sb.append("    sasName: ").append(toIndentedString(sasName)).append("\n");
    sb.append("    sasPName: ").append(toIndentedString(sasPName)).append("\n");
    sb.append("    answerId: ").append(toIndentedString(answerId)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

  public CvarVariableDTO(String programmeName, Long programmeId, Long catId,
                  String catName, String qvRoot, String qvRootUpper,
                  Long scatId, String scatName, Integer scatSeqNo,
                  Long groupId, String groupName, Integer groupSeqNo,
                  Long questionId, String questionName, Integer questionSeqNo,
                  Long responseId, String responseName, Integer responseSeqNo,
                  Integer tkWord, Integer tkBit, String qvSuffix,
                  String qvSuffixUpper, String sasName, String sasPName,
                  Long answerId)
  {
    this.programmeName = programmeName;
    this.programmeId = programmeId;
    this.catId = catId;
    this.catName = catName;
    this.qvRoot = qvRoot;
    this.qvRootUpper = qvRootUpper;
    this.scatId = scatId;
    this.scatName = scatName;
    this.scatSeqNo = scatSeqNo;
    this.groupId = groupId;
    this.groupName = groupName;
    this.groupSeqNo = groupSeqNo;
    this.questionId = questionId;
    this.questionName = questionName;
    this.questionSeqNo = questionSeqNo;
    this.responseId = responseId;
    this.responseName = responseName;
    this.responseSeqNo = responseSeqNo;
    this.tkWord = tkWord;
    this.tkBit = tkBit;
    this.qvSuffix = qvSuffix;
    this.qvSuffixUpper = qvSuffixUpper;
    this.sasName = sasName;
    this.sasPName = sasPName;
    this.answerId = answerId;
  }  
}

