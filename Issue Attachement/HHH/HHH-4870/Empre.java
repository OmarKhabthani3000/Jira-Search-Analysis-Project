package org.kyrian.entity.muvale;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Formula;
import org.kyrian.entity.afiliacion.AfiObservacion;
import org.kyrian.entity.comercial.GridEmpre;
import org.kyrian.entity.gnomo.listener.EmpreListener;
import org.kyrian.entity.rrhh.RhDatosPersonales;
import org.kyrian.util.constants.NamedQueryIdentifiers;
import org.kyrian.util.converter.StringCapitalizeConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 *
 * @author marcial
 */
@Entity
@XStreamAlias(value="Empre")
@EntityListeners(EmpreListener.class)
@Table(schema = "MUVALE", name = "EMPRE")
public class Empre implements Serializable {

	public static final BigInteger AUTONOMO = BigInteger.valueOf(2);
	public static final BigInteger EMPRESA_MUTUA = BigInteger.valueOf(1);
	public static final BigInteger EMPRESA_CAPTACION = BigInteger.valueOf(3);
	
	
	/**
	 *
	 */
	private static final long serialVersionUID = -5767239350008193120L;

	//START-ENTITY-MOD
	
	
	//END-ENTITY-MOD
	
	@Column(name = "DOC", nullable = false)
	private BigInteger doc;
	@Column(name = "NIF", nullable = false)
	private String nif;
	@Column(name = "NOMBRE",length=80)
	@XStreamConverter(value = StringCapitalizeConverter.class)
	private String nombre;
	@Column(name = "NOMBREC",length=40)
	@XStreamConverter(value = StringCapitalizeConverter.class)
	private String nombrec;
	@Column(name = "DOMIC")
	@XStreamConverter(value = StringCapitalizeConverter.class)
	private String domic;
	@Column(name = "PROVI")
	private BigInteger provi;
	@Column(name = "POBLA")
	private BigInteger pobla;
	@Column(name = "DISPO")
	private BigInteger dispo;
	@Column(name = "TELEF1")
	private String telef1;
	@Column(name = "TELEF2")
	private String telef2;
	@Column(name = "FAX")
	private String fax;
	@Column(name = "UPC")
	private BigInteger upc;
	
	@JoinColumn(name = "UPC", referencedColumnName = "CLAVE_PRO",insertable=false,updatable=false)
	@ManyToOne(fetch=FetchType.LAZY)
	private RhDatosPersonales upcRrhh;
	
	@Column(name = "CENTRA")
	private BigInteger centra;
	@Column(name = "EMAIL")
	private String email;
	@Column(name = "CONTACTO")
 	@XStreamConverter(value = StringCapitalizeConverter.class)
	private String contacto;
	@Column(name = "USUALT")
	private BigInteger usualt;
	@Column(name = "USUMOD")
	private BigInteger usumod;
	@Id
	@Column(name = "NUM", nullable = false)
	@GeneratedValue(generator="SeqEmpre")
    @SequenceGenerator(name="SeqEmpre",sequenceName="MUVALE.NUME", allocationSize=1)
	private Long num;
	@Column(name = "NIF_ORI")
	private String nifOri;
	@Column(name = "NIF_DES")
	private String nifDes;
	@Column(name = "VIP")
	private Long vip;
	@Column(name = "F_ENTREGA")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fEntrega;
	@Column(name = "F_CLAVE_W")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fClaveW;
	@Column(name = "F_PLAN_ACOGIDA")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fPlanAcogida;
	@Column(name = "MUT_ORIGEN")
	private BigInteger mutOrigen;
	@Column(name = "TIP_NIF")
	private BigInteger tipNif;
	@Column(name = "NUM_VINCULADO")
	private BigInteger numVinculado;
	@Column(name = "CAPTAR_NUTRA")
	private BigInteger captarNutra;
	
	@Formula(value="(select e.nombre from muvale.empre e where e.num = num_vinculado)")
	@XStreamConverter(value = StringCapitalizeConverter.class)
	private String nombreEmpreVinculada;
	
	@Column(name = "CAPTAR_NCENTROS")
	private BigInteger captarNcentros;
	@OneToMany(mappedBy = "num", fetch = FetchType.LAZY)
	private Set<Centra> centraCollection;
	@OneToMany(mappedBy = "empre", fetch = FetchType.LAZY)
	private Set<Docu> docuCollection;
	@JoinColumn(name = "EMP_CEN_DOM", referencedColumnName = "CE_ID")
	@ManyToOne (fetch= FetchType.LAZY)
	private Centra empCenDom;
	@JoinColumn(name = "EMP_CEN_ENV", referencedColumnName = "CE_ID")
	@ManyToOne (fetch= FetchType.LAZY)
	private Centra empCenEnv;
	@JoinColumn(name = "ASELAB", referencedColumnName = "CLACO")
	@ManyToOne (fetch= FetchType.LAZY)
	private Colab aselab;
	/*
	@OneToMany(mappedBy = "empReferencia")
	private Set<Empre> empreCollection;
	@JoinColumn(name = "EMP_REFERENCIA", referencedColumnName = "NUM")
	@ManyToOne
	private Empre empReferencia;
	*/
	@JoinColumn(name = "GRUPOE", referencedColumnName = "GRUPOE")
	@ManyToOne (fetch= FetchType.LAZY)
	private Grupoe grupoe;

	//START-ENTITY-MOD
	@JoinColumn(name = "FIRMA", referencedColumnName = "CLACO")
	@ManyToOne (fetch= FetchType.LAZY)
	private Colab firma;
	
//	@JoinColumn(name = "FIRMA", referencedColumnName = "CLAVE_PRO")
//	@ManyToOne (fetch= FetchType.LAZY)
//	private RhDatosPersonales firma;

	@Column(name = "EMP_F_FIRMA")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fFirma;
	
	@Formula(value="(nvl(" +
			" (select max(ae.aee_fecha) " + 
			" from afiliacion.afi_empre_estado ae " + 
			" 	where ae.aee_empre = num and ae.aee_estado = 2)" +
			" , EMP_F_FIRMA))")
	private Date fechaUltimaFirma;

	@JoinColumn(name = "EMP_USR_FIRMA", referencedColumnName = "NUSUARIO")
	@ManyToOne (fetch= FetchType.LAZY)
	private Usuarios usuarioFirma;
	
	@Column(name = "EMP_F_EFECTO")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fEfectoFirma;

	@JoinColumns({
		@JoinColumn(name = "UPC", referencedColumnName = "CLACO", insertable = false, updatable = false)})
    @OneToOne(fetch= FetchType.LAZY)
    private Colab colabUPC;

    @OneToMany(mappedBy = "empresa")
    private Collection<Prcontra> prcontraCollection;

    @JoinColumns( {
		@JoinColumn(name = "PROVI", referencedColumnName = "PROVI", insertable = false, updatable = false),
		@JoinColumn(name = "POBLA", referencedColumnName = "POBLA", insertable = false, updatable = false),
		@JoinColumn(name = "DISPO", referencedColumnName = "DISPO", insertable = false, updatable = false) })
	@ManyToOne (fetch= FetchType.LAZY)		
	private Pobla poblacion;

    @Formula(value="(lpad(provi, 2, '0') || lpad(pobla, 3, '0'))")
    private String codigoPostal;
    
    @Formula(value="(muvale.nombre_dt(muvale.dt_poblacion(provi,pobla,dispo)))")
 	@XStreamConverter(value = StringCapitalizeConverter.class)
    private String nombreDt;

    @Formula(value="(select " +
    		"       trim(domic) || chr(13) || ', ' || lpad(provi, 2  , '0') || lpad(pobla, 3, '0') || ' ' || " +
    		"       pobla.trapo1 || ' (' || trim(provi.texto) || ')' "+
    		"  from muvale.POBLA pobla " +
    		"  join muvale.PROVI provi on pobla.provi = provi.provi " +
    		" where provi = pobla.provi and pobla = pobla.pobla and dispo = pobla.dispo " +
    		" )")
 	@XStreamConverter(value = StringCapitalizeConverter.class)
	private String direccion;
  
