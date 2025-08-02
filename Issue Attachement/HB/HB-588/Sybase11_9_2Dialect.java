package com.kbcam.core.persistence.dialect;

import net.sf.hibernate.dialect.SybaseDialect;
import net.sf.hibernate.sql.JoinFragment;
import com.kbcam.core.persistence.sql.Sybase11_9_2JoinFragment;

/**
 * A SQL dialect suitable for use with Sybase 11.9.2 (specifically: avoids ANSI JOIN syntax)
 * @author Colm O' Flaherty
 */

public class Sybase11_9_2Dialect extends SybaseDialect  {
	public Sybase11_9_2Dialect() {
		super();
	}

	public JoinFragment createOuterJoinFragment() {
			return new Sybase11_9_2JoinFragment();
	}

}
