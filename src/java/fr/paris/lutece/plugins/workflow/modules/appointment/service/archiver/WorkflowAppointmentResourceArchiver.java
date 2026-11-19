package fr.paris.lutece.plugins.workflow.modules.appointment.service.archiver;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.workflow.modules.archive.ArchivalType;
import fr.paris.lutece.plugins.workflow.modules.archive.IResourceArchiver;
import fr.paris.lutece.plugins.workflow.modules.archive.service.IArchiveProcessingService;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;

@ApplicationScoped
@Named( "workflow-appointment.workflowAppointmentResourceArchiver" )
public class WorkflowAppointmentResourceArchiver implements IResourceArchiver {
	
	public static final String BEAN_NAME = "workflow-appointment.workflowAppointmentResourceArchiver";
	
	@Inject
    @Named( WorkflowAppointmentAnonymizeArchiveProcessingService.BEAN_NAME )
    private IArchiveProcessingService _anonymizeArchiveProcessingService;
	
	@Inject
    @Named( WorkflowAppointmentDeleteArchiveProcessingService.BEAN_NAME )
    private IArchiveProcessingService _deleteArchiveProcessingService;

	@Override
	public void archiveResource(ArchivalType archivalType, ResourceWorkflow resourceWorkflow) {
		// The archive task calls every archiver for each archived resource: only the appointments are handled here, the id of a
		// resource of another type (a forms response...) would delete or anonymize the appointment that has the same id
		if ( resourceWorkflow == null || !Appointment.APPOINTMENT_RESOURCE_TYPE.equals( resourceWorkflow.getResourceType( ) ) )
		{
			return;
		}
		switch( archivalType )
        {
        	case DELETE:
        		_deleteArchiveProcessingService.archiveResource(resourceWorkflow);
        		break;
	        case ANONYMIZE:
	            _anonymizeArchiveProcessingService.archiveResource( resourceWorkflow );
	            break;
	        default:
	            break;
        }
	}

	@Override
	public String getBeanName()
	{
		return BEAN_NAME;
	}

}
