package fr.paris.lutece.plugins.workflow.modules.appointment.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.appointment.business.NotifyAppointmentDTO;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskNotifyAdminAppointmentConfig;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskNotifyAppointmentConfig;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskNotifyCrmConfig;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskUpdateAppointmentCancelActionConfig;
import fr.paris.lutece.plugins.workflow.modules.appointment.service.TaskNotifyAdminAppointment;
import fr.paris.lutece.plugins.workflow.modules.appointment.service.TaskNotifyAppointementCrm;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.NoConfigTaskComponent;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.string.StringUtil;



public class NotifyCrmTaskComponent extends NoFormTaskComponent {

	 // TEMPLATES
    private static final String TEMPLATE_NOTIFY_CRM = "admin/plugins/workflow/modules/appointment/task_notify_appointement_crm.html";

    // MESSAGES
    private static final String MESSAGE_MANDATORY_FIELD = "portal.util.message.mandatoryFields";
    private static final String MESSAGE_ADMIN_USER_ASSOCIATED_TO_APPOINTMENT = "module.workflow.appointment.task_update_admin_appointment_config.adminUserAssociatedToAppointment";
    private static final String ERROR_MESSAGE_ADMIN_USER_BUSY = "module.workflow.appointment.task_update_admin_appointment_config.adminUserAlreadyBusy";

    // MARKS
    private static final String MARK_CONFIG = "config";

    // PARAMETERS
    private static final String PARAMETER_ID_DEMANDE_TYPE = "demandeType";
    private static final String PARAMETER_DATA = "data";
    private static final String PARAMETER_STATUS_TEXT = "statusText";
    private static final String PARAMETER_ID_STATUS_CRM= "idStatusCRM";
    private static final String PARAMETER_OBJECT= "object";
    private static final String PARAMETER_MESSAGE = "message";
    private static final String PARAMETER_SENDER= "sender";

    // CONSTANTS
    private static final String CONSTANT_SPACE = " ";
    
 // SERVICES
    @Inject
    @Named( TaskNotifyAppointementCrm.CONFIG_SERVICE_BEAN_NAME )
    private ITaskConfigService _taskNotifyAppointmentCrmConfigService;

	@Override
	public String getDisplayConfigForm(HttpServletRequest request,
			Locale locale, ITask task) {
		Map<String, Object> model = new HashMap<String, Object>(  );
		
		TaskNotifyCrmConfig config = _taskNotifyAppointmentCrmConfigService.findByPrimaryKey( task.getId(  ) );

	        model.put( MARK_CONFIG, config);
	     //   model.put( MARK_LOCALE, locale );
	      //  model.put( MARK_DEFAULT_SENDER_NAME, MailService.getNoReplyEmail(  ) );

	        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_NOTIFY_CRM, locale, model );

	        return template.getHtml(  );
		
	}

	@Override
	public String getDisplayTaskInformation(int nIdHistory,
			HttpServletRequest request, Locale locale, ITask task) {
		
		return I18nService.getLocalizedString("module.workflow.appointment.taskNotifyAppointmentCrm.title", locale);
	}

	@Override
	public String getTaskInformationXml(int nIdHistory,
			HttpServletRequest request, Locale locale, ITask task) {
		// TODO Auto-generated method stub
		return null;
	}

	 /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
    	 String strDemandeType = request.getParameter( PARAMETER_ID_DEMANDE_TYPE );
         String strData = request.getParameter( PARAMETER_DATA );
         String strStatusText = request.getParameter( PARAMETER_STATUS_TEXT );
         String strIdStatusCRM = request.getParameter( PARAMETER_ID_STATUS_CRM );
         String strObject = request.getParameter( PARAMETER_OBJECT );
         String strMessage = request.getParameter( PARAMETER_MESSAGE );
         String strSender = request.getParameter( PARAMETER_SENDER );
         
         TaskNotifyCrmConfig config = _taskNotifyAppointmentCrmConfigService.findByPrimaryKey( task.getId(  ) );
         Boolean bCreate = false;

         if ( config == null )
         {
            
            config = new TaskNotifyCrmConfig(  );
            config.setIdTask( task.getId(  ) );
            bCreate = true;
         }

         config.setDemandeType(strDemandeType);
         config.setData(strData);
         config.setIdStatusCRM(strIdStatusCRM);
         config.setStatusText(strStatusText);
         config.setObject(strObject);
         config.setMessage(strMessage);
         config.setSender(strSender);

         if ( bCreate )
         {
        	 _taskNotifyAppointmentCrmConfigService.create( config );
         }
         else
         {
        	 _taskNotifyAppointmentCrmConfigService.update( config );
         }

         return null;
     }
    	
}