    @Formula(value="( nvl(CAPTAR_NUTRA, 0) )")
	private Integer numTrabajadores;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EMP_F_ESTADO")
	private Date fechaUltimoEstado;
		
	@Column(name="EMP_ESTADO")
	private Integer estadoId;

	@Formula(value = "(DECODE(DOC,2,DECODE(EMP_ESTADO,8,1,3),EMP_ESTADO))")
	private Integer ultimoEstadoId;

	@Formula(value="( select DECODE(DOC,2,DECODE(EMP_ESTADO,8,'No umivale',A.AE_DESC),A.AE_DESC)  FROM AFILIACION.AFI_ESTADO A WHERE A.AE_ID = EMP_ESTADO )")
	private String ultimoEstadoDesc;
	
	@JoinColumn(name = "CAPTAR_MUCES", referencedColumnName = "MU_ID")
	@ManyToOne (fetch= FetchType.LAZY)
	private Mutua mutuaCesante;

	@JoinColumn(name = "EMP_CNAE3", referencedColumnName = "CNAE_3")
	@ManyToOne (fetch= FetchType.LAZY)
	// @Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
	private Cnae3 cnae;

	@Column(name = "EMP_WEB")
	private String web;
	
	@JoinTable(name = "AFILIACION.AFI_OBSERV_EMPRE", joinColumns = {@JoinColumn(name = "AOE_EMPRE", referencedColumnName = "NUM")}, inverseJoinColumns = {@JoinColumn(name = "AOE_OBSERV", referencedColumnName = "AO_ID")})
    @OneToMany(fetch= FetchType.LAZY, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
	private Set<AfiObservacion> observaciones;

	@OneToMany(fetch= FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy="geEmpre")
	private Set<GridEmpre> gridEmpreCollection;
	
	@Formula(value="(DECODE(DOC,2,DECODE(EMP_ESTADO,8,1,3),EMP_ESTADO))")
	private Integer ultimoEstadoComercialId;
	
	@SuppressWarnings("unused")
	@Transient
	private EmpreEstado ultimoEstadoComercial;
	
	/**
	 * Saco aquellas cuentas que estén asociadas a un docu activo
	 * siempre y cuando el motivo de baja no sea el 79 (Sin trabajadores)
	 * y que tenga una fecha de baja superior al dia de hoy
	 * 
	 */
	@Formula(value="( select distinct 'S' " +
				   "	from muvale.ccc c" +
				   "   inner join muvale.docu d on c.doc = d.doc and c.numdoc = d.numdoc " +
				   "   where d.doc_num = num " +
				   "     and nvl(d.f_Baja,'01/01/2099') between sysdate and '01/01/2099' " +
				   "     and c.f_bajamut  > sysdate" +
				   "	 and c.mobaj <> 79 )")
	private String cccConBajaSolicitada;
	
	/**
	 * Para empresas que tengan el cif incorrecto solo nos podremos planificar actuaciones
	 * siempre y cuando no hayamos supero el número de 3 actuaciones en el periodo de un
	 * año
	 */
	@Formula(value="(" +
			"	select distinct 'N' " +
			"     from gnomo.pl_actividad_empre ae " +
			"    inner join gnomo.gn_pl_actuacion a on ae.pae_actividad = a.ac_id " +
			"    where ae.pae_empre = num " +
			"      and (lpad(to_char(num),10,'0') = nif or muvale.Cmn_Pck_Utiles.escif(nif) = 0 ) " +
			"      and a.ac_fecha1 >= (sysdate - 365) "+
			"    group by ae.pae_actividad " +
			"   having count(1) >= 3)")
	private String puedoPlanificarme;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CAPTAR_VTO")
	private Date captarVto;
	
	@JoinColumn(name = "EMP_SPEXTERNO", referencedColumnName = "SPE_COD")
	@ManyToOne (fetch= FetchType.LAZY)
	private PreSpexterno spExternoET;
	
	@JoinColumn(name = "EMP_SP_VDS", referencedColumnName = "SPE_COD")
	@ManyToOne (fetch= FetchType.LAZY)
	private PreSpexterno spExternoVdS;
	
	@Column(name = "EMP_SP_ET_PROPIO")
	private Long spPropioET;
	
	@Column(name = "EMP_SP_VDS_PROPIO")
	private Long spPropioVdS;
	
	@Column(name = "EMP_VER_TOPSPA")
	private Long verTopSPA;
	
	//¿Tiene al menos un contrato activo de ET con umivale prevención?
	@Formula(value="(select count(1) from muvale.prcontra p " +
		" where p.doc = doc " +  
		" and p.nif = nif " + 
		" and p.tipo = 1 " + // tipo 1 -> ET
		" and p.f_baja is null " + 
		" and rownum <= 1) ")
    private Long tieneContratoETActivo;
	
	//¿Tiene al menos un contrato activo de VdS con umivale prevención?
	@Formula(value="(select count(1) from muvale.prcontra p " +
			" where p.doc = doc " +  
			" and p.nif = nif " + 
			" and p.tipo = 2 " + // tipo 2 -> VdS
			" and p.f_baja is null " + 
			" and rownum <= 1) ")
	private Long tieneContratoVdSActivo;
	
	@JoinColumn(name = "EMP_GEST_ABS", referencedColumnName = "NUSUARIO")
	@ManyToOne (fetch= FetchType.LAZY)
	private Usuarios gestorAbsentismo;
	
	@Transient
	private String nombrePobla;
	
	@Formula(value="(muvale.cmn_pck_utiles.escif(nif)) ")
    private Long cifValido;

	@Formula(value="(decode(ltrim(nif, '0'), to_char(num), 1, 0))")
    private Long cifDesconocido;
	
	@Formula(value="(select trim(da.nomper) || ' ' || trim(da.ap1per) || ' ' || trim(da.ap2per) " +
			" from muvale.datpera da " +
			" where da.nif = nif and rownum <= 1) ")
	@XStreamConverter(value = StringCapitalizeConverter.class)
	private String nombreAutonomo;

	@Formula(value="(" +
			" select MAX(D.F_ALTA) " +
			"   from muvale.docu d " +
			"  where d.doc_num = NUM " +
			") ")
	private Date fAltaUltimoDocu;

	@Formula(value="(" +
			" select MAX(D.F_BAJA) " +
			"   from muvale.docu d " +
			"  where d.doc_num = NUM " +
			") ")
	private Date fBajaUltimoDocu;


	@Formula(value="(select trim(m.conom) from muvale.mutua m where CAPTAR_MUCES = m.mu_id)")
	@XStreamConverter(value = StringCapitalizeConverter.class)
	private String nombreMutua;
	
	@Formula(value="(to_number(to_char(captar_Vto, 'MM')) )")
	private Integer vto;
	
	@Formula(value="(select dp.apellido1 || ' ' || dp.apellido2 || ', ' || dp.nombre " +
					"  from RRHH.rh_datos_personales dp " +
					" where dp.clave_pro = upc)")
	@XStreamConverter(value = StringCapitalizeConverter.class)
	private String nomGds;
	
    @Formula(value = "(select dp.iniciales from RRHH.RH_DATOS_PERSONALES dp where dp.clave_pro = upc)")
    private String inicialesGds;
    
    @Formula(value = "(nvl(upc, 0))")
    private Integer idGds;

	@Formula(value="(" +
						"select max(a.ac_f_fin_ejec) "+
						"  from gnomo.gn_pl_actuacion a "+
						" inner join gnomo.pl_actividad_empre ae on a.ac_id = ae.pae_actividad "+
						" where ae.pae_empre = num  "+
						"   and a.ac_f_fin_ejec is not null" +
					")")
	private Date ultimaAct;
	
	@Formula(value="(" +
						"select min(nvl(a.ac_Fecha1,a.ac_f_fin_vto)) "+
						"  from gnomo.gn_pl_actuacion a "+
						" inner join gnomo.pl_actividad_empre ae on a.ac_id = ae.pae_actividad "+
						" where ae.pae_empre = num  "+
						"   and nvl(a.ac_Fecha1,a.ac_f_fin_vto) > sysdate " +
					")")
	private Date proximaAct;
	
	@Formula(value="(" +
			"SELECT 'Corporativa' " +
			" FROM MUVALE.DOCU D " +
			"INNER JOIN MUVALE.CCC C ON  D.DOC=C.DOC AND D.NUMDOC=C.NUMDOC " +
			"WHERE D.DOC_NUM= NUM " +
			"  AND D.DOC = 1 " +
			"  AND nvl(c.F_BAJAMUT,'01/01/2099') between sysdate and '01/01/2099' " +
			"  AND C.TIPO_CTA='C' " +
			"  AND ROWNUM <= 1 "+
			")")
	private String tipoCuenta;
	
	@Formula(value= "(select c.conat "+
					"  from muvale.docu d "+
					" inner join muvale.ccc c on d.doc = c.doc and d.numdoc = c.numdoc "+ 
					" where d.doc = 2 "+
					"   and d.doc_num = num "+
					"   and not exists (select 1  "+
					"                     from muvale.docu d2 "+ 
					"                    where d2.doc_num = num "+
					"                      and d2.f_alta > d.f_alta)" +
					"   and rownum <= 1) ")
	private String autonomoConAT;
	
	@Formula(value= "(select p.texto from muvale.provi p where p.provi = provi) ")
	private String nombreProvi;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="F_ULTMOD",insertable=false,updatable=false)
	private Date fUltmod;
	
	
	@Formula(value="( select decode(count(1), 0, 'N', 'S') from comercial.grid_jd jd where jd.GJ_EMPRE = NUM)")
	private String miembroJD;
	
	@JoinTable(name = "PRE_EMPRE_ESPECIALIDAD",
			schema="MUVALE", 
			joinColumns = {@JoinColumn(name = "PEE_EMPRE", referencedColumnName = "NUM")}, 
			inverseJoinColumns = {@JoinColumn(name = "PEE_ESPECIALIDAD", referencedColumnName = "PE_ID")})
    @ManyToMany(fetch= FetchType.LAZY)
	private Set<PreEspecialidades> especialidadesTecnicas;
	
	@Formula(value=
		    "(" +
			"	select max(decode(ASELAB,cc.colab,'CIF','CCC')) " +
			"  	  from muvale.docu d " +
			"	 inner join muvale.ccc cc on cc.doc=d.doc and cc.numdoc = d.numdoc and nvl(cc.F_BAJAMUT,'01/01/2099') between sysdate and '01/01/2099' " +
			"	 where DOC = 1 " +
			"      and d.doc_Num = NUM " +
			"      and nvl(d.f_baja,'01/01/2099') = '01/01/2099'" +
			")")
	private String nivel;  //EMPRESA GESTIONADA POR EL DESPACHO A NIVEL DE CIF O CCC Para empresas activas

	@Formula(value=
			"(select dp.iniciales " +
			"   from muvale.docu d " +
			"  inner join muvale.ccc c on c.doc = d.doc and c.numdoc = d.numdoc " +
			"  inner join rrhh.rh_datos_personales dp on dp.clave_pro = c.inter " +
			"  where d.doc_num = NUM  " +
			"    and not exists( " +
			" 		select 1 " +
			"         from muvale.docu d2 " +
			" 	  	 where d.doc = d2.doc " +
			" 	       and d.numdoc = d2.numdoc " +
			"	       and d.f_alta < d.f_alta) " +
			"    and not exists (" +
			"		select 1 " +
			"         from muvale.ccc c2 " +
			"		where c2.ccc_id = c.ccc_id and c2.f_altamut > c.f_altamut" +
			"	 ) " +
			"    and DOC = 2 " +
			"    and rownum <= 1" +
			")")
	private String inicialesGdSCCC;
	
	@Formula(value=
			"(select nvl2(dp.nombre,dp.apellido1 || ' ' || dp.apellido2 || ', ' || dp.nombre,null) " +
			"   from muvale.docu d " +
			"  inner join muvale.ccc c on c.doc = d.doc and c.numdoc = d.numdoc " +
			"  inner join rrhh.rh_datos_personales dp on dp.clave_pro = c.inter " +
			"  where d.doc_num = NUM  " +
			"    and not exists( " +
			" 		select 1 " +
			"         from muvale.docu d2 " +
			" 	  	 where d.doc = d2.doc " +
			" 	       and d.numdoc = d2.numdoc " +
			"	       and d.f_alta < d.f_alta) " +
			"    and not exists (" +
			"		select 1 " +
			"         from muvale.ccc c2 " +
			"		where c2.ccc_id = c.ccc_id and c2.f_altamut > c.f_altamut" +
			"	 ) " +
			"    and DOC = 2 " +
			"    and rownum <= 1)")
	@XStreamConverter(value = StringCapitalizeConverter.class)
	private String nomGdSCCC;
	
	@Formula(value=
			"(select dp.clave_pro " + 
			"   from muvale.docu d " +
			"  inner join muvale.ccc c on c.doc = d.doc and c.numdoc = d.numdoc " +
			"  inner join rrhh.rh_datos_personales dp on dp.clave_pro = c.inter " +
			"  where d.doc_num = NUM  " +
			"    and not exists( " +
			" 		select 1 " +
			"         from muvale.docu d2 " +
			" 	  	 where d.doc = d2.doc " +
			" 	       and d.numdoc = d2.numdoc " +
			"	       and d.f_alta < d.f_alta) " +
			"    and not exists (" +
			"		select 1 " +
			"         from muvale.ccc c2 " +
			"		where c2.ccc_id = c.ccc_id and c2.f_altamut > c.f_altamut" +
			"	 ) " +
			"    and DOC = 2 " +
			"    and rownum <= 1)")
	private Long idGdSCCC;
	
	@Column(name="emp_ver_top_spa_desc",length=1000)
	private String empVerTopSpaMotivo;
		
	//END-ENTITY-MOD
	

	public Empre() {
	}

	public Empre(Long num) {
		this.num = num;
	}

	public Empre(Long num, BigInteger doc, String nif) {
		this.num = num;
		this.doc = doc;
		this.nif = nif;
	}
	
	/**
	 * Constructor para #Colab.getEmpresasDelDespacho#
	 */
	public Empre(Long num, String nif, String nombre, Integer ultimoEstadoId, String ultimoEstadoDesc, BigInteger doc, String nomGds, String inicialesGds, Integer idGds, BigInteger upc) {
		this.num = num;
		this.nombre = nombre;
		this.nif = nif;
		this.ultimoEstadoId = ultimoEstadoId;
		this.ultimoEstadoDesc = ultimoEstadoDesc;
		this.doc = doc;
		this.nomGds = nomGds;
		this.inicialesGds = inicialesGds;
		this.idGds = idGds;
		this.upc = upc;
	}
	
	/**
	 * Constructor para #Colab.getTodasEmpresasYAutonomosDelDespacho#
	 */
	public Empre(Long num, String nif, String nombre, Integer ultimoEstadoId, String ultimoEstadoDesc, BigInteger doc, String nomGds, String inicialesGds, Integer idGds, BigInteger upc, Integer numTrabajadores) {
		this.num = num;
		this.nombre = nombre;
		this.nif = nif;
		this.ultimoEstadoId = ultimoEstadoId;
		this.ultimoEstadoDesc = ultimoEstadoDesc;
		this.doc = doc;
		this.nomGds = nomGds;
		this.inicialesGds = inicialesGds;
		this.idGds = idGds;
		this.upc = upc;
		this.numTrabajadores = numTrabajadores;
	}
	
