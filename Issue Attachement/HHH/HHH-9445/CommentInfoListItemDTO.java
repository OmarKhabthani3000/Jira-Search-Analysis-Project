/**
 * *****************************************************************************
 * Copyright (c) 2014, William Rosmus All rights reserved.
 * 
* Redistribution and use in source and binary forms, with or without
 * modification, are not permitted without the express and explicit written
 * permission of the copyright holder or authorized representatives thereof. In
 * the event redistribution rights are granted the following conditions apply:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. All advertising materials
 * mentioning features or use of this software must display the following
 * acknowledgment: This product includes software developed by Extreme Latitude
 * Software. 4. Neither the name of Extreme Latitude Software nor the names of
 * its contributors may be used to endorse or promote products derived
 * (including advertising) from this software without specific prior written
 * permission.
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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Objects;
import javax.persistence.Temporal;

/**
 *
 * @author bill
 */

public class CommentInfoListItemDTO implements Serializable {

    private BigInteger articleId;
    private BigInteger articleCommentId;
    private BigInteger parentCommentId;
    private String articleTitle;
    private Boolean articlePublished;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Calendar articlePublishDate;
    private String articleAvailabilityState;
    private Boolean articleEnableComments;
    private String commentTitle;
    private Boolean commentHide;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Calendar commentCreatedTimestamp;
    private BigInteger commentPersonId;
    private BigInteger articlePartyId;

    public CommentInfoListItemDTO() {
    }

    public CommentInfoListItemDTO(BigInteger articleId, BigInteger articleCommentId, 
            BigInteger parentCommentId, String articleTitle, Boolean articlePublished, 
            Calendar articlePublishDate, String articleAvailabilityState, 
            Boolean articleEnableComments, String commentTitle, Boolean commentHide, 
            Calendar commentCreatedTimestamp, BigInteger commentPersonId, 
            BigInteger articlePartyId) {
        this.articleId = articleId;
        this.articleCommentId = articleCommentId;
        this.parentCommentId = parentCommentId;
        this.articleTitle = articleTitle;
        this.articlePublished = articlePublished;
        this.articlePublishDate = articlePublishDate;
        this.articleAvailabilityState = articleAvailabilityState;
        this.articleEnableComments = articleEnableComments;
        this.commentTitle = commentTitle;
        this.commentHide = commentHide;
        this.commentCreatedTimestamp = commentCreatedTimestamp;
        this.commentPersonId = commentPersonId;
        this.articlePartyId = articlePartyId;
    }

    public BigInteger getArticleId() {
        return articleId;
    }

    public void setArticleId(BigInteger articleId) {
        this.articleId = articleId;
    }

    public BigInteger getArticleCommentId() {
        return articleCommentId;
    }

    public void setArticleCommentId(BigInteger articleCommentId) {
        this.articleCommentId = articleCommentId;
    }

    public BigInteger getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(BigInteger parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public Boolean getArticlePublished() {
        return articlePublished;
    }

    public void setArticlePublished(Boolean articlePublished) {
        this.articlePublished = articlePublished;
    }

    public Calendar getArticlePublishDate() {
        return articlePublishDate;
    }

    public void setArticlePublishDate(Calendar articlePublishDate) {
        this.articlePublishDate = articlePublishDate;
    }

    public String getArticleAvailabilityState() {
        return articleAvailabilityState;
    }

    public void setArticleAvailabilityState(String articleAvailabilityState) {
        this.articleAvailabilityState = articleAvailabilityState;
    }

    public Boolean getArticleEnableComments() {
        return articleEnableComments;
    }

    public void setArticleEnableComments(Boolean articleEnableComments) {
        this.articleEnableComments = articleEnableComments;
    }

    public String getCommentTitle() {
        return commentTitle;
    }

    public void setCommentTitle(String commentTitle) {
        this.commentTitle = commentTitle;
    }

    public Boolean getCommentHide() {
        return commentHide;
    }

    public void setCommentHide(Boolean commentHide) {
        this.commentHide = commentHide;
    }

    public Calendar getCommentCreatedTimestamp() {
        return commentCreatedTimestamp;
    }

    public void setCommentCreatedTimestamp(Calendar commentCreatedTimestamp) {
        this.commentCreatedTimestamp = commentCreatedTimestamp;
    }

    public BigInteger getCommentPersonId() {
        return commentPersonId;
    }

    public void setCommentPersonId(BigInteger commentPersonId) {
        this.commentPersonId = commentPersonId;
    }

    public BigInteger getArticlePartyId() {
        return articlePartyId;
    }

    public void setArticlePartyId(BigInteger articlePartyId) {
        this.articlePartyId = articlePartyId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.articleId);
        hash = 37 * hash + Objects.hashCode(this.articleCommentId);
        hash = 37 * hash + Objects.hashCode(this.parentCommentId);
        hash = 37 * hash + Objects.hashCode(this.articleTitle);
        hash = 37 * hash + Objects.hashCode(this.articlePublished);
        hash = 37 * hash + Objects.hashCode(this.articlePublishDate);
        hash = 37 * hash + Objects.hashCode(this.articleAvailabilityState);
        hash = 37 * hash + Objects.hashCode(this.articleEnableComments);
        hash = 37 * hash + Objects.hashCode(this.commentTitle);
        hash = 37 * hash + Objects.hashCode(this.commentHide);
        hash = 37 * hash + Objects.hashCode(this.commentCreatedTimestamp);
        hash = 37 * hash + Objects.hashCode(this.commentPersonId);
        hash = 37 * hash + Objects.hashCode(this.articlePartyId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CommentInfoListItemDTO other = (CommentInfoListItemDTO) obj;
        if (!Objects.equals(this.articleId, other.articleId)) {
            return false;
        }
        if (!Objects.equals(this.articleCommentId, other.articleCommentId)) {
            return false;
        }
        if (!Objects.equals(this.parentCommentId, other.parentCommentId)) {
            return false;
        }
        if (!Objects.equals(this.articleTitle, other.articleTitle)) {
            return false;
        }
        if (!Objects.equals(this.articlePublished, other.articlePublished)) {
            return false;
        }
        if (!Objects.equals(this.articlePublishDate, other.articlePublishDate)) {
            return false;
        }
        if (!Objects.equals(this.articleAvailabilityState, other.articleAvailabilityState)) {
            return false;
        }
        if (!Objects.equals(this.articleEnableComments, other.articleEnableComments)) {
            return false;
        }
        if (!Objects.equals(this.commentTitle, other.commentTitle)) {
            return false;
        }
        if (!Objects.equals(this.commentHide, other.commentHide)) {
            return false;
        }
        if (!Objects.equals(this.commentCreatedTimestamp, other.commentCreatedTimestamp)) {
            return false;
        }
        if (!Objects.equals(this.commentPersonId, other.commentPersonId)) {
            return false;
        }
        if (!Objects.equals(this.articlePartyId, other.articlePartyId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CommentTitleList{" + "articleId=" + articleId + ", articleCommentId=" + articleCommentId + ", parentCommentId=" + parentCommentId + ", articleTitle=" + articleTitle + ", articlePublished=" + articlePublished + ", articlePublishDate=" + articlePublishDate + ", articleAvailabilityState=" + articleAvailabilityState + ", articleEnableComments=" + articleEnableComments + ", CommentTitle=" + commentTitle + ", commentHidden=" + commentHide + ", commentCreatedTimestamp=" + commentCreatedTimestamp + ", commentPersonId=" + commentPersonId + ", articlePartyId=" + articlePartyId + '}';
    }
    
    
    
   
}