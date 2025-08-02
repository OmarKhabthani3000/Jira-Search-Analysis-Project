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

import com.extremelatitudesoftware.els_commons.utilities.ELSLogger;
import com.extremelatitudesoftware.utilities.singleton.DozerInstantiator;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author bill
 */
@Stateless
public class CommentInfoListFacade {

    @Resource(mappedName = "jdbc/ElsProd1Pool")
    private javax.sql.DataSource ds;
    @PersistenceContext(unitName = "ELS_Soulard_PU")
    private EntityManager em;
    @EJB
    private DozerInstantiator di;
    private static final Logger LOGGER = Logger.getLogger("com.extremelatitudesoftware.entity.services.presentation.article.CommentTitleList");

    public List<CommentInfoListItemDTO> getCommentTitleListByPersonId(BigInteger personId) {
        String queryString = "select c.article_id, "
                        + "c.article_comment_id, "
                        + "c.parent_comment_id, "
                        + "a.title as article_title, "
                        + "a.publish_flag as article_published,"
                        + "a.publish_date as article_publish_date, "
                        + "a.availability_state as article_availability_state, "
                        + "a.enable_comments as article_enable_comments, "
                        + "c.title as comment_title, "
                        + "c.hide as comment_hide, "
                        + "c.created_timestamp as comment_created_timestamp, "
                        + "c.person_id as comment_person_id, "
                        + "a.party_id as aticle_party_id "
                        + "from article_comment c "
                        + "join article a "
                        + "on a.article_id = c.article_id "
                        + "where c.person_id = :personId";
        
        Query q = em.createNativeQuery(queryString, "CommentInfoListItemDTOMapping");
        q.setParameter("personId", personId);

        List<CommentInfoListItemDTO> commentInfoList = (List<CommentInfoListItemDTO>)q.getResultList();

        int listSize = commentInfoList.size();

        if (listSize == 0) {
            String msg = String.format("Could not find comment with personId: %d\n", personId);
            ELSLogger.LOG(Level.INFO, this.getClass().getName(), "getCommentTitleListByPersonId", msg);
            return (null);
        }

        for (CommentInfoListItemDTO listElement : commentInfoList){
                System.out.println("COMMENT TITLE LIST: " + listElement.toString());
        }
        return (commentInfoList);
    }

}