	/**
	 * Constructor para #Colab.XXX#
	 */
	public Empre(Long num, String nif, String nombre, Integer ultimoEstadoId, String ultimoEstadoDesc, BigInteger doc, String nomGds, String inicialesGds, Integer idGds, BigInteger upc, Integer numTrabajadores, String centroDesc) {
		this.num = num;
		this.nombre = nombre;
		this.nif = nif;
		this.ultimoEstadoId = ultimoEstadoId;
		this.ultimoEstadoDesc = ultimoEstadoDesc;
		this.doc = doc;
		this.nomGds = nomGds;
		this.inicialesGds = inicialesGds;
		this.idGds = idGds;
		this.upc = upc;
		this.numTrabajadores = numTrabajadores;
		
		RhDatosPersonales dp = new RhDatosPersonales();
		dp.setNombreCompleto(nomGds);
		dp.setIniciales(inicialesGds);
		dp.setClavePro(Long.valueOf(idGds));
		dp.setCentroDesc(centroDesc);
		
		this.upcRrhh = dp;
	}
	
	/**
	 * Constructor para #Colab.getEmpresasDelDespacho#
	 */
	public Empre(Long num, String nif, String nombre, Integer ultimoEstadoId, String ultimoEstadoDesc, BigInteger doc, String nomGds, String inicialesGds, Integer idGds, BigInteger upc, Long despachoId, String nombreDespacho, Integer idAsesor, String nombreAsesor, String inicialesAsesor, Integer numTrabajadores) {
		this.num = num;
		this.nombre = nombre;
		this.nif = nif;
		this.ultimoEstadoId = ultimoEstadoId;
		this.ultimoEstadoDesc = ultimoEstadoDesc;
		this.doc = doc;
		this.nomGds = nomGds;
		this.inicialesGds = inicialesGds;
		this.idGds = idGds;
		this.upc = upc;
		this.numTrabajadores = numTrabajadores;
		
		Colab despacho = new Colab();
		despacho.setClaco(despachoId);
		despacho.setNomco(nombreDespacho);
		despacho.setIdAsesor(idAsesor);
		despacho.setNomAsesor(nombreAsesor);
		despacho.setInicialesAsesor(inicialesAsesor);
		this.setAselab(despacho);
		
//		RhDatosPersonales upc = new RhDatosPersonales();
//		upc.setClavePro(Long.valueOf(idGds)); //Claco[Colab] <=> ClavePro[RhDatosPersonales]
//		upc.setNombre(nomGds);
//		upc.setIniciales(inicialesGds);
//		this.setUpcRrhh(upc);
	}
	
	public Empre(Long num, String nombre, String nif, String direccion) {
		this.num = num;
		this.nombre = nombre;
		this.nif = nif;
		this.direccion = direccion;
	}
	
	public Empre(Long num, String nombre, String nif, String direccion, BigInteger doc, String ultimoEstadoDesc, String nombreDt, 
			String nomGds, String inicialesGds, Integer idGds, Date fechaUltimoEstado, Long cifValido, Long cifDesconocido,Integer estadoId) {
		this.num = num;
		this.nombre = nombre;
		this.nif = nif;
		this.direccion = direccion;
		this.doc = doc; 
		this.ultimoEstadoDesc = ultimoEstadoDesc;
		this.nombreDt = nombreDt;
		this.nomGds = nomGds;
		this.inicialesGds = inicialesGds;
		this.idGds = idGds;
		this.fechaUltimoEstado = fechaUltimoEstado;
		this.cifValido = cifValido;
		this.cifDesconocido = cifDesconocido;
		if(estadoId != null){
			this.ultimoEstadoId = estadoId;
			this.ultimoEstadoComercialId = estadoId;
			this.ultimoEstadoComercial = EmpreEstado.fromInt(estadoId.intValue());
		}
	}
	
	public Empre(Long num, String nombre, String nif, String direccion, BigInteger doc, String ultimoEstadoDesc, String nombreDt, 
			String nomGds, String inicialesGds, Integer idGds, Date fechaUltimoEstado, Long cifValido, Long cifDesconocido,Integer estadoId,
			String nombreAutonomo) {
		this.num = num;
		this.nombre = nombre;
		this.nif = nif;
		this.direccion = direccion;
		this.doc = doc; 
		this.ultimoEstadoDesc = ultimoEstadoDesc;
		this.nombreDt = nombreDt;
		this.nomGds = nomGds;
		this.inicialesGds = inicialesGds;
		this.idGds = idGds;
		this.fechaUltimoEstado = fechaUltimoEstado;
		this.cifValido = cifValido;
		this.cifDesconocido = cifDesconocido;
		this.nombreAutonomo = nombreAutonomo;
		if(estadoId != null){
			this.ultimoEstadoId = estadoId;
			this.ultimoEstadoComercialId = estadoId;
			this.ultimoEstadoComercial = EmpreEstado.fromInt(estadoId.intValue());
		}
	}
	
	public Empre(Long num, String nombre, String nif, String direccion, Colab despacho) {
		this.num = num;
		this.nombre = nombre;
		this.nif = nif;
		this.direccion = direccion;
		if(despacho != null){
			this.aselab = new Colab(despacho.getClaco(), despacho.getNif(),despacho.getNomco(), despacho.getDomco());
		}
	}
	
	/** 
	 * Constructor para el listado de Mis_Empresas
	 */
	public Empre(Long num, String cif, String nombre, String nombreDt, Integer numTrabajadores, 
				Integer vto, String nomMutua, RhDatosPersonales upcRrhh, Date ultAct, Date proxAct, String estado) {
		this.nif = cif;
		this.num = num;
		this.nombre = nombre;
		this.nombreDt = nombreDt;
		this.numTrabajadores = numTrabajadores;
		this.vto = vto;
		this.nombreMutua = nomMutua;
		this.upcRrhh = upcRrhh;
		this.ultimaAct = ultAct;
		this.proximaAct = proxAct;
		this.ultimoEstadoDesc = estado;
	}
		
	public Empre(Long num, String cif, String nombre, String nombreDt, Integer numTrabajadores, 
			Integer vto, String nomMutua, RhDatosPersonales upcRrhh, Date ultAct, Date proxAct, String estado,Date fechaEstado,
			String nombrePoblacion,String nombreProvincia) {
		this.nif = cif;
		this.num = num;
		this.nombre = nombre;
		this.nombreDt = nombreDt;
		this.numTrabajadores = numTrabajadores;
		this.vto = vto;
		this.nombreMutua = nomMutua;
		this.upcRrhh = upcRrhh;
		this.ultimaAct = ultAct;
		this.proximaAct = proxAct;
		this.ultimoEstadoDesc = estado;
		this.fechaUltimoEstado = fechaEstado;
		this.nombrePobla = nombrePoblacion;
		Pobla pobla = new Pobla();
		Provi provi = new Provi();
		provi.setTexto(nombreProvincia);
		pobla.setTrapo(nombrePoblacion);
		pobla.setProvi1(provi);
		this.poblacion = pobla;	
		this.nombreProvi = nombreProvincia;
	}

	public Empre(Long num, String cif, String nombre, String nombreDt, Integer numTrabajadores, 
			Integer vto, String nomMutua, RhDatosPersonales upcRrhh, Date ultAct, Date proxAct, String estado,Date fechaEstado,
			String nombrePoblacion,String nombreProvincia, String nivel) {
		this.nif = cif;
		this.num = num;
		this.nombre = nombre;
		this.nombreDt = nombreDt;
		this.numTrabajadores = numTrabajadores;
		this.vto = vto;
		this.nombreMutua = nomMutua;
		this.upcRrhh = upcRrhh;
		this.ultimaAct = ultAct;
		this.proximaAct = proxAct;
		this.ultimoEstadoDesc = estado;
		this.fechaUltimoEstado = fechaEstado;
		this.nombrePobla = nombrePoblacion;
		Pobla pobla = new Pobla();
		Provi provi = new Provi();
		provi.setTexto(nombreProvincia);
		pobla.setTrapo(nombrePoblacion);
		pobla.setProvi1(provi);
		this.poblacion = pobla;	
		this.nombreProvi = nombreProvincia;
		this.nivel = nivel;
	}

