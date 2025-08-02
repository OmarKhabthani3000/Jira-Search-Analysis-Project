package tavant.twms.domain.customReports;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.persistence.Entity;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.SecurityHelper;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Dec 10, 2008
 * Time: 11:45:28 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class ReportSection implements Comparable<ReportSection>, AuditableColumns {
    @Id
    @GeneratedValue(generator = "ReportSection")
	@GenericGenerator(name = "ReportSection", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "REPORT_SECTION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20"),
			@Parameter(name = "optimizer", value = "pooled") })
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "section_order",nullable = false)
    private Integer order;

    @OneToMany(cascade = {javax.persistence.CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinTable(name = "questionnaire", joinColumns = @JoinColumn(name = "for_section"))
    @Filter(name="excludeInactive")
    private List<ReportFormQuestion> questionnaire = new ArrayList<ReportFormQuestion>();

    @OneToMany(fetch = FetchType.LAZY )
    @Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @JoinTable(name = "Section_i18n_text",
            joinColumns = @JoinColumn(name = "Report_Section"),
            inverseJoinColumns = @JoinColumn(name = "i18n_text"))
    private List<I18NReportSectionText> i18nReportSectionTexts = new ArrayList<I18NReportSectionText>();

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        String i18nName = null;
        for (I18NReportSectionText i18nSectionText : getI18nReportSectionTexts()) {
			if (i18nSectionText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) ) {
				i18nName=i18nSectionText.getDescription();
                break;
            }
			else if(i18nSectionText.getLocale().equalsIgnoreCase("en_US")) {
				i18nName = i18nSectionText.getDescription();
			}

		}
        if(StringUtils.hasText(i18nName)){
            return i18nName;
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<ReportFormQuestion> getQuestionnaire() {
       if(!questionnaire.isEmpty()){
            Collections.sort(questionnaire);
        }
        return questionnaire;
    }

    public void setQuestionnaire(List<ReportFormQuestion> questionnaire) {
        this.questionnaire = questionnaire;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public int compareTo(ReportSection otherSection) {
        if (this.order != null) {
            return this.order.compareTo(otherSection.getOrder());
        } else {
            return -1;
        }
    }

    public List<I18NReportSectionText> getI18nReportSectionTexts() {
        return i18nReportSectionTexts;
    }

    public void setI18nReportSectionTexts(List<I18NReportSectionText> i18nReportSectionTexts) {
        this.i18nReportSectionTexts = i18nReportSectionTexts;
    }
}