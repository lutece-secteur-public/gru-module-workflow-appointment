<%@ page errorPage="../../../../ErrorPagePortal.jsp"%>

${ pageContext.response.sendRedirect( executeWorkflowAction.doExecuteWorkflowAction( pageContext.request, pageContext.response ) ) }
