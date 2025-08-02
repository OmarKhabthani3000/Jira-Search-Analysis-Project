package org.hibernate.bugs;

public class Wrapper {

		public Wrapper() {
		}

		public Wrapper(JPAUnitTestCase.Department department, Long id) {
			this.id = id;
			this.department = department;
		}

		JPAUnitTestCase.Department department;
		Long id;

		public void setId(Long id) {
			this.id = id;
		}

		public Long getId() {
			return id;
		}

		public void setDepartment(JPAUnitTestCase.Department department) {
			this.department = department;
		}

		public JPAUnitTestCase.Department getDepartment() {
			return this.department;
		}
	}