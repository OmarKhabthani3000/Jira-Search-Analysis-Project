
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cible")
@Inheritance(strategy = InheritanceType.JOINED)
public class Cible {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn
    private CibleRevision derniereRevision;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TypeCible typeCible;
}


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cible_rev")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CibleRevision {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Cible cible;
}


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cooperative_rev")
public class CooperativeRevision extends CibleRevision {

    @OneToOne
    @JoinColumn
    private Fichier imageEquipe;
}


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "don_rev")
public class DonsRevision extends CibleRevision {
}


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "entreprise_rev")
public class EntrepriseRevision extends CibleRevision {

    @OneToOne
    @JoinColumn
    private Fichier imageEquipe;
}


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "club_deal_rev")
public class ClubDealRevision extends CibleRevision {

    @OneToOne
    @JoinColumn
    private Fichier imageEquipe;
}


@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "fichier_gen")
@Inheritance(strategy = InheritanceType.JOINED)
public class FichierGenerique {

    @Id
    @GeneratedValue
    protected Long id;
}

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "fichier")
@DynamicInsert
public class Fichier extends FichierGenerique {

    @Column(unique = true)
    private String url;
}
