/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.insertordering;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import org.junit.jupiter.api.Test;

public class InsertOrderingWithSelfManyToOneAndChildrenManyToOneEntities extends BaseInsertOrderingTest {

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] { PostComment.class, Post.class };
	}

	@Test
	public void testBatching() {
		sessionFactoryScope().inTransaction( session -> {
			Post parentPost = new Post();

			PostComment parentPostCommentA = new PostComment();
      parentPostCommentA.post = parentPost;
			PostComment parentPostCommentB = new PostComment();
      parentPostCommentB.post = parentPost;

			Post childPostA = new Post();
			childPostA.parent = parentPost;
			Post childPostB = new Post();
			childPostB.parent = parentPost;

			PostComment childApostComment = new PostComment();
      childApostComment.post = childPostA;
			PostComment childBpostComment = new PostComment();
      childBpostComment.post = childPostB;

			session.persist( parentPost );

			session.persist( parentPostCommentA );
			session.persist( parentPostCommentB );

			session.persist( childPostA );
			session.persist( childPostB );

			session.persist( childApostComment );
			session.persist( childBpostComment );

			clearBatches();
		} );

		verifyContainsBatches(
				new Batch( "insert into PostComment (post_ID,text,ID) values (?,?,?)", 4 ),
				new Batch( "insert into Post (name,parent_ID,ID) values (?,?,?)", 3 )
		);
	}

	@Entity(name = "PostComment")
	public static class PostComment {
		@Id
		@Column(name = "ID", nullable = false)
		@GeneratedValue(strategy = GenerationType.AUTO)
		private java.util.UUID id;

		private String text;

		@ManyToOne
		private Post post;
	}

	@Entity(name = "Post")
	public static class Post {
		@Id
		@Column(name = "ID", nullable = false)
		@GeneratedValue(strategy = GenerationType.AUTO)
		private java.util.UUID id;

		private String name;

		@ManyToOne
		private Post parent;
	}
}
