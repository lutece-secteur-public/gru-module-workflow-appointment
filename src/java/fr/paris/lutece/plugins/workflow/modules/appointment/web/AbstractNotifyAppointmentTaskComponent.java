/*
 * Copyright (c) 2002-2022, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.appointment.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.referencelist.business.ReferenceItem;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.NotifyAppointmentDTO;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskNotifyAdminAppointmentConfig;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskNotifyAppointmentConfig;
import fr.paris.lutece.plugins.workflow.modules.appointment.provider.AppointmentNotificationMarkers;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
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
import fr.paris.lutece.util.url.UrlItem;

/**
 * Abstract task component to notify a user of an appointment
 */
public abstract class AbstractNotifyAppointmentTaskComponent extends NoFormTaskComponent
{
    // CONSTANTS
    private static final String CONSTANT_SPACE = " ";

    // MARKS
    private static final String MARK_CONFIG = "config";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LIST_ACTIONS = "list_actions";
    private static final String MARK_NOTIFY_ADMIN = "notify_admin";
    private static final String MARK_LIST_ADMIN_USERS = "list_admin_users";
    private static final String MARK_DEFAULT_SENDER_NAME = "default_sender_name";
    private static final String MARK_LIST_MARKERS = "list_markers";
    private static final String MARK_TASK_TITLE = "task_title";
    private static final String MARK_LIST_APPOINTMENT_FORMS = "list_appointment_forms";
    private static final String MARK_ID_SELECTED_APPOINTMENT_FORM = "id_selected_appointment_form";

    // PARAMETERS
    private static final String PARAMETER_SUBJECT = "subject";
    private static final String PARAMETER_MESSAGE = "message";
    private static final String PARAMETER_SEND_SMS = "send_sms";
    private static final String PARAMETER_LOCATION = "location";
    private static final String PARAMETER_SENDER_NAME = "sender_name";
    private static final String PARAMETER_SENDER_EMAIL = "sender_email";
    private static final String PARAMETER_CREATE_NOTIF = "create_notif";
    private static final String PARAMETER_ID_ADMIN_USER = "id_admin_user";
    private static final String PARAMETER_RECIPIENTS_CC = "recipients_cc";
    private static final String PARAMETER_RECIPIENTS_BCC = "recipients_bcc";
    private static final String PARAMETER_SEND_ICAL_NOTIF = "send_ical_notif";
    private static final String PARAMETER_ID_ACTION_CANCEL = "id_action_cancel";
    private static final String PARAMETER_ID_ACTION_VALIDATE = "id_action_validate";
    private static final String PARAMETER_ID_TASK = "id_task";
    private static final String PARAMETER_SELECT_APPOINTMENT_FORMS = "id_list_appointment_forms";
    private static final String PARAMETER_APPLY_APPOINTMENT_FORM_SELECTION = "apply_appointment_form_selection";
    private static final String PARAMETER_SELECTED_APPOINTMENT_FORM = "selected_appointment_form";

    // ACTIONS
    private static final String ACTION_APPLY_APPOINTMENT_FORM_SELECTION = "applyAppointmentFormSelection";

    // TEMPLATES
    private static final String TEMPLATE_TASK_NOTIFY_APPOINTMENT_CONFIG = "admin/plugins/workflow/modules/appointment/task_notify_appointment_config.html";

    // JSPs
    private static final String JSP_MODIFY_TASK = "jsp/admin/plugins/workflow/ModifyTask.jsp";

    // FIELDS
    private static final String FIELD_SUBJECT = "module.workflow.appointment.task_notify_appointment_config.label_subject";
    private static final String FIELD_MESSAGE = "module.workflow.appointment.task_notify_appointment_config.label_message";
    private static final String FIELD_SENDER_NAME = "module.workflow.appointment.task_notify_appointment_config.label_sender_name";
    private static final String FIELD_SENDER_EMAIL = "module.workflow.appointment.task_notify_appointment_config.label_sender_email";
    private static final String FIELD_SENDER_EMAIL_NOT_VALID = "module.workflow.appointment.task_notify_appointment_config.sender_email_not_valid";

    // MESSAGES
    private static final String MESSAGE_MANDATORY_FIELD = "module.workflow.appointment.message.mandatory.field";

    // SERVICES
    @Inject
    @Named( ActionService.BEAN_SERVICE )
    private ActionService _actionService;

    /**
     * Get the config form to display
     * 
     * @param request
     *            The request
     * @param locale
     *            The locale
     * @param task
     *            The task
     * @param taskConfigService
     *            The task config service to use
     * @param bNotifyAdmin
     *            True to notify an admin user, false to notify the user of the appointment
     * @param taskConfigTitle
     *            Title displayed on the configuration form's page
     * @return The HTML code to display
     */
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task, ITaskConfigService taskConfigService, boolean bNotifyAdmin, String taskConfigTitle )
    {
        TaskNotifyAppointmentConfig config = taskConfigService.findByPrimaryKey( task.getId( ) );

        // Get the selected Appointment Form's ID
        int idSelectedForm = getSelectedAppointmentFormId( request, config );

        ActionFilter filter = new ActionFilter( );
        filter.setAutomaticReflexiveAction( false );
        Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );
        filter.setIdStateBefore( action.getStateAfter( ).getId( ) );

        List<Action> listActions = _actionService.getListActionByFilter( filter );
        List<Integer> listIdStateBefore = action.getListIdStateBefore();
        int stateAfterId = action.getStateAfter().getId();
        int alternativeStateAfterId = action.getStateAfter().getId();

        if (listIdStateBefore.contains(stateAfterId) || listIdStateBefore.contains(alternativeStateAfterId))
        {
            for ( Action actionFound : listActions )
            {
                if ( actionFound.getId( ) == action.getId( ) )
                {
                    listActions.remove( actionFound );

                    break;
                }
            }
        }

        ReferenceList refListActions = new ReferenceList( listActions.size( ) + 1 );
        refListActions.addItem( 0, StringUtils.EMPTY );

        for ( Action actionFound : listActions )
        {
            refListActions.addItem( actionFound.getId( ), actionFound.getName( ) );
        }

        String strDefaultSenderName = MailService.getNoReplyEmail( );

        Map<String, Object> model = new HashMap<>( );

        if ( bNotifyAdmin )
        {
            Collection<AdminUser> listAdminUser = AdminUserHome.findUserList( );
            ReferenceList refListAdmins = new ReferenceList( );
            refListAdmins.addItem( StringUtils.EMPTY, StringUtils.EMPTY );

            for ( AdminUser adminUser : listAdminUser )
            {
                refListAdmins.addItem( adminUser.getUserId( ), adminUser.getFirstName( ) + CONSTANT_SPACE + adminUser.getLastName( ) );
            }

            model.put( MARK_LIST_ADMIN_USERS, refListAdmins );
        }

        model.put( MARK_NOTIFY_ADMIN, bNotifyAdmin );
        model.put( MARK_CONFIG, config );
        model.put( MARK_DEFAULT_SENDER_NAME, strDefaultSenderName );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, locale );
        model.put( MARK_LIST_ACTIONS, refListActions );
        model.put( MARK_LIST_MARKERS, AppointmentNotificationMarkers.getMarkerDescriptions( idSelectedForm, bNotifyAdmin ) );
        model.put( MARK_ID_SELECTED_APPOINTMENT_FORM, idSelectedForm );
        // Set the custom title to display in this task's config
        model.put( MARK_TASK_TITLE, taskConfigTitle );
        // Set the list of appointment forms available, to display their specific InfoMarkers (depends on their entries)
        model.put( MARK_LIST_APPOINTMENT_FORMS, getDisplayedFormList( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_NOTIFY_APPOINTMENT_CONFIG, locale, model );

        return template.getHtml( );
    }

    /**
     * Do save the configuration of the task
     * 
     * @param request
     *            The request
     * @param locale
     *            The locale
     * @param task
     *            The task
     * @param taskConfigService
     *            The task config service to use
     * @param bNotifyAdmin
     *            True to notify the admin user, false to notify the user of the appointment
     * @return The next URL to redirect to if an error occurs, or null if there was no error.
     */
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task, ITaskConfigService taskConfigService, boolean bNotifyAdmin )
    {
        String paramActionAppointmentFormSelection = request.getParameter( PARAMETER_APPLY_APPOINTMENT_FORM_SELECTION );
        String strSelectedAppointmentForm = request.getParameter( PARAMETER_SELECT_APPOINTMENT_FORMS );
        // If the user clicks on the refresh button when selecting an Appointment Form, then reload the current page with that form's specific Entry Markers
        if( StringUtils.equals( paramActionAppointmentFormSelection, ACTION_APPLY_APPOINTMENT_FORM_SELECTION ) &&
                StringUtils.isNotBlank( strSelectedAppointmentForm ) )
        {
            // Reload the page with the new data (selected Appointment Form)
            return getUrlToTaskModificationPage( request, task.getId( ), strSelectedAppointmentForm );
        }

        String strSenderName = request.getParameter( PARAMETER_SENDER_NAME );
        String strSenderEmail = request.getParameter( PARAMETER_SENDER_EMAIL );
        String strSubject = request.getParameter( PARAMETER_SUBJECT );
        String strMessage = request.getParameter( PARAMETER_MESSAGE );
        String strRecipientsCc = request.getParameter( PARAMETER_RECIPIENTS_CC );
        String strRecipientsBcc = request.getParameter( PARAMETER_RECIPIENTS_BCC );
        boolean bSendICalNotif = Boolean.parseBoolean( request.getParameter( PARAMETER_SEND_ICAL_NOTIF ) );
        String strLocation = request.getParameter( PARAMETER_LOCATION );

        String strError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strSenderName ) )
        {
            strError = FIELD_SENDER_NAME;
        }

        if ( StringUtils.isBlank( strSenderEmail ) )
        {
            strError = FIELD_SENDER_EMAIL;
        }
        else
            if ( StringUtils.isBlank( strSubject ) )
            {
                strError = FIELD_SUBJECT;
            }
            else
                if ( StringUtils.isBlank( strMessage ) )
                {
                    strError = FIELD_MESSAGE;
                }

        if ( !StringUtil.checkEmail( strSenderEmail ) )
        {
            strError = FIELD_SENDER_EMAIL_NOT_VALID;
        }

        if ( !strError.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strError, locale )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        NotifyAppointmentDTO config = taskConfigService.findByPrimaryKey( task.getId( ) );
        boolean bCreate = false;

        if ( config == null )
        {
            if ( bNotifyAdmin )
            {
                config = new TaskNotifyAdminAppointmentConfig( );
            }
            else
            {
                config = new TaskNotifyAppointmentConfig( );
            }

            config.setIdTask( task.getId( ) );
            bCreate = true;
        }

        config.setMessage( strMessage );
        config.setSenderEmail( strSenderEmail );
        config.setSenderName( strSenderName );
        config.setSubject( strSubject );
        config.setRecipientsCc( StringUtils.isNotEmpty( strRecipientsCc ) ? strRecipientsCc : StringUtils.EMPTY );
        config.setRecipientsBcc( StringUtils.isNotEmpty( strRecipientsBcc ) ? strRecipientsBcc : StringUtils.EMPTY );
        config.setSendICalNotif( bSendICalNotif );
        config.setLocation( strLocation );
        config.setIdAppointmentForm( StringUtils.isNumeric( strSelectedAppointmentForm ) ? Integer.parseInt( strSelectedAppointmentForm ) : 0 );

        if ( bSendICalNotif )
        {
            config.setCreateNotif( Boolean.parseBoolean( request.getParameter( PARAMETER_CREATE_NOTIF ) ) );
        }

        String strIdAction = request.getParameter( PARAMETER_ID_ACTION_CANCEL );
        int nIdAction = 0;

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) )
        {
            nIdAction = Integer.parseInt( strIdAction );
        }

        if ( bNotifyAdmin )
        {
            TaskNotifyAdminAppointmentConfig configAdmin = (TaskNotifyAdminAppointmentConfig) config;
            configAdmin.setIdActionCancel( nIdAction );

            String strIdActionValidate = request.getParameter( PARAMETER_ID_ACTION_VALIDATE );
            int nIdActionValidate = 0;

            if ( StringUtils.isNotEmpty( strIdActionValidate ) && StringUtils.isNumeric( strIdActionValidate ) )
            {
                nIdActionValidate = Integer.parseInt( strIdActionValidate );
            }

            configAdmin.setIdActionValidate( nIdActionValidate );

            String strIdAdminUser = request.getParameter( PARAMETER_ID_ADMIN_USER );

            if ( StringUtils.isNotEmpty( strIdAdminUser ) && StringUtils.isNumeric( strIdAdminUser ) )
            {
                int nIdAdminUser = Integer.parseInt( strIdAdminUser );

                if ( nIdAdminUser > 0 )
                {
                    configAdmin.setIdAdminUser( nIdAdminUser );
                }
            }
        }
        else
        {
            boolean bNotifySms = Boolean.parseBoolean( request.getParameter( PARAMETER_SEND_SMS ) );
            config.setIsSms( bNotifySms );

            if ( bNotifySms )
            {
                config.setSendICalNotif( false );
            }

            ( (TaskNotifyAppointmentConfig) config ).setIdActionCancel( nIdAction );
        }

        if ( bCreate )
        {
            taskConfigService.create( config );
        }
        else
        {
            taskConfigService.update( config );
        }

        return null;
    }

    /**
     * Get the ID of the Appointment Form selected in the Task's configuration parameters
     * 
     * @param request
     *            The HTTTP request
     * @param config
     *            The config of the current Task. Can be null if the config has not been created yet
     * @return the ID of the Appointment Form selected
     */
    private int getSelectedAppointmentFormId( HttpServletRequest request, TaskNotifyAppointmentConfig config )
    {
        // When the page is reloaded after a form has been selected
        String selectedAppointmentForm = request.getParameter( PARAMETER_SELECTED_APPOINTMENT_FORM );
        if ( StringUtils.isNumeric( selectedAppointmentForm ) )
        {
            return Integer.parseInt( selectedAppointmentForm );
        }
        if ( config != null )
        {
            return config.getIdAppointmentForm( );
        }
        return 0;
    }

    /**
     * Build and return the URL to modify a task (reloads the page)
     * 
     * @param request
     *            The HTTP request
     * @param idTask
     *            The ID of the task whose configuration is getting modified
     * @param idForm
     *            ID of the form selected by the user
     * @return The URL of this config's modification page
     */
    private String getUrlToTaskModificationPage( HttpServletRequest request, int idTask, String appointmentForm )
    {
        StringBuilder redirectUrl = new StringBuilder( AppPathService.getBaseUrl( request ) );
        redirectUrl.append( JSP_MODIFY_TASK );

        UrlItem url = new UrlItem( redirectUrl.toString( ) );
        url.addParameter( PARAMETER_ID_TASK, idTask );
        url.addParameter( PARAMETER_SELECTED_APPOINTMENT_FORM, appointmentForm );

        return url.getUrl( );
    }

    /**
     * Get a ReferenceList containing all the available Appointment Forms
     * 
     * @return the ReferenceList of all the Appointment Forms
     */
    private ReferenceList getDisplayedFormList( )
    {
        // Get a ReferenceList of all available Appointment Forms
        ReferenceList refListForms = FormService.findAllInReferenceList( );

        // Add a ReferenceItem to represent an empty choice / no value
        ReferenceItem emptyReferenceItem = new ReferenceItem ( );
        emptyReferenceItem.setCode( StringUtils.EMPTY );
        emptyReferenceItem.setName( StringUtils.EMPTY );

        refListForms.add( 0, emptyReferenceItem );

        return refListForms;
    }
}
