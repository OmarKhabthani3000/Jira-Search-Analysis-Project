@Entity
@Table(name = "LOGIN_HISTORY")
@GenericGenerator(name = "custom-id-generator",        strategy = "sequence-identity",
        parameters = @org.hibernate.annotations.Parameter(name = "sequence", value = "CUSTOM_SEQUENCE"))
public class UserLoginHistoryEntry implements Identifiable {
    private Long id;
    private String loginId;
    private String userIP;
    private String sessionID;
    private String accessURL;
    private DateTime loginDate;
    private DateTime logoutDate;
    private String logoutReason;
    private String serverName;
    private String serverIP;
    private String fullName;
    private String activeRoles;

    @Override
    @Id
    @GeneratedValue(generator = "custom-id-generator")
    @Column(name = "ROW_IDENTIFIER", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "USER_IP")
    public String getUserIP() {
        return userIP;
    }

    public void setUserIP(String userIP) {
        this.userIP = userIP;
    }

    @Column(name = "SESSION_ID")
    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    @Column(name = "ACCESS_URL")
    public String getAccessURL() {
        return accessURL;
    }

    public void setAccessURL(String accessURL) {
        this.accessURL = accessURL;
    }

    @Column(name = "LOGIN_DATE")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime", parameters = @Parameter(name = "javaZone", value = "UTC"))
    public DateTime getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(DateTime loginDate) {
        this.loginDate = loginDate;
    }

    @Column(name = "LOGOUT_DATE")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime", parameters = @Parameter(name = "javaZone", value = "UTC"))
    public DateTime getLogoutDate() {
        return logoutDate;
    }

    public void setLogoutDate(DateTime logoutDate) {
        this.logoutDate = logoutDate;
    }

    @Column(name = "LOGOUT_REASON")
    public String getLogoutReason() {
        return logoutReason;
    }

    public void setLogoutReason(String logoutReason) {
        this.logoutReason = logoutReason;
    }

    @Column(name = "SERVER_NAME")
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Column(name = "SERVER_IP")
    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    @Column(name = "LOGIN_ID")
    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    @Column(name = "FULL_NAME")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name = "ACTIVE_ROLES")
    public String getActiveRoles() {
        return activeRoles;
    }

    public void setActiveRoles(String activeRoles) {
        this.activeRoles = activeRoles;
    }
}