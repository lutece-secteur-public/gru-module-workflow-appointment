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
package fr.paris.lutece.plugins.workflow.modules.appointment.service;

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.appointment.business.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.ManualAppointmentNotificationHistory;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.ManualAppointmentNotificationHistoryHome;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.template.AppTemplateService;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * TaskNotifyAppointment
 */
public class TaskManualAppointmentNotification extends SimpleTask
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

        if ( resourceHistory == null
                || !Appointment.APPOINTMENT_RESOURCE_TYPE.equals( resourceHistory.getResourceType( ) ) )
        {
            return;
        }

        // Record
        Appointment appointment = AppointmentHome.findByPrimaryKey( resourceHistory.getIdResource( ) );

        if ( appointment != null )
        {
            AppointmentSlot appointmentSlot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot( ) );

            String strSenderName = request.getParameter( PARAMETER_SENDER_NAME );
            String strCc = request.getParameter( PARAMETER_CC );
            String strBcc = request.getParameter( PARAMETER_BCC );
            String strMessage = request.getParameter( PARAMETER_MESSAGE );
            String strSubject = request.getParameter( PARAMETER_SUBJECT );

            if ( StringUtils.isBlank( strSenderName ) )
            {
                strSenderName = MailService.getNoReplyEmail( );
            }

            if ( appointmentSlot != null && StringUtils.isNotBlank( strSubject ) && StringUtils.isNotBlank( strMessage ) )
            {
                Map<String, Object> model = fillModel( strMessage, appointment, appointmentSlot );

                strSubject = AppTemplateService.getTemplateFromStringFtl( strSubject, locale, model ).getHtml( );

                boolean bHasRecipients = ( StringUtils.isNotBlank( strBcc ) || StringUtils.isNotBlank( strCc ) );

                String strContent = AppTemplateService.getTemplateFromStringFtl(
                        AppTemplateService.getTemplate( TEMPLATE_TASK_NOTIFY_MAIL, locale, model ).getHtml( ), locale,
                        model ).getHtml( );

                if ( bHasRecipients )
                {
                    MailService.sendMailHtml( appointment.getEmail( ), strCc, strBcc, strSenderName,
                            MailService.getNoReplyEmail( ), strSubject, strContent );
                }
                else
                {
                    MailService.sendMailHtml( appointment.getEmail( ), strSenderName, MailService.getNoReplyEmail( ),
                            strSubject, strContent );
                }
                ManualAppointmentNotificationHistory history = new ManualAppointmentNotificationHistory( );
                history.setIdHistory( resourceHistory.getId( ) );
                history.setIdAppointment( resourceHistory.getIdResource( ) );
                history.setEmailTo( appointment.getEmail( ) );
                history.setEmailSubject( strSubject );
                history.setEmailMessage( strContent );
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

    /**
     * Get a model to generate email content for a given appointment and a given
     * task.
     * @param config The configuration of the task.
     * @param appointment The appointment to get data from
     * @param appointmentSlot The slot associated with the appointment
     * @return The model with data
     */
    private Map<String, Object> fillModel( String strMessage, Appointment appointment, AppointmentSlot appointmentSlot )
    {
        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( MARK_FIRSTNAME, appointment.getFirstName( ) );
        model.put( MARK_LASTNAME, appointment.getLastName( ) );
        model.put( MARK_EMAIL, appointment.getEmail( ) );
        model.put( MARK_REFERENCE, AppointmentService.getService( ).computeRefAppointment( appointment ) );
        model.put( MARK_DATE_APPOINTMENT, appointment.getDateAppointment( ) );

        String strStartingTime = AppointmentService.getService( ).getFormatedStringTime(
                appointmentSlot.getStartingHour( ), appointmentSlot.getEndingHour( ) );
        model.put( MARK_TIME_APPOINTMENT, strStartingTime );
        model.put( MARK_MESSAGE, strMessage );

        List<Response> listResponse = AppointmentHome.findListResponse( appointment.getIdAppointment( ) );
        model.put( MARK_LIST_RESPONSE, listResponse );

        return model;
    }
}
