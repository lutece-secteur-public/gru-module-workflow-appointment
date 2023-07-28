package fr.paris.lutece.plugins.workflow.modules.appointment.service.archiver;

import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.workflow.modules.archive.service.AbstractArchiveProcessingService;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;

public class WorkflowAppointmentDeleteArchiveProcessingService extends AbstractArchiveProcessingService
{
	public static final String BEAN_NAME = "workflow-appointment.workflowAppointmentDeleteArchiveProcessingService";

	@Override
	public void archiveResource(ResourceWorkflow resourceWorkflow)
	{
		AppointmentService.deleteAppointment(resourceWorkflow.getIdResource());
	}

}
