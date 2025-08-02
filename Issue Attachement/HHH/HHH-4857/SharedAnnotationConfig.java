package org.hibernate.cfg;

import org.hibernate.MappingException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.SettingsFactory;

public class SharedAnnotationConfig extends AnnotationConfiguration{
	private boolean initialized = false;
	private boolean completedSecondPass = false;

	public SharedAnnotationConfig(SettingsFactory sf) {
		super(sf);
	}

	/**
	 * @return the initialized
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * @param initialized the initialized to set
	 */
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	public void buildMappings(){
		if (!initialized){
			super.buildMappings();			
		}
		initialized = true;
	}

	/**
	 * @see org.hibernate.cfg.AnnotationConfiguration#secondPassCompile()
	 */
	@Override
	protected void secondPassCompile() throws MappingException {
		if (!completedSecondPass){
			super.secondPassCompile();
		}
		completedSecondPass = true;
	}
	
	
}
