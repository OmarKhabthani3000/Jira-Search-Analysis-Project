package com.tura.common.domain;

public class Parent
{
	protected Long id;

	public static enum Manufacturers
	{
		UNKNOWN("Unknown");

		public String label;

		Manufacturers(String name)
		{
			this.label = name;
		}

		public String getLabel()
		{
			return label;
		}

		public void setLabel()
		{
		}
	}

	public static String UNKNOWN = Manufacturers.UNKNOWN.getLabel();

	public Parent()
	{
		// TODO Auto-generated constructor stub
	}
}
