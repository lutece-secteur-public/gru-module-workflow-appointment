/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskNotifyAppointmentConfig;
import fr.paris.lutece.plugins.workflow.modules.appointment.service.TaskNotifyAppointmentConfigService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * NotifyAppointmentTaskComponent
 *
 */
public class NotifyAppointmentTaskComponent extends NoFormTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_NOTIFY_APPOINTMENT_CONFIG = "admin/plugins/workflow/modules/appointment/task_notify_appointment_config.html";

    // FIELDS
    private static final String FIELD_SUBJECT = "module.workflow.appointment.task_notify_appointment_config.label_subject";
    private static final String FIELD_MESSAGE = "module.workflow.appointment.task_notify_appointment_config.label_message";
    private static final String FIELD_SENDER_NAME = "module.workflow.appointment.task_notify_appointment_config.label_sender_name";

    // MESSAGES
    private static final String MESSAGE_MANDATORY_FIELD = "module.workflow.appointment.message.mandatory.field";
    private static final String MESSAGE_EMAIL_SENT_TO_USER = "module.workflow.appointment.message.emailSentToUser";

    // MARKS
    private static final String MARK_DEFAULT_SENDER_NAME = "default_sender_name";
    private static final String MARK_CONFIG = "config";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";

    // PARAMETERS
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_SUBJECT = "subject";
    private static final String PARAMETER_MESSAGE = "message";
    private static final String PARAMETER_SENDER_NAME = "sender_name";
    private static final String PARAMETER_RECIPIENTS_CC = "recipients_cc";
    private static final String PARAMETER_RECIPIENTS_BCC = "recipients_bcc";

    // SERVICES
    @Inject
    @Named( TaskNotifyAppointmentConfigService.BEAN_SERVICE )
    private ITaskConfigService _taskNotifyAppointmentConfigService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        String strSenderName = request.getParameter( PARAMETER_SENDER_NAME );
        String strSubject = request.getParameter( PARAMETER_SUBJECT );
        String strMessage = request.getParameter( PARAMETER_MESSAGE );
        String strRecipientsCc = request.getParameter( PARAMETER_RECIPIENTS_CC );
        String strRecipientsBcc = request.getParameter( PARAMETER_RECIPIENTS_BCC );
        String strApply = request.getParameter( PARAMETER_APPLY );
        String strError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strApply ) )
        {
            if ( StringUtils.isBlank( strSenderName ) )
            {
                strError = FIELD_SENDER_NAME;
            }
            else if ( StringUtils.isBlank( strSubject ) )
            {
                strError = FIELD_SUBJECT;
            }
            else if ( StringUtils.isBlank( strMessage ) )
            {
                strError = FIELD_MESSAGE;
            }
        }

        if ( !strError.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        TaskNotifyAppointmentConfig config = _taskNotifyAppointmentConfigService.findByPrimaryKey( task.getId(  ) );
        Boolean bCreate = false;

        if ( config == null )
        {
            config = new TaskNotifyAppointmentConfig(  );
            config.setIdTask( task.getId(  ) );
            bCreate = true;
        }

        config.setMessage( strMessage );
        config.setSenderName( strSenderName );
        config.setSubject( strSubject );
        config.setRecipientsCc( StringUtils.isNotEmpty( strRecipientsCc ) ? strRecipientsCc : StringUtils.EMPTY );
        config.setRecipientsBcc( StringUtils.isNotEmpty( strRecipientsBcc ) ? strRecipientsBcc : StringUtils.EMPTY );

        if ( bCreate )
        {
            _taskNotifyAppointmentConfigService.create( config );
        }
        else
        {
            _taskNotifyAppointmentConfigService.update( config );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        TaskNotifyAppointmentConfig config = _taskNotifyAppointmentConfigService.findByPrimaryKey( task.getId(  ) );

        String strDefaultSenderName = MailService.getNoReplyEmail(  );

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_CONFIG, config );
        model.put( MARK_DEFAULT_SENDER_NAME, strDefaultSenderName );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, request.getLocale(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_NOTIFY_APPOINTMENT_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return I18nService.getLocalizedString( MESSAGE_EMAIL_SENT_TO_USER, locale );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        // TODO Auto-generated method stub
        return null;
    }
}