	public Empre(Long num, String cif, String nombre, String nombreDt, Integer numTrabajadores, 
		Integer vto, String nomMutua, RhDatosPersonales upcRrhh, Date ultAct, Date proxAct, String estado,Date fechaEstado,
		String nombrePoblacion,String nombreProvincia, Date fAltaUltimoDocu, Date fBajaUltimoDocu) {
		this.nif = cif;
		this.num = num;
		this.nombre = nombre;
		this.nombreDt = nombreDt;
		this.numTrabajadores = numTrabajadores;
		this.vto = vto;
		this.nombreMutua = nomMutua;
		this.upcRrhh = upcRrhh;
		this.ultimaAct = ultAct;
		this.proximaAct = proxAct;
		this.ultimoEstadoDesc = estado;
		this.fechaUltimoEstado = fechaEstado;
		this.nombrePobla = nombrePoblacion;
		Pobla pobla = new Pobla();
		Provi provi = new Provi();
		provi.setTexto(nombreProvincia);
		pobla.setTrapo(nombrePoblacion);
		pobla.setProvi1(provi);
		this.poblacion = pobla;	
		this.nombreProvi = nombreProvincia;
		this.fAltaUltimoDocu = fAltaUltimoDocu;
		this.fBajaUltimoDocu = fBajaUltimoDocu;
	}
	
	/**
	 * Constructor para la query Empre.ReasignacionMisEmpresas
	 */
	public Empre(Long num, BigInteger doc, String cif, String nombre, String nombreDt, Integer numTrabajadores, 
			Integer vto, String nomMutua, RhDatosPersonales upcRrhh, Date ultAct, Date proxAct, String estado,Date fechaEstado,
			String nombrePoblacion,String nombreProvincia, Date fAltaUltimoDocu, Date fBajaUltimoDocu) {
		this.nif = cif;
		this.num = num;
		this.nombre = nombre;
		this.nombreDt = nombreDt;
		this.numTrabajadores = numTrabajadores;
		this.vto = vto;
		this.nombreMutua = nomMutua;
		this.upcRrhh = upcRrhh;
		this.ultimaAct = ultAct;
		this.proximaAct = proxAct;
		this.ultimoEstadoDesc = estado;
		this.fechaUltimoEstado = fechaEstado;
		this.nombrePobla = nombrePoblacion;
		Pobla pobla = new Pobla();
		Provi provi = new Provi();
		provi.setTexto(nombreProvincia);
		pobla.setTrapo(nombrePoblacion);
		pobla.setProvi1(provi);
		this.poblacion = pobla;	
		this.nombreProvi = nombreProvincia;
		this.fAltaUltimoDocu = fAltaUltimoDocu;
		this.fBajaUltimoDocu = fBajaUltimoDocu;
		this.doc = doc;
	}
	
	/**
	 * Constructor para el listado de Autónomos del Despacho (Colab.getAutonomosDelDespachoSeleccionado)
	 */
	public Empre(Long num, String cif, String nombre, String nombreAutonomo, String nombreDt, Integer numTrabajadores, 
			Integer vto, String nomMutua, RhDatosPersonales upcRrhh, Date ultAct, Date proxAct, String estado,Date fechaEstado,
			String nombrePoblacion,String nombreProvincia,Long despachoId,String nombreDespacho, String autonomoConAt) {
		this.nif = cif;
		this.num = num;
		this.nombre = nombre;
		this.nombreAutonomo = nombreAutonomo;
		this.nombreDt = nombreDt;
		this.numTrabajadores = numTrabajadores;
		this.vto = vto;
		this.nombreMutua = nomMutua;
		this.upcRrhh = upcRrhh;
		this.ultimaAct = ultAct;
		this.proximaAct = proxAct;
		this.ultimoEstadoDesc = estado;
		this.fechaUltimoEstado = fechaEstado;
		this.nombrePobla = nombrePoblacion;
		Pobla pobla = new Pobla();
		Provi provi = new Provi();
		provi.setTexto(nombreProvincia);
		pobla.setTrapo(nombrePoblacion);
		pobla.setProvi1(provi);
		this.setPoblacion(pobla);
		Colab despacho = new Colab();
		despacho.setClaco(despachoId);
		despacho.setNomco(nombreDespacho);
		this.setAselab(despacho);
		this.autonomoConAT = autonomoConAt;
		this.nombreProvi = nombreProvincia;
	}
	
	/**
	 * Constructor para el listado de Empresas de un Grupo (Grupoe.getEmpresas)
	 */
	public Empre(Long num, String cif, String nombre, String nombreDt, Integer numTrabajadores, 
			Integer vto, String nomMutua, RhDatosPersonales upcRrhh, Date ultAct, Date proxAct, String estado,Date fechaEstado,
			String nombrePoblacion,String nombreProvincia,Long despachoId,String nombreDespacho, String autonomoConAt) {
		this.nif = cif;
		this.num = num;
		this.nombre = nombre;
		this.nombreDt = nombreDt;
		this.numTrabajadores = numTrabajadores;
		this.vto = vto;
		this.nombreMutua = nomMutua;
		this.upcRrhh = upcRrhh;
		this.ultimaAct = ultAct;
		this.proximaAct = proxAct;
		this.ultimoEstadoDesc = estado;
		this.fechaUltimoEstado = fechaEstado;
		this.nombrePobla = nombrePoblacion;
		Pobla pobla = new Pobla();
		Provi provi = new Provi();
		provi.setTexto(nombreProvincia);
		pobla.setTrapo(nombrePoblacion);
		pobla.setProvi1(provi);
		this.setPoblacion(pobla);
		Colab despacho = new Colab();
		despacho.setClaco(despachoId);
		despacho.setNomco(nombreDespacho);
		this.setAselab(despacho);
		this.autonomoConAT = autonomoConAt;
		this.nombreProvi = nombreProvincia;
	}
	
	// Poblacion, DT, UPC, Despacho
	public Empre(Long num, String cif, String nombre, String nombreDt, Integer numTrabajadores, 
			Integer vto, String nomMutua, RhDatosPersonales upcRrhh, Date ultAct, Date proxAct, String estado,Date fechaEstado,
			String nombrePoblacion,String nombreProvincia,Long despachoId,String nombreDespacho, String autonomoConAt, String nombreAutonomo) {
		this.nif = cif;
		this.num = num;
		this.nombre = nombre;
		this.nombreDt = nombreDt;
		this.numTrabajadores = numTrabajadores;
		this.vto = vto;
		this.nombreMutua = nomMutua;
		this.upcRrhh = upcRrhh;
		this.ultimaAct = ultAct;
		this.proximaAct = proxAct;
		this.ultimoEstadoDesc = estado;
		this.fechaUltimoEstado = fechaEstado;
		this.nombrePobla = nombrePoblacion;
		Pobla pobla = new Pobla();
		Provi provi = new Provi();
		provi.setTexto(nombreProvincia);
		pobla.setTrapo(nombrePoblacion);
		pobla.setProvi1(provi);
		this.setPoblacion(pobla);
		Colab despacho = new Colab();
		despacho.setClaco(despachoId);
		despacho.setNomco(nombreDespacho);
		this.setAselab(despacho);
		this.autonomoConAT = autonomoConAt;
		this.nombreProvi = nombreProvincia;
		this.nombreAutonomo = nombreAutonomo;
	}
	
