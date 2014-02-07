<%@page import="fr.paris.lutece.plugins.workflow.modules.appointment.web.ExecuteWorkflowAction"%>
<%
    String strResult = ExecuteWorkflowAction.doExecuteWorkflowAction(request,response);
	if ( !response.isCommitted( ) )
	{
		response.sendRedirect( strResult );
	}
%>
