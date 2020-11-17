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
package fr.paris.lutece.plugins.workflow.modules.appointment.service;

import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.UserService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.EmailDTO;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.ManualAppointmentNotificationHistory;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.ManualAppointmentNotificationHistoryHome;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.NotifyAppointmentDTO;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;

/**
 * Workflow task to manually notify a user of an appointment
 */
public class TaskManualAppointmentNotification extends AbstractTaskNotifyAppointment<NotifyAppointmentDTO>
{
    // Messages
    private static final String MESSAGE_TASK_TITLE = "module.workflow.appointment.taskManualAppointmentNotification.title";

    // Parameters
    private static final String PARAMETER_SENDER_NAME = "sender_name";
    private static final String PARAMETER_SENDER_EMAIL = "sender_email";
    private static final String PARAMETER_CC = "receipient_cc";
    private static final String PARAMETER_BCC = "receipient_bcc";
    private static final String PARAMETER_MESSAGE = "message";
    private static final String PARAMETER_SUBJECT = "subject";
    private static final String PARAMETER_SEND_ICAL_NOTIF = "send_ical_notif";
    private static final String PARAMETER_LOCATION = "location";
    private static final String PARAMETER_CREATE_NOTIF = "create_notif";

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
        AppointmentDTO appointment = AppointmentService.buildAppointmentDTOFromIdAppointment(resourceHistory.getIdResource( ) );

        Map<String, String [ ]> parameters = request.getParameterMap( );
        String strCancelMotif = null;
        for ( Map.Entry<String, String [ ]> entry : parameters.entrySet( ) )
        {
            if ( entry.getKey( ).startsWith( "comment_value_" ) )
            {
                String [ ] tabAllParamsStartedWithCommentValue = entry.getValue( );
                strCancelMotif = tabAllParamsStartedWithCommentValue [0];
                break;
            }
        }

        String strSenderName = request.getParameter( PARAMETER_SENDER_NAME );
        String strSenderEmail = request.getParameter( PARAMETER_SENDER_EMAIL );
        String strCc = request.getParameter( PARAMETER_CC );
        String strBcc = request.getParameter( PARAMETER_BCC );
        String strMessage = request.getParameter( PARAMETER_MESSAGE );
        String strSubject = request.getParameter( PARAMETER_SUBJECT );
        boolean bSendICalNotif = Boolean.parseBoolean( request.getParameter( PARAMETER_SEND_ICAL_NOTIF ) );

        if ( StringUtils.isBlank( strSenderName ) )
        {
            strSenderName = MailService.getNoReplyEmail( );
        }

        NotifyAppointmentDTO notifyAppointmentDTO = new NotifyAppointmentDTO( );
        notifyAppointmentDTO.setMessage( strMessage );
        notifyAppointmentDTO.setRecipientsCc( strCc );
        notifyAppointmentDTO.setRecipientsBcc( strBcc );
        notifyAppointmentDTO.setSubject( strSubject );
        notifyAppointmentDTO.setSenderName( strSenderName );
        notifyAppointmentDTO.setCancelMotif( strCancelMotif );
        // We do not check the email nor the sender name since it's done by the
        // sendEmail( ... ) method.
        notifyAppointmentDTO.setSenderEmail( strSenderEmail );
        notifyAppointmentDTO.setSendICalNotif( bSendICalNotif );

        if ( bSendICalNotif )
        {
            String strLocation = request.getParameter( PARAMETER_LOCATION );
            notifyAppointmentDTO.setLocation( strLocation );
            notifyAppointmentDTO.setCreateNotif( Boolean.parseBoolean( request.getParameter( PARAMETER_CREATE_NOTIF ) ) );
        }

        String strEmail = null;
        User user = UserService.findUserById( appointment.getIdUser( ) );
        if ( notifyAppointmentDTO.getIsSms( ) )
        {
            strEmail = getEmailForSmsFromAppointment( appointment );
        }
        else
        {
            strEmail = user.getEmail( );
        }

        if ( StringUtils.isNotBlank( strEmail ) )
        {
            EmailDTO emailDTO = this.sendEmail( appointment, resourceHistory, request, locale, notifyAppointmentDTO, strEmail );

            if ( emailDTO != null )
            {
                ManualAppointmentNotificationHistory history = new ManualAppointmentNotificationHistory( );
                history.setIdHistory( resourceHistory.getId( ) );
                history.setIdAppointment( resourceHistory.getIdResource( ) );
                history.setEmailTo( user.getEmail( ) );
                history.setEmailSubject( emailDTO.getSubject( ) );
                history.setEmailMessage( emailDTO.getContent( ) );
                ManualAppointmentNotificationHistoryHome.create( history );
            }
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
