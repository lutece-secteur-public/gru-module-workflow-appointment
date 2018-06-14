/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.appointment.business.NotifyAppointmentDTO;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskNotifyAdminAppointmentConfig;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskNotifyAppointmentConfig;
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

    // TEMPLATES
    private static final String TEMPLATE_TASK_NOTIFY_APPOINTMENT_CONFIG = "admin/plugins/workflow/modules/appointment/task_notify_appointment_config.html";

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
     * @return The HTML code to display
     */
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task, ITaskConfigService taskConfigService, boolean bNotifyAdmin )
    {
        TaskNotifyAppointmentConfig config = taskConfigService.findByPrimaryKey( task.getId( ) );

        ActionFilter filter = new ActionFilter( );
        Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );
        filter.setIdStateBefore( action.getStateAfter( ).getId( ) );

        List<Action> listActions = _actionService.getListActionByFilter( filter );

        if ( action.getStateAfter( ).getId( ) == action.getStateBefore( ).getId( ) )
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

        Map<String, Object> model = new HashMap<String, Object>( );

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
        String strSenderName = request.getParameter( PARAMETER_SENDER_NAME );
        String strSenderEmail = request.getParameter( PARAMETER_SENDER_EMAIL );
        String strSubject = request.getParameter( PARAMETER_SUBJECT );
        String strMessage = request.getParameter( PARAMETER_MESSAGE );
        String strRecipientsCc = request.getParameter( PARAMETER_RECIPIENTS_CC );
        String strRecipientsBcc = request.getParameter( PARAMETER_RECIPIENTS_BCC );
        boolean bSendICalNotif = Boolean.valueOf( request.getParameter( PARAMETER_SEND_ICAL_NOTIF ) );
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
        Boolean bCreate = false;

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
}
