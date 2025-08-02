import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

import org.hibernate.ejb.criteria.CriteriaBuilderImpl;
import org.hibernate.ejb.criteria.CriteriaQueryCompiler;
import org.hibernate.ejb.criteria.CriteriaQueryCompiler.RenderingContext;
import org.hibernate.ejb.criteria.ParameterRegistry;
import org.hibernate.ejb.criteria.Renderable;
import org.hibernate.ejb.criteria.predicate.AbstractSimplePredicate;
import org.springframework.util.StringUtils;

public class CriteriaTypePredicate<X>
	extends AbstractSimplePredicate
	implements Serializable {

	private final Expression<X> expression;
	
	private final String clazz;
	
	private final boolean checkEquals;

	public CriteriaTypePredicate(
			CriteriaBuilder criteriaBuilder,
			Expression<X> expression,
			Class<? extends X> clazz) {
		this( criteriaBuilder, expression, true,clazz);
	}
	public CriteriaTypePredicate(
			CriteriaBuilder criteriaBuilder,
			Expression<X> expression,
			boolean checkEquals,
			Class<? extends X> clazz) {
		super( (CriteriaBuilderImpl) criteriaBuilder );
		this.expression = expression;
		this.checkEquals = checkEquals;
		
		Entity entity = clazz.getAnnotation(Entity.class);
	    boolean hasName = null != entity && StringUtils.hasText(entity.name());
	    this.clazz =  hasName ? entity.name() : clazz.getSimpleName();
	}

	@Override
	public void registerParameters(ParameterRegistry registry) {
	}

	@Override
	public String render(CriteriaQueryCompiler.RenderingContext renderingContext) {
		return new StringBuilder().append( "TYPE" ).append( '(' )
			.append(( (Renderable) expression ).render( renderingContext ))
			.append( ')' )
			.append(checkEquals?"=":"<>")
			.append( clazz )
			.toString();
	}
	@Override
	public String renderProjection(RenderingContext arg0) {
		return render(arg0);
	}

}

