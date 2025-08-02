/**
 * *****************************************************************************
 * Copyright (c) 2012, William Rosmus All rights reserved.
 * 
* Redistribution and use in source and binary forms, with or without
 * modification, are not permitted without the express and explicit written
 * permission of the copyright holder or authorized representatives thereof. In
 * the event redistribution rights are granted the following conditions apply:
 * 
* 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
* 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
* 3. All advertising materials mentioning features or use of this software must
 * display the following acknowledgment: This product includes software
 * developed by Extreme Latitude Software.
 * 
* 4. Neither the name of Extreme Latitude Software nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
* THIS SOFTWARE IS PROVIDED BY EXTREME LATITUDE SOFTWARE AND WILLIAM ROSMUS
 * ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL EXTREME LATITUDE SOFTWARE NOR
 * WILLIAM ROSMUS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
******************************************************************************
 */
package com.extremelatitudesoftware.entity.services.presentation.article;

import com.extremelatitudesoftware.entity.utility.AbstractElsEntity;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author "Bill Rosmus"
 */
@Entity

@SqlResultSetMapping(name = "CommentInfoListItemDTOMapping", classes = {
    @ConstructorResult(targetClass = CommentInfoListItemDTO.class,
            columns = {
                @ColumnResult(name = "article_id"),
                @ColumnResult(name = "article_comment_id"),
                @ColumnResult(name = "parent_comment_id"),
                @ColumnResult(name = "article_title"),
                @ColumnResult(name = "article_published"),
                @ColumnResult(name = "article_publish_date"),
                @ColumnResult(name = "article_availability_state"),
                @ColumnResult(name = "article_enable_comments"),
                @ColumnResult(name = "comment_title"),
                @ColumnResult(name = "comment_hide"),
                @ColumnResult(name = "comment_created_timestamp"),
                @ColumnResult(name = "comment_person_id"),
                @ColumnResult(name = "aticle_party_id")
            })
})

@Table(name = "article_comment")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ArticleComment.findAll", query = "SELECT e FROM ArticleComment e"),
    @NamedQuery(name = "ArticleComment.findByArticleCommentId", query = "SELECT e FROM ArticleComment e WHERE e.articleCommentId = :articleCommentId"),
    @NamedQuery(name = "ArticleComment.findByArticleId", query = "SELECT e FROM ArticleComment e WHERE e.articleId = :articleId"),
    @NamedQuery(name = "ArticleComment.findByParentCommentId", query = "SELECT e FROM ArticleComment e WHERE e.parentCommentId = :parentCommentId"),
    @NamedQuery(name = "ArticleComment.findByTitle", query = "SELECT e FROM ArticleComment e WHERE e.title = :title"),
    @NamedQuery(name = "ArticleComment.findByComment", query = "SELECT e FROM ArticleComment e WHERE e.comment = :comment"),
    @NamedQuery(name = "ArticleComment.findByPersonId", query = "SELECT e FROM ArticleComment e WHERE e.personId = :personId"),
    @NamedQuery(name = "ArticleComment.findByConfirmedUser", query = "SELECT e FROM ArticleComment e WHERE e.confirmedUser = :confirmedUser"),
    @NamedQuery(name = "ArticleComment.findByModerationRank", query = "SELECT e FROM ArticleComment e WHERE e.moderationRank = :moderationRank"),
    @NamedQuery(name = "ArticleComment.findByModerationReason", query = "SELECT e FROM ArticleComment e WHERE e.moderationReason = :moderationReason"),
    @NamedQuery(name = "ArticleComment.findByHide", query = "SELECT e FROM ArticleComment e WHERE e.hide = :hide"),
    @NamedQuery(name = "ArticleComment.findByHideReason", query = "SELECT e FROM ArticleComment e WHERE e.hideReason = :hideReason"),
    @NamedQuery(name = "ArticleComment.findBySessionId", query = "SELECT e FROM ArticleComment e WHERE e.sessionId = :sessionId"),
    @NamedQuery(name = "ArticleComment.findByConfirmationUuid", query = "SELECT e FROM ArticleComment e WHERE e.confirmationUuid = :confirmationUuid"),
    @NamedQuery(name = "ArticleComment.findByCreatedTimestamp", query = "SELECT e FROM ArticleComment e WHERE e.createdTimestamp = :createdTimestamp"),
    @NamedQuery(name = "ArticleComment.findByCreatedById", query = "SELECT e FROM ArticleComment e WHERE e.createdById = :createdById"),
    @NamedQuery(name = "ArticleComment.findByUpatedTimestamp", query = "SELECT e FROM ArticleComment e WHERE e.updatedTimestamp = :updatedTimestamp"),
    @NamedQuery(name = "ArticleComment.findByUpatedById", query = "SELECT e FROM ArticleComment e WHERE e.updatedById = :updatedById")})
public class ArticleComment extends AbstractElsEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_comment_id")
    private BigInteger articleCommentId;
    @Basic(optional = false)
    @Column(name = "article_id")
    private BigInteger articleId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "parent_comment_id")
    private BigInteger parentCommentId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "title")
    private String title;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "comment")
    private String comment;
    @Basic(optional = false)
    @NotNull
    @Column(name = "person_id")
    private BigInteger personId;
    @Column(name = "confirmed_user")
    private Boolean confirmedUser;
    @Column(name = "comment_depth")
    private Integer commentDepth;
    @Column(name = "moderation_rank")
    private Integer moderationRank;
    @Size(max = 20)
    @Column(name = "moderation_reason")
    private String moderationReason;
    @Basic(optional = false)
    @NotNull
    @Column(name = "hide")
    private boolean hide;
    @Size(max = 20)
    @Column(name = "hide_reason")
    private String hideReason;
    @Size(max = 64)
    @Column(name = "session_id")
    private String sessionId;
    @Size(max = 64)
    @Column(name = "confirmation_uuid")
    private String confirmationUuid;

    public ArticleComment() {
    }

    public BigInteger getArticleCommentId() {
        return articleCommentId;
    }

    public void setArticleCommentId(BigInteger editorialCommentId) {
        this.articleCommentId = editorialCommentId;
    }

    public Integer getCommentDepth() {
        return commentDepth;
    }

    public void setCommentDepth(Integer commentDepth) {
        this.commentDepth = commentDepth;
    }

    public String getConfirmationUuid() {
        return confirmationUuid;
    }

    public void setConfirmationUuid(String confirmationUuid) {
        this.confirmationUuid = confirmationUuid;
    }

    public BigInteger getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(BigInteger parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public BigInteger getPersonId() {
        return personId;
    }

    public void setPersonId(BigInteger personId) {
        this.personId = personId;
    }

    public Boolean getConfirmedUser() {
        return confirmedUser;
    }

    public void setConfirmedUser(Boolean confirmedUser) {
        this.confirmedUser = confirmedUser;
    }

    public Integer getModerationRank() {
        return moderationRank;
    }

    public void setModerationRank(Integer moderationRank) {
        this.moderationRank = moderationRank;
    }

    public String getModerationReason() {
        return moderationReason;
    }

    public void setModerationReason(String moderationReason) {
        this.moderationReason = moderationReason;
    }

    public boolean getHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public String getHideReason() {
        return hideReason;
    }

    public void setHideReason(String hideReason) {
        this.hideReason = hideReason;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public BigInteger getArticalCommentId() {
        return articleCommentId;
    }

    public void setArticalCommentId(BigInteger articalCommentId) {
        this.articleCommentId = articalCommentId;
    }

    public BigInteger getArticleId() {
        return articleId;
    }

    public void setArticleId(BigInteger articleId) {
        this.articleId = articleId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ArticleComment other = (ArticleComment) obj;
        if (!Objects.equals(this.articleCommentId, other.articleCommentId)) {
            return false;
        }
        if (!Objects.equals(this.parentCommentId, other.parentCommentId)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.comment, other.comment)) {
            return false;
        }
        if (!Objects.equals(this.personId, other.personId)) {
            return false;
        }
        if (!Objects.equals(this.confirmedUser, other.confirmedUser)) {
            return false;
        }
        if (!Objects.equals(this.moderationRank, other.moderationRank)) {
            return false;
        }
        if (this.hide != other.hide) {
            return false;
        }
        if (!Objects.equals(this.hideReason, other.hideReason)) {
            return false;
        }
        if (!Objects.equals(this.sessionId, other.sessionId)) {
            return false;
        }
        if (!Objects.equals(this.createdTimestamp, other.createdTimestamp)) {
            return false;
        }
        if (!Objects.equals(this.createdById, other.createdById)) {
            return false;
        }
        if (!Objects.equals(this.updatedTimestamp, other.updatedTimestamp)) {
            return false;
        }
        if (!Objects.equals(this.updatedById, other.updatedById)) {
            return false;
        }
        if (!Objects.equals(this.articleId, other.articleId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.articleCommentId);
        hash = 31 * hash + Objects.hashCode(this.parentCommentId);
        hash = 31 * hash + Objects.hashCode(this.title);
        hash = 31 * hash + Objects.hashCode(this.comment);
        hash = 31 * hash + Objects.hashCode(this.personId);
        hash = 31 * hash + Objects.hashCode(this.confirmedUser);
        hash = 31 * hash + Objects.hashCode(this.moderationRank);
        hash = 31 * hash + (this.hide ? 1 : 0);
        hash = 31 * hash + Objects.hashCode(this.hideReason);
        hash = 31 * hash + Objects.hashCode(this.sessionId);
        hash = 31 * hash + Objects.hashCode(this.createdTimestamp);
        hash = 31 * hash + Objects.hashCode(this.createdById);
        hash = 31 * hash + Objects.hashCode(this.updatedTimestamp);
        hash = 31 * hash + Objects.hashCode(this.updatedById);
        hash = 31 * hash + Objects.hashCode(this.articleId);
        return hash;
    }

    @Override
    public String toString() {
        return "ArticleComment{" + "articleCommentId=" + articleCommentId + ", parentId=" + parentCommentId + ", title=" + title + ", comment=" + comment + ", personId=" + personId + ", confirmedUser=" + confirmedUser + ", rank=" + moderationRank + ", hide=" + hide + ", hideReason=" + hideReason + ", sessionId=" + sessionId + ", createdTimestamp=" + createdTimestamp + ", createdById=" + createdById + ", upatedTimestamp=" + updatedTimestamp + ", upatedById=" + updatedById + ", articleId=" + articleId + '}';
    }
}
