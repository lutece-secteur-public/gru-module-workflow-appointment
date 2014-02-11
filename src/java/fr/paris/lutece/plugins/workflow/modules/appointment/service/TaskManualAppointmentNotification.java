/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.appointment.service;

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.appointment.business.AppointmentHome;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.ManualAppointmentNotificationHistory;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.ManualAppointmentNotificationHistoryHome;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.NotifyAppointmentDTO;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;

import org.apache.commons.lang.StringUtils;

import java.util.Locale;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;


/**
 * TaskNotifyAppointment
 */
public class TaskManualAppointmentNotification extends AbstractTaskNotifyAppointment<NotifyAppointmentDTO>
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_NOTIFY_MAIL = "admin/plugins/workflow/modules/appointment/task_notify_appointment_mail.html";

    // Messages
    private static final String MESSAGE_TASK_TITLE = "module.workflow.appointment.taskManualAppointmentNotification.title";

    // Marks
    private static final String MARK_MESSAGE = "message";
    private static final String MARK_LIST_RESPONSE = "listResponse";
    private static final String MARK_FIRSTNAME = "firstName";
    private static final String MARK_LASTNAME = "lastName";
    private static final String MARK_EMAIL = "email";
    private static final String MARK_REFERENCE = "reference";
    private static final String MARK_DATE_APPOINTMENT = "date_appointment";
    private static final String MARK_TIME_APPOINTMENT = "time_appointment";

    // Parameters
    private static final String PARAMETER_SENDER_NAME = "sender_name";
    private static final String PARAMETER_CC = "receipient_cc";
    private static final String PARAMETER_BCC = "receipient_bcc";
    private static final String PARAMETER_MESSAGE = "message";
    private static final String PARAMETER_SUBJECT = "subject";

    // SERVICES
    @Inject
    private IResourceHistoryService _resourceHistoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        Appointment appointment = AppointmentHome.findByPrimaryKey( resourceHistory.getIdResource(  ) );

        String strSenderName = request.getParameter( PARAMETER_SENDER_NAME );
        String strCc = request.getParameter( PARAMETER_CC );
        String strBcc = request.getParameter( PARAMETER_BCC );
        String strMessage = request.getParameter( PARAMETER_MESSAGE );
        String strSubject = request.getParameter( PARAMETER_SUBJECT );

        if ( StringUtils.isBlank( strSenderName ) )
        {
            strSenderName = MailService.getNoReplyEmail(  );
        }

        NotifyAppointmentDTO notifyAppointmentDTO = new NotifyAppointmentDTO(  );
        notifyAppointmentDTO.setMessage( strMessage );
        notifyAppointmentDTO.setRecipientsCc( strCc );
        notifyAppointmentDTO.setRecipientsBcc( strBcc );
        notifyAppointmentDTO.setSubject( strSubject );
        notifyAppointmentDTO.setSenderName( strSenderName );

        String strContent = this.sendEmail( appointment, resourceHistory, request, locale, notifyAppointmentDTO,
                appointment.getEmail(  ) );

        if ( strContent != null )
        {
            ManualAppointmentNotificationHistory history = new ManualAppointmentNotificationHistory(  );
            history.setIdHistory( resourceHistory.getId(  ) );
            history.setIdAppointment( resourceHistory.getIdResource(  ) );
            history.setEmailTo( appointment.getEmail(  ) );
            history.setEmailSubject( strSubject );
            history.setEmailMessage( strContent );
            ManualAppointmentNotificationHistoryHome.create( history );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_TASK_TITLE, locale );
    }
}
