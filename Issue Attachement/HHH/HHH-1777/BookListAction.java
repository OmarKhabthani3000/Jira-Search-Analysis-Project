//Created by MyEclipse Struts
// XSL source (default): platform:/plugin/com.genuitec.eclipse.cross.easystruts.eclipse_4.1.1/xslt/JavaClass.xsl

package de.laliluna.library.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import de.laliluna.library.bl.LibraryManager;
import de.laliluna.library.struts.form.BookListForm;

/** 
 * MyEclipse Struts
 * Creation date: 05-23-2006
 * 
 * XDoclet definition:
 * @struts.action path="/bookList" name="bookListForm" input="/form/bookList.jsp" scope="request" validate="true"
 * @struts.action-forward name="showList" path="/jsp/bookList.jsp"
 */
public class BookListAction extends Action {

	// --------------------------------------------------------- Instance Variables

	// --------------------------------------------------------- Methods

	 /** 
	   * Method loads book from DB
	   * @param mapping
	   * @param form
	   * @param request
	   * @param response
	   * @return ActionForward
	   */
	  public ActionForward execute(
	    ActionMapping mapping,
	    ActionForm form,
	    HttpServletRequest request,
	    HttpServletResponse response)
	  {
	    BookListForm bookListForm = (BookListForm) form;
	    // [laliluna] 27.11.2004 get busines logic 
	    LibraryManager libraryManager = new LibraryManager();
	    // [laliluna] 29.11.2004 update the form bean, from which the jsp will read the data later.
	    
	    bookListForm.setBooks(libraryManager.getAllBooks());
	    
	    return mapping.findForward("showList");
	  }


}

