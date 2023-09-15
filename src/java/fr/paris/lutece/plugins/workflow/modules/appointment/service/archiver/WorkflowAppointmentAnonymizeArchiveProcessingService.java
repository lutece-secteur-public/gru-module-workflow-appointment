package fr.paris.lutece.plugins.workflow.modules.appointment.service.archiver;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentResponseHome;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.UserService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.anonymization.IEntryAnonymizationType;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflow.modules.appointment.service.archiver.anonymization.IAnonymizationService;
import fr.paris.lutece.plugins.workflow.modules.archive.service.AbstractArchiveProcessingService;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class WorkflowAppointmentAnonymizeArchiveProcessingService extends AbstractArchiveProcessingService
{
	
	public static final String BEAN_NAME = "workflow-appointment.workflowAppointmentAnonymizeArchiveProcessingService";

	@Override
	public void archiveResource(ResourceWorkflow resourceWorkflow)
	{
		Appointment appointment = AppointmentService.findAppointmentById( resourceWorkflow.getIdResource() );
		if (appointment != null)
		{
			anonymizeUserInfos(appointment);
			anonymizeGenericAttributesEntries(appointment);
		}
	}
	
	private void anonymizeUserInfos(Appointment appointment)
	{
		List<Slot> slotList = SlotService.findListSlotByIdAppointment(appointment.getIdAppointment());
		Form form = (slotList != null && !slotList.isEmpty()) ? FormService.findFormLightByPrimaryKey(slotList.get(0).getIdForm()) : null;
		User user = UserService.findUserById( appointment.getIdUser( ) );
		if (user != null && form != null && form.isAnonymizable())
		{
			 IAnonymizationService anonymizationService = getAnonymizationServiceByPattern(form.getAnonymizationPattern());
			 if (anonymizationService != null)
			 {
				 user.setFirstName(anonymizationService.getAnonymisedValue(form));
				 user.setLastName(anonymizationService.getAnonymisedValue(form));
				 user.setEmail(anonymizationService.getAnonymisedValue(form));
				 UserHome.update(user);
			 }
		}
	}
	
	private IAnonymizationService getAnonymizationServiceByPattern(String pattern)
	{
		List<IAnonymizationService> anonymizationServiceList = SpringContextService.getBeansOfType(IAnonymizationService.class);
		for(IAnonymizationService anonymizationService : anonymizationServiceList)
		{
			if (StringUtils.equals(pattern, anonymizationService.getPattern()))
			{
				return anonymizationService;
			}
		}
		return null;
	}
	
	private void anonymizeGenericAttributesEntries(Appointment appointment)
	{
		List<Response> listResponses = AppointmentResponseHome.findListResponse(appointment.getIdAppointment());
		for (Response response : listResponses)
		{
			Entry entry = EntryHome.findByPrimaryKey(response.getEntry().getIdEntry());
			IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );
			boolean first = true;
			for ( IEntryAnonymizationType wildcard : entryTypeService.getValidWildcards( ) )
		    {
		        wildcard.getAnonymisationTypeService( ).anonymizeResponse( entry, response, first );
		        first = false;
		    }
		    ResponseHome.update( response );
		}
	}
}
