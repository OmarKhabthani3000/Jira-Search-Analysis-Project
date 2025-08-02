package br.com.gesplan.persistence.hibernate;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitForeignKeyNameSource;
import org.hibernate.boot.model.naming.ImplicitIndexNameSource;
import org.hibernate.boot.model.naming.ImplicitJoinColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitMapKeyColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.model.naming.ImplicitUniqueKeyNameSource;
import org.hibernate.boot.model.naming.NamingHelper;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.spi.MetadataBuildingContext;

public class MyClassImplicitNamingStrategy extends ImplicitNamingStrategyJpaCompliantImpl {
	
	public static final ImplicitNamingStrategy INSTANCE = new MyClassImplicitNamingStrategy();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public Identifier determineMapKeyColumnName(ImplicitMapKeyColumnNameSource source) {
		return toIdentifier(transformAttributePath(source.getPluralAttributePath()), source.getBuildingContext());
	}
	
	@Override
	public Identifier determineForeignKeyName(ImplicitForeignKeyNameSource source) {
		return toIdentifier(NamingHelper.INSTANCE.generateHashedFkName("FK_", source.getTableName(), source.getReferencedTableName(), source.getColumnNames()), source.getBuildingContext());
	}
	
	@Override
	public Identifier determineUniqueKeyName(ImplicitUniqueKeyNameSource source) {
		return toIdentifier(NamingHelper.INSTANCE.generateHashedConstraintName("UK_", source.getTableName(), source.getColumnNames()), source.getBuildingContext());
	}
	
	@Override
	public Identifier determineIndexName(ImplicitIndexNameSource source) {
		return toIdentifier(NamingHelper.INSTANCE.generateHashedConstraintName("IDX_", source.getTableName(), source.getColumnNames()), source.getBuildingContext());
	}
	
	@Override
	public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {
		String name;
		
		if (source.getNature() == ImplicitJoinColumnNameSource.Nature.ELEMENT_COLLECTION || source.getAttributePath() == null) {
			name = transformEntityName(source.getEntityNaming()) + '_' + source.getReferencedColumnName().getText();
		} else {
			name = transformAttributePath(source.getAttributePath()) + '_' + source.getReferencedColumnName().getText();
		}
		
		name = MyClassPhysicalNamingStrategy.join(MyClassPhysicalNamingStrategy.splitAndReplace(name));
		return toIdentifier(name, source.getBuildingContext());
	}
	
	@Override
	protected String transformAttributePath(AttributePath attributePath) {
		final StringBuilder sb = new StringBuilder();
		process(attributePath, sb);
		return sb.toString();
	}
	
	public static void process(AttributePath attributePath, StringBuilder sb) {
		if (attributePath.getParent() != null) {
			// CASO FOR COMPOSITEKEY ele não coloca ignora o id. do começo da chave por conta da propriedade GesplanCompositeEntity @EmbeddedId private T id;
			if (!attributePath.getParent().getFullPath().equals("id")) {
				process(attributePath.getParent(), sb);
				
				if (!"".equals(attributePath.getParent().getProperty())) {
					sb.append("_");
				}
			}
		}
		
		String property = attributePath.getProperty();
		property = property.replace("<", "");
		property = property.replace(">", "");
		
		sb.append(MyClassPhysicalNamingStrategy.join(MyClassPhysicalNamingStrategy.splitAndReplace(property)));
	}
	
	@Override
	protected Identifier toIdentifier(String stringForm, MetadataBuildingContext buildingContext) {
		return buildingContext.getMetadataCollector().getDatabase().getJdbcEnvironment().getIdentifierHelper().toIdentifier(stringForm);
	}
}
