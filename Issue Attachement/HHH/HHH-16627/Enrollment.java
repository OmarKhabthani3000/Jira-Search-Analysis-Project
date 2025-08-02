package org.hibernate.bugs.duplicates;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "enrollment")
public class Enrollment {
	private UUID id;
	private Integer score;
	private Student student;
	private Course course;
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	
	@Column(name = "score")
	public Integer getScore() { return score; }
	public void setScore(Integer score) { this.score = score; }
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "student_id", nullable = false)
	public Student getStudent() { return student; }
	public void setStudent(Student student) { this.student = student; }
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "course_id", nullable = false)
	public Course getCourse() { return course; }
	public void setCourse(Course course) { this.course = course; }
}