	// Mis autónomos
	public Empre(Long num, String cif, String nombre, String nombreAutonomo, String nombreDt, String upcIniciales, 
			BigInteger upcNusuario, String upcNombreCompleto, String nombrePoblacion, String nombreProvincia, 
			Long despachoId, String nombreDespacho, String autonomoConAt) {
		
		this.nif = cif;
		this.num = num;
		this.nombre = nombre;
		this.nombreDt = nombreDt;
		RhDatosPersonales upcRrhh = new RhDatosPersonales();
		upcRrhh.setIniciales(upcIniciales);
		upcRrhh.setNusuario(upcNusuario);
		upcRrhh.setNombreCompleto(upcNombreCompleto);
		this.upcRrhh = upcRrhh;
		this.nombrePobla = nombrePoblacion;
		Pobla pobla = new Pobla();
		Provi provi = new Provi();
		provi.setTexto(nombreProvincia);
		pobla.setTrapo(nombrePoblacion);
		pobla.setProvi1(provi);
		this.setPoblacion(pobla);
		Colab despacho = new Colab();
		despacho.setClaco(despachoId);
		despacho.setNomco(nombreDespacho);
		this.setAselab(despacho);
		this.autonomoConAT = autonomoConAt;
		this.nombreProvi = nombreProvincia;
		this.nombreAutonomo = nombreAutonomo;
	}
	
	/** 
	 * Constructor para el listado de Grupos
	 */
	public Empre(Long num, String cif, String nombre, Integer numTrabajadores, 
			String gds, String ultimoEstadoDesc, Date fechaUltimoEstado) {
		this.nif = cif;
		this.num = num;
		this.nombre = nombre;
		this.numTrabajadores = numTrabajadores;
		this.nomGds = gds;
		this.ultimoEstadoDesc = ultimoEstadoDesc;
		this.fechaUltimoEstado = fechaUltimoEstado;
	}	

	public BigInteger getDoc() {
		return doc;
	}

	public void setDoc(BigInteger doc) {
		this.doc = doc;
	}

	public String getNif() {
		return (nif != null ? nif.trim() : null);
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getNombre() {
		return (nombre != null ? nombre.trim() : null);
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombrec() {
		return (nombrec != null ? nombrec.trim() : null);
	}

	public void setNombrec(String nombrec) {
		this.nombrec = nombrec;
	}

	public String getDomic() {
		return (domic != null ? domic.trim() : null);
	}

	public void setDomic(String domic) {
		this.domic = domic;
	}

	public BigInteger getProvi() {
		return provi;
	}

	public void setProvi(BigInteger provi) {
		this.provi = provi;
	}

	public BigInteger getPobla() {
		return pobla;
	}

	public void setPobla(BigInteger pobla) {
		this.pobla = pobla;
	}

	public BigInteger getDispo() {
		return dispo;
	}

	public void setDispo(BigInteger dispo) {
		this.dispo = dispo;
	}

	public String getTelef1() {
		return (telef1 != null ? telef1.trim() : null);
	}

	public void setTelef1(String telef1) {
		this.telef1 = telef1;
	}

	public String getTelef2() {
		return (telef2 != null ? telef2.trim() : null);
	}

	public void setTelef2(String telef2) {
		this.telef2 = telef2;
	}

	public String getFax() {
		return (fax != null ? fax.trim() : null);
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public BigInteger getUpc() {
		return upc;
	}

	public void setUpc(BigInteger upc) {
		this.upc = upc;
	}

	public BigInteger getCentra() {
		return centra;
	}

	public void setCentra(BigInteger centra) {
		this.centra = centra;
	}

	public String getEmail() {
		return email.trim();
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContacto() {
		return (contacto != null ? contacto.trim() : null);
	}

	public void setContacto(String contacto) {
		this.contacto = contacto;
	}

	public BigInteger getUsualt() {
		return usualt;
	}

	public void setUsualt(BigInteger usualt) {
		this.usualt = usualt;
	}

	public BigInteger getUsumod() {
		return usumod;
	}

	public void setUsumod(BigInteger usumod) {
		this.usumod = usumod;
	}

	public Long getNum() {
		return num;
	}

	public void setNum(Long num) {
		this.num = num;
	}

	public String getNifOri() {
		return (nifOri != null ? nifOri.trim() : null);
	}

	public void setNifOri(String nifOri) {
		this.nifOri = nifOri;
	}

	public String getNifDes() {
		return (nifDes != null ? nifDes.trim() : null);
	}

	public void setNifDes(String nifDes) {
		this.nifDes = nifDes;
	}

//START-ENTITY-MOD
	public Colab getFirma() {
		return firma;
	}

	public void setFirma(Colab firma) {
		this.firma = firma;
	}
//END-ENTITY-MOD
	public Long getVip() {
		return vip;
	}

	public void setVip(Long vip) {
		this.vip = vip;
	}

	public Date getFEntrega() {
		return fEntrega;
	}

	public void setFEntrega(Date fEntrega) {
		this.fEntrega = fEntrega;
	}

	public Date getFClaveW() {
		return fClaveW;
	}

	public void setFClaveW(Date fClaveW) {
		this.fClaveW = fClaveW;
	}

	public Date getFPlanAcogida() {
		return fPlanAcogida;
	}

	public void setFPlanAcogida(Date fPlanAcogida) {
		this.fPlanAcogida = fPlanAcogida;
	}

	public BigInteger getMutOrigen() {
		return mutOrigen;
	}

	public void setMutOrigen(BigInteger mutOrigen) {
		this.mutOrigen = mutOrigen;
	}

	public BigInteger getTipNif() {
		return tipNif;
	}

	public void setTipNif(BigInteger tipNif) {
		this.tipNif = tipNif;
	}

	public BigInteger getNumVinculado() {
		return numVinculado;
	}

	public void setNumVinculado(BigInteger numVinculado) {
		this.numVinculado = numVinculado;
	}

//	public BigInteger getCaptarMuces() {
//		return captarMuces;
//	}
//
//	public void setCaptarMuces(BigInteger captarMuces) {
//		this.captarMuces = captarMuces;
//	}

	public BigInteger getCaptarNutra() {
		return captarNutra;
	}

	public void setCaptarNutra(BigInteger captarNutra) {
		this.captarNutra = captarNutra;
	}

	public BigInteger getCaptarNcentros() {
		return captarNcentros;
	}

	public void setCaptarNcentros(BigInteger captarNcentros) {
		this.captarNcentros = captarNcentros;
	}

	public Set<Centra> getCentraCollection() {
		return centraCollection;
	}

	public void setCentraCollection(Set<Centra> centraCollection) {
		this.centraCollection = centraCollection;
	}

	public Set<Docu> getDocuCollection() {
		return docuCollection;
	}

	public void setDocuCollection(Set<Docu> docuCollection) {
		this.docuCollection = docuCollection;
	}

	public Centra getEmpCenDom() {
		return empCenDom;
	}

	public void setEmpCenDom(Centra empCenDom) {
		this.empCenDom = empCenDom;
	}

	public Centra getEmpCenEnv() {
		return empCenEnv;
	}

	public void setEmpCenEnv(Centra empCenEnv) {
		this.empCenEnv = empCenEnv;
	}

	public Colab getAselab() {
		return aselab;
	}

	public void setAselab(Colab aselab) {
		this.aselab = aselab;
	}

//	public Set<Empre> getEmpreCollection() {
//		return empreCollection;
//	}
//
//	public void setEmpreCollection(Set<Empre> empreCollection) {
//		this.empreCollection = empreCollection;
//	}
//
//	public Empre getEmpReferencia() {
//		return empReferencia;
//	}
//
//	public void setEmpReferencia(Empre empReferencia) {
//		this.empReferencia = empReferencia;
//	}

	public Grupoe getGrupoe() {
		return grupoe;
	}

	public void setGrupoe(Grupoe grupoe) {
		this.grupoe = grupoe;
	}

//START-ENTITY-MOD
	/**
	 * @return the fFirma
	 */
	public Date getFFirma() {
		return fFirma;
	}

	/**
	 * @param firma the fFirma to set
	 */
	public void setFFirma(Date firma) {
		fFirma = firma;
	}

	/**
	 * @return the fEfectoFirma
	 */
	public Date getFEfectoFirma() {
		return fEfectoFirma;
	}

	/**
	 * @param efectoFirma the fEfectoFirma to set
	 */
	public void setFEfectoFirma(Date efectoFirma) {
		fEfectoFirma = efectoFirma;
	}

	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public String getCodigoPostal() {
		return codigoPostal;
	}

	public Pobla getPoblacion() {
		return poblacion;
	}

	public void setPoblacion(Pobla poblacion) {
		this.poblacion = poblacion;
//		this.nombrePobla = poblacion.getTrapo();
	}

	public Integer getNumTrabajadores() {
		return numTrabajadores;
	}

	/**
	 * @return the direccion
	 */
	public String getDireccion() {
		return (direccion != null ? direccion.trim() : null);
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	/**
	 * @return the colabUPC
	 */
	public Colab getColabUPC() {
		return colabUPC;
	}

	/**
	 * @param colabUPC the colabUPC to set
	 */
	public void setColabUPC(Colab colabUPC) {
		this.colabUPC = colabUPC;
	}

	/**
	 * @return the fechaUltimoEstado
	 */
	public Date getFechaUltimoEstado() {
		return fechaUltimoEstado;
	}

	/**
	 * @param fechaUltimoEstado the fechaUltimoEstado to set
	 */
	public void setFechaUltimoEstado(Date fechaUltimoEstado) {
		this.fechaUltimoEstado = fechaUltimoEstado;
	}

	/**
	 * @return the ultimoEstadoId
	 */
	public Integer getUltimoEstadoId() {
		return ultimoEstadoId;
	}

	/**
	 * @param ultimoEstadoId the ultimoEstadoId to set
	 */
	public void setUltimoEstadoId(Integer ultimoEstadoId) {
		this.ultimoEstadoId = ultimoEstadoId;
	}

	/**
	 * @return the ultimoEstadoDesc
	 */
	public String getUltimoEstadoDesc() {
		return (ultimoEstadoDesc != null ? ultimoEstadoDesc.trim() : null);
	}

	/**
	 * @param ultimoEstadoDesc the ultimoEstadoDesc to set
	 */
	public void setUltimoEstadoDesc(String ultimoEstadoDesc) {
		this.ultimoEstadoDesc = ultimoEstadoDesc;
	}

	/**
	 * @return the mutuaCesante
	 */
	public Mutua getMutuaCesante() {
		return mutuaCesante;
	}

	/**
	 * @param mutuaCesante the mutuaCesante to set
	 */
	public void setMutuaCesante(Mutua mutuaCesante) {
		this.mutuaCesante = mutuaCesante;
	}

	/**
	 * @return the web
	 */
	public String getWeb() {
		return (web != null ? web.trim() : null);
	}

	/**
	 * @param web the web to set
	 */
	public void setWeb(String web) {
		this.web = web;
	}

	/**
	 * @return the cnae
	 */
	public Cnae3 getCnae() {
		return cnae;
	}

	/**
	 * @param cnae the cnae to set
	 */
	public void setCnae(Cnae3 cnae) {
		this.cnae = cnae;
	}

	public Set<AfiObservacion> getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(Set<AfiObservacion> observaciones) {
		this.observaciones = observaciones;
	}
	
	public String getNombreDt() {
		return nombreDt;
	}

	public void setNombreDt(String dt) {
		this.nombreDt = dt;
	}
	
	/**
	 * @return the ultimoEstadoComercial
	 */
	public Integer getUltimoEstadoComercialId() {
		return ultimoEstadoComercialId;
	}

	/**
	 * @param ultimoEstadoComercial the ultimoEstadoComercial to set
	 */
	public void setUltimoEstadoComercialId(Integer ultimoEstadoComercialId) {
		this.ultimoEstadoComercialId = ultimoEstadoComercialId;
	}
	
	/**
	 * 
	 * @return the nombreProvi
	 */
	public String getNombreProvi() {
		return nombreProvi;
	}

	/**
	 * 
	 * @param nombreProvi  the nombreProvi to set
	 */
	public void setNombreProvi(String nombreProvi) {
		this.nombreProvi = nombreProvi;
	}

	
//END-ENTITY-MOD

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (num != null ? num.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Empre)) {
			return false;
		}
		Empre other = (Empre) object;
		if (this.num != other.num
				&& (this.num == null || !this.num.equals(other.num))) {
			return false;
		}
		return true;
	}

	public Collection<Prcontra> getPrcontraCollection() {
		return prcontraCollection;
	}

	public void setPrcontraCollection(Collection<Prcontra> prcontraCollection) {
		this.prcontraCollection = prcontraCollection;
	}

	/**
	 *
	 * @return
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("Empre[");
		buffer.append(" nif = ").append(nif);
		buffer.append(" nombre = ").append(nombre);
		buffer.append(" nombrec = ").append(nombrec);
		buffer.append(" num = ").append(num);
		buffer.append("]");
		return buffer.toString();
	}

	public String getPuedoPlanificarme() {
		return puedoPlanificarme;
	}

	public void setPuedoPlanificarme(String puedoPlanificarme) {
		this.puedoPlanificarme = puedoPlanificarme;
	}

	public Usuarios getUsuarioFirma() {
		return usuarioFirma;
	}

	public void setUsuarioFirma(Usuarios usuarioFirma) {
		this.usuarioFirma = usuarioFirma;
	}

	public void setNumTrabajadores(Integer numTrabajadores) {
		//this.numTrabajadores = numTrabajadores;
	}

	/**
	 * @return the gridEmpreCollection
	 */
	public Set<GridEmpre> getGridEmpreCollection() {
		return gridEmpreCollection;
	}

	/**
	 * @param gridEmpreCollection the gridEmpreCollection to set
	 */
	public void setGridEmpreCollection(Set<GridEmpre> gridEmpreCollection) {
		this.gridEmpreCollection = gridEmpreCollection;
	}

	//START-ENTITY-MOD
//	@PostLoad
//	void populateTransientFields() {
//		ultimoEstadoComercial = EmpreEstado.fromInt((ultimoEstadoComercialId != null ? ultimoEstadoComercialId : -1));
//	}
	//END-ENTITY-MOD

	public String getCccConBajaSolicitada() {
		return cccConBajaSolicitada;
	}

	public void setCccConBajaSolicitada(String cccConBajaSolicitada) {
		this.cccConBajaSolicitada = cccConBajaSolicitada;
	}

	public String getNombreMutua() {
		return nombreMutua;
	}

	public int getVto() {
		return vto;
	}

	public String getNomGds() {
		return nomGds;
	}

	public Date getUltimaAct() {
		return ultimaAct;
	}

	public Date getProximaAct() {
		return proximaAct;
	}

	public void setNombreMutua(String nombreMutua) {
		this.nombreMutua = nombreMutua;
	}

	public void setVto(int vto) {
		this.vto = vto;
	}

	public void setNomGds(String nomGds) {
		this.nomGds = nomGds;
	}

	public void setUltimaAct(Date ultimaAct) {
		this.ultimaAct = ultimaAct;
	}

	public void setProximaAct(Date proximaAct) {
		this.proximaAct = proximaAct;
	}
	

	public RhDatosPersonales getUpcRrhh() {
		return upcRrhh;
	}

	public void setUpcRrhh(RhDatosPersonales upcRrhh) {
		this.upcRrhh = upcRrhh;
	}

	/**
	 * @return the fechaUltimaFirma
	 */
	public Date getFechaUltimaFirma() {
		return fechaUltimaFirma;
	}

	/**
	 * @param fechaUltimaFirma the fechaUltimaFirma to set
	 */
	public void setFechaUltimaFirma(Date fechaUltimaFirma) {
		this.fechaUltimaFirma = fechaUltimaFirma;
	}

	/**
	 * @return the nombreVinculado
	 */
	public String getNombreEmpreVinculada() {
		return nombreEmpreVinculada;
	}

	/**
	 * @param nombreVinculado the nombreVinculado to set
	 */
	public void setNombreEmpreVinculada(String nombreEmpreVinculada) {
		this.nombreEmpreVinculada = nombreEmpreVinculada;
	}

	/**
	 * @return the inicialesGds
	 */
	public String getInicialesGds() {
		return inicialesGds;
	}

	/**
	 * @return the idGds
	 */
	public Integer getIdGds() {
		return idGds;
	}

	/**
	 * @param inicialesGds the inicialesGds to set
	 */
	public void setInicialesGds(String inicialesGds) {
		this.inicialesGds = inicialesGds;
	}

	/**
	 * @param idGds the idGds to set
	 */
	public void setIdGds(Integer idGds) {
		this.idGds = idGds;
	}

	public String getNombrePobla() {
		return nombrePobla;
	}

	public void setNombrePobla(String nombrePobla) {
		this.nombrePobla = nombrePobla;
	}
	
	/**
	 * @return the tipoCuenta
	 */
	public String getTipoCuenta() {
		return tipoCuenta;
	}

	/**
	 * @param tipoCuenta the tipoCuenta to set
	 */
	public void setTipoCuenta(String tipoCuenta) {
		this.tipoCuenta = tipoCuenta;
	}

	public String getAutonomoConAT() {
		return autonomoConAT;
	}

	public void setAutonomoConAT(String autonomoConAT) {
		this.autonomoConAT = autonomoConAT;
	}

	/**
	 * @return the cifValido
	 */
	public Long getCifValido() {
		return cifValido;
	}

	/**
	 * @param cifValido the cifValido to set
	 */
	public void setCifValido(Long cifValido) {
		this.cifValido = cifValido;
	}

	/**
	 * @return the cifDesconocido
	 */
	public Long getCifDesconocido() {
		return cifDesconocido;
	}

	/**
	 * @param cifDesconocido the cifDesconocido to set
	 */
	public void setCifDesconocido(Long cifDesconocido) {
		this.cifDesconocido = cifDesconocido;
	}
	
	public Date getFUltmod() {
		return fUltmod;
	}

	public void setFUltmod(Date ultmod) {
		fUltmod = ultmod;
	}	

	public Date getFAltaUltimoDocu() {
		return fAltaUltimoDocu;
	}

	public Date getFBajaUltimoDocu() {
		return fBajaUltimoDocu;
	}

	public void setFAltaUltimoDocu(Date altaUltimoDocu) {
		fAltaUltimoDocu = altaUltimoDocu;
	}

	public void setFBajaUltimoDocu(Date bajaUltimoDocu) {
		fBajaUltimoDocu = bajaUltimoDocu;
	}

	public Date getCaptarVto() {
		return captarVto;
	}

	public void setCaptarVto(Date captarVto) {
		this.captarVto = captarVto;
	}

	/**
	 * @return the ultEstadoComercial
	 */
	public EmpreEstado getUltimoEstadoComercial() {
		return EmpreEstado.fromInt(ultimoEstadoComercialId);
	}

	/**
	 * @param ultEstadoComercial the ultEstadoComercial to	 set
	 */
	public void setUltimoEstadoComercial(EmpreEstado ultEstadoComercial) {
		this.ultimoEstadoComercialId = ultEstadoComercial.getId();
	}

	/**
	 * @return the spExternoET
	 */
	public PreSpexterno getSpExternoET() {
		return spExternoET;
	}

	/**
	 * @param spExternoET the spExternoET to set
	 */
	public void setSpExternoET(PreSpexterno spExternoET) {
		this.spExternoET = spExternoET;
	}
	
	/**
	 * @return the spExternoVdS
	 */
	public PreSpexterno getSpExternoVdS() {
		return spExternoVdS;
	}

	/**
	 * @param spExternoVdS the spExternoVdS to set
	 */
	public void setSpExternoVdS(PreSpexterno spExternoVdS) {
		this.spExternoVdS = spExternoVdS;
	}
	
	/**
	 * @return the spPropioET
	 */
	public Long getSpPropioET() {
		return spPropioET;
	}

	/**
	 * @param spPropioET the spPropioET to set
	 */
	public void setSpPropioET(Long spPropioET) {
		this.spPropioET = spPropioET;
	}

	/**
	 * @return the spPropioVdS
	 */
	public Long getSpPropioVdS() {
		return spPropioVdS;
	}

	/**
	 * @param spPropioVdS the spPropioVdS to set
	 */
	public void setSpPropioVdS(Long spPropioVdS) {
		this.spPropioVdS = spPropioVdS;
	}

	/**
	 * @return the verTopSPA
	 */
	public Long getVerTopSPA() {
		return verTopSPA;
	}

	/**
	 * @param verTopSPA the verTopSPA to set
	 */
	public void setVerTopSPA(Long verTopSPA) {
		this.verTopSPA = verTopSPA;
	}
	
	/**
	 * @return the tieneContratoETActivo
	 */
	public Long getTieneContratoETActivo() {
		return tieneContratoETActivo;
	}

	/**
	 * @param tieneContratoETActivo the tieneContratoETActivo to set
	 */
	public void setTieneContratoETActivo(Long tieneContratoETActivo) {
		this.tieneContratoETActivo = tieneContratoETActivo;
	}

	/**
	 * @return the tieneContratoVdSActivo
	 */
	public Long getTieneContratoVdSActivo() {
		return tieneContratoVdSActivo;
	}

	/**
	 * @param tieneContratoVdSActivo the tieneContratoVdSActivo to set
	 */
	public void setTieneContratoVdSActivo(Long tieneContratoVdSActivo) {
		this.tieneContratoVdSActivo = tieneContratoVdSActivo;
	}


	/**
	 * @return the gestorAbsentismo
	 */
	public Usuarios getGestorAbsentismo() {
		return gestorAbsentismo;
	}

	/**
	 * @param gestorAbsentismo the gestorAbsentismo to set
	 */
	public void setGestorAbsentismo(Usuarios gestorAbsentismo) {
		this.gestorAbsentismo = gestorAbsentismo;
	}

	/**
	 * @return the nombreAutonomo
	 */
	public String getNombreAutonomo() {
		return nombreAutonomo;
	}

	/**
	 * @param nombreAutonomo the nombreAutonomo to set
	 */
	public void setNombreAutonomo(String nombreAutonomo) {
		this.nombreAutonomo = nombreAutonomo;
	}

	public String getMiembroJD() {
		return miembroJD;
	}

	public void setMiembroJD(String miembroJD) {
		this.miembroJD = miembroJD;
	}

	/**
	 * @return the especialidadesET
	 */
	public Set<PreEspecialidades> getEspecialidadesTecnicas() {
		return especialidadesTecnicas;
	}

	/**
	 * @param especialidadesET the especialidadesET to set
	 */
	public void setEspecialidadesTecnicas(Set<PreEspecialidades> especialidadesTecnicas) {
		this.especialidadesTecnicas = especialidadesTecnicas;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	/**
	 * @return the inicialesGdSCCC
	 */
	public String getInicialesGdSCCC() {
		return inicialesGdSCCC;
	}

	/**
	 * @param inicialesGdSCCC the inicialesGdSCCC to set
	 */
	public void setInicialesGdSCCC(String inicialesGdSCCC) {
		this.inicialesGdSCCC = inicialesGdSCCC;
	}

	/**
	 * @return the nomGdSCCC
	 */
	public String getNomGdSCCC() {
		return nomGdSCCC;
	}

	/**
	 * @param nomGdSCCC the nomGdSCCC to set
	 */
	public void setNomGdSCCC(String nomGdSCCC) {
		this.nomGdSCCC = nomGdSCCC;
	}

	/**
	 * @return the idGdSCCC
	 */
	public Long getIdGdSCCC() {
		return idGdSCCC;
	}

	/**
	 * @param idGdSCCC the idGdSCCC to set
	 */
	public void setIdGdSCCC(Long idGdSCCC) {
		this.idGdSCCC = idGdSCCC;
	}

	public String getEmpVerTopSpaMotivo() {
		return empVerTopSpaMotivo;
	}

	public void setEmpVerTopSpaMotivo(String empVerTopSpaMotivo) {
		this.empVerTopSpaMotivo = empVerTopSpaMotivo;
	}

	public Integer getEstadoId() {
		return estadoId;
	}

	public void setEstadoId(Integer estadoId) {
		this.estadoId = estadoId;
	}
	
	
	
}
