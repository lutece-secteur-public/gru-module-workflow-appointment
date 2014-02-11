<%@page import="fr.paris.lutece.portal.service.util.AppPathService"%>
<%@page import="fr.paris.lutece.portal.service.message.SiteMessageException"%>
<%@ page errorPage="../../../../ErrorPagePortal.jsp"%>
<jsp:useBean id="executeWorkflowAction" scope="session" class="fr.paris.lutece.plugins.workflow.modules.appointment.web.ExecuteWorkflowAction" />

<%
	try
	{
	    String strResult = executeWorkflowAction.doExecuteWorkflowAction( request, response );
	    if ( !response.isCommitted( ) )
	    {
	        response.sendRedirect( strResult );
	    }
	}
	catch ( SiteMessageException e )
	{
	    response.sendRedirect( AppPathService.getSiteMessageUrl( request ) );
	}
%>
