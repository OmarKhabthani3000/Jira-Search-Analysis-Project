package pragya.usc.activation;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import pragya.usc.accesscontrol.Portal;
import pragya.usc.voicemenu.VoiceMenu;

/**
 * Class that represent a template activator
 * 
 * @hibernate.class table="activation"
 * @hibernate.cache usage="read-write"
 */
public class Activation implements Serializable {
    public static String VOICE_MENU = "Voice Menu";

    public static String SINGLE_PROMPT = "Single Prompt";

    public static String DIRECT_SERVICE = "Direct Service";

    public static String VXML_FILE = "VXML File"; // fhsl@26.04.2005

    public static String PORTAL = "Portal";

    private int id;

    private String ani;

    private String dnis;

    private Calendar startDate;

    private Calendar endDate;

    private boolean enable;

    private VoiceMenu voiceMenu;

    private String promptPath;

    private String vxmlPath; // fhsl@26.04.2005

    private int serviceId;

    private int templateId;

    private Portal portal;

    private String accessType;

    private Portal childPortal;

    private long permissionServiceId;

    private Logger logger;

    /**
     * Constructor
     */
    public Activation() {
        logger = Logger.getLogger(this.getClass());

        serviceId = 0;
        templateId = 0;
        permissionServiceId = -1;
        enable = false;
    }
	public Activation(Activation act) {
		this.setAccessType(act.getAccessType());
		this.setAni(act.getAni());
		this.setChildPortal(act.getChildPortal());
		this.setDnis(act.getDnis());
		this.setEnable(act.isEnable());
		this.setEndDate(act.getEndDate());
		this.setId(act.getId());
		this.setPortal(act.getPortal());
		this.setPromptPath(act.getPromptPath());
		this.setServiceId(this.getServiceId());
		this.setStartDate(this.getStartDate());
		this.setTemplateId(this.getTemplateId());
		this.setVoiceMenu(this.getVoiceMenu());
		this.setVxmlPath(this.getVxmlPath());
	}
    /**
     * Destroyer
     */
    public void destroy() {
        startDate = null;
        endDate = null;
        voiceMenu = null;
    }

    /**
     * Get start date
     * 
     * @return startDate
     * 
     * @hibernate.property not-null="false" column="initialdatetime"
     */
    public Calendar getStartDate() {
        return startDate;
    }

    /**
     * Define end date
     * 
     * @param endDate
     */
    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    /**
     * Define start date
     * 
     * @param startDate
     */
    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    /**
     * Get ani
     * 
     * @return ani
     * 
     * @hibernate.property not-null="false" length="30"
     */
    public String getAni() {
        return ani;
    }

    /**
     * Get dnis
     * 
     * @return dnis
     * 
     * @hibernate.property not-null="false" length="30"
     */
    public String getDnis() {
        return dnis;
    }

    /**
     * Define ani
     * 
     * @param ani
     */
    public void setAni(String ani) {
        this.ani = ani;
    }

    /**
     * Define dnis
     * 
     * @param string
     */
    public void setDnis(String dnis) {

        this.dnis = dnis;
    }

    /**
     * Get if is enable
     * 
     * @return enbale
     * 
     * @hibernate.property not-null="false" column="isenable"
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * Get end date
     * 
     * @return endDate
     * 
     * @hibernate.property not-null="false" column="finaldatetime"
     */
    public Calendar getEndDate() {
        return endDate;
    }

    /**
     * Define if is enable
     * 
     * @param enable
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * Get voice menu
     * 
     * @return voiceMenu
     * 
     * @hibernate.many-to-one column="id_voicemenu" lazy="true"
     */
    public VoiceMenu getVoiceMenu() {
        return voiceMenu;
    }

    /**
     * Define voice menu
     * 
     * @param voiceMenu
     */
    public void setVoiceMenu(VoiceMenu voiceMenu) {
        this.voiceMenu = voiceMenu;
    }

    /**
     * Define id
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Format date with format specified
     * 
     * @param pattern
     * @param date
     * 
     * @return formattedDate
     */
    public String getFormattedDate(String pattern, Calendar date) {
        String dt = "";

        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            dt = format.format(date.getTime());
        } catch (NullPointerException e) {
            logger.warn("Invalid format: " + pattern);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid format: " + pattern);
        }

        return dt;
    }

    /**
     * Get id
     * 
     * @return id
     * 
     * @hibernate.id generator-class="identity"
     */
    public int getId() {
        return id;
    }

    /**
     * Get portal
     * 
     * @return portal
     * 
     * @hibernate.many-to-one column="fk_portal" lazy="true"
     */
    public Portal getPortal() {
        return portal;
    }

    /**
     * Define portal
     * 
     * @param portal
     */
    public void setPortal(Portal portal) {
        this.portal = portal;
    }

    /**
     * Get prompt path
     * 
     * @return promptPath
     * 
     * @hibernate.property not-null="false" length="200" column="path_prompt"
     */
    public String getPromptPath() {
        return promptPath;
    }

    /**
     * Define prompt path
     * 
     * @param promptPath
     */
    public void setPromptPath(String promptPath) {
        this.promptPath = promptPath;
    }

    /**
     * Get service id
     * 
     * @return serviceId
     * 
     * @hibernate.property not-null="false" column="id_service"
     * 
     */
    public int getServiceId() {
        return serviceId;
    }

    /**
     * Get template id
     * 
     * @return templateId
     * 
     * @hibernate.property not-null="false" column="id_template"
     */
    public int getTemplateId() {
        return templateId;
    }

    /**
     * Define service id
     * 
     * @param serviceId
     */
    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * Define template id
     * 
     * @param templateId
     */
    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    /**
     * Get access type
     * 
     * @return accessType
     * 
     * @hibernate.property not-null="false" length="20" column="type_access"
     */
    public String getAccessType() {
        return accessType;
    }

    /**
     * Define access type
     * 
     * @param accessType
     */
    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    /**
     * Define VXML Path if the activation is a VXML File
     * 
     * @return Returns the vxmlPath.
     * 
     * @hibernate.property not-null="false" length="200" column="path_vxml"
     */
    public String getVxmlPath() {
        return vxmlPath;
    }

    /**
     * Get VXML Path if the activation is a VXML File
     * 
     * @param vxmlPath
     *            The vxmlPath to set.
     */
    public void setVxmlPath(String vxmlPath) {
        this.vxmlPath = vxmlPath;
    }

    /**
     * Get portal
     * 
     * @return portal
     * 
     * @hibernate.many-to-one column="id_childportal" lazy="true"
     */
    public Portal getChildPortal() {
        return childPortal;
    }

    public void setChildPortal(Portal childPortal) {
        this.childPortal = childPortal;
    }

    /**
     * @return Returns the permissionServiceId.
     * @hibernate.property column = "fk_id_permissionservice"
     */
    public long getPermissionServiceId() {
        return permissionServiceId;
    }

    /**
     * @param permissionServiceId
     *            The permissionServiceId to set.
     */
    public void setPermissionServiceId(long permissionServiceId) {
        this.permissionServiceId = permissionServiceId;
    }
}