package rh11.hplay.mapped;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "node")
public class Node {
    @GenericGenerator(name = "nodeSeq", strategy = "seqhilo", parameters = {
            @Parameter(name = "sequence", value = "node_seq") })
    @Id
    @GeneratedValue(generator = "nodeSeq")
    @Column(name = "id", unique = false, nullable = false, insertable = true, updatable = false)
    int id;

    @Column(name = "name")
    String name;

    @OneToMany(mappedBy="target")
    Set<Edge> incomingEdges;

    @OneToMany(mappedBy="source")
    Set<Edge> outgoingEdges;
}
