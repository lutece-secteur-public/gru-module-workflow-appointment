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
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.EmailDTO;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.NotifyAppointmentDTO;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.string.StringUtil;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * Abstract task to notify a user of an appointment.
 * @param <T> The type of the DTO to use to send the email
 */
public abstract class AbstractTaskNotifyAppointment<T extends NotifyAppointmentDTO> extends SimpleTask
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_NOTIFY_MAIL = "admin/plugins/workflow/modules/appointment/task_notify_appointment_mail.html";
    private static final String TEMPLATE_TASK_NOTIFY_APPOINTMENT_RECAP = "admin/plugins/workflow/modules/appointment/task_notify_appointment_recap.html";

    // MARKS
    private static final String MARK_MESSAGE = "message";
    private static final String MARK_LIST_RESPONSE = "listResponse";
    private static final String MARK_FIRSTNAME = "firstName";
    private static final String MARK_LASTNAME = "lastName";
    private static final String MARK_EMAIL = "email";
    private static final String MARK_REFERENCE = "reference";
    private static final String MARK_DATE_APPOINTMENT = "date_appointment";
    private static final String MARK_TIME_APPOINTMENT = "time_appointment";
    private static final String MARK_RECAP = "recap";
    private ICalService _iCalService;

    /**
     * Send an email to a user
     * @param appointment The appointment
     * @param resourceHistory The resource history
     * @param request The request
     * @param locale The locale
     * @param notifyAppointmentDTO The DTO with data of the email
     * @param strEmail The address to send the email to
     * @return The content sent, or null if no email was sent
     */
    @SuppressWarnings( "deprecation" )
    public EmailDTO sendEmail( Appointment appointment, ResourceHistory resourceHistory, HttpServletRequest request,
        Locale locale, T notifyAppointmentDTO, String strEmail )
    {
        if ( ( notifyAppointmentDTO != null ) && ( resourceHistory != null ) &&
                Appointment.APPOINTMENT_RESOURCE_TYPE.equals( resourceHistory.getResourceType(  ) ) &&
                ( appointment != null ) )
        {
            AppointmentSlot appointmentSlot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );

            if ( appointmentSlot != null )
            {
                if ( StringUtils.isEmpty( notifyAppointmentDTO.getSenderEmail(  ) ) ||
                        !StringUtil.checkEmail( notifyAppointmentDTO.getSenderEmail(  ) ) )
                {
                    notifyAppointmentDTO.setSenderEmail( MailService.getNoReplyEmail(  ) );
                }

                if ( StringUtils.isBlank( notifyAppointmentDTO.getSenderName(  ) ) )
                {
                    notifyAppointmentDTO.setSenderName( notifyAppointmentDTO.getSenderEmail(  ) );
                }

                Map<String, Object> model = fillModel( request, notifyAppointmentDTO, appointment, appointmentSlot,
                        locale );

                String strSubject = AppTemplateService.getTemplateFromStringFtl( notifyAppointmentDTO.getSubject(  ),
                        locale, model ).getHtml(  );

                boolean bHasRecipients = ( StringUtils.isNotBlank( notifyAppointmentDTO.getRecipientsBcc(  ) ) ||
                    StringUtils.isNotBlank( notifyAppointmentDTO.getRecipientsCc(  ) ) );

                String strContent = AppTemplateService.getTemplateFromStringFtl( AppTemplateService.getTemplate( 
                            TEMPLATE_TASK_NOTIFY_MAIL, locale, model ).getHtml(  ), locale, model ).getHtml(  );

                if ( notifyAppointmentDTO.getSendICalNotif(  ) )
                {
                    getICalService(  )
                        .sendAppointment( strEmail, notifyAppointmentDTO.getRecipientsCc(  ), strSubject, strContent,
                        notifyAppointmentDTO.getLocation(  ), notifyAppointmentDTO.getSenderName(  ),
                        notifyAppointmentDTO.getSenderEmail(  ), appointment, notifyAppointmentDTO.getCreateNotif(  ) );
                }
                else
                {
                    if ( bHasRecipients )
                    {
                        MailService.sendMailHtml( strEmail, notifyAppointmentDTO.getRecipientsCc(  ),
                            notifyAppointmentDTO.getRecipientsBcc(  ), notifyAppointmentDTO.getSenderName(  ),
                            notifyAppointmentDTO.getSenderEmail(  ), strSubject, strContent );
                    }
                    else
                    {
                        MailService.sendMailHtml( strEmail, notifyAppointmentDTO.getSenderName(  ),
                            notifyAppointmentDTO.getSenderEmail(  ), strSubject, strContent );
                    }
                }

                return new EmailDTO( strSubject, strContent );
            }
        }

        return null;
    }

    /**
     * Get a model to generate email content for a given appointment and a given
     * task.
     * @param request The request
     * @param notifyAppointmentDTO The configuration of the task.
     * @param appointment The appointment to get data from
     * @param appointmentSlot The slot associated with the appointment
     * @param locale The locale
     * @return The model with data
     */
    public Map<String, Object> fillModel( HttpServletRequest request, T notifyAppointmentDTO, Appointment appointment,
        AppointmentSlot appointmentSlot, Locale locale )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_FIRSTNAME, appointment.getFirstName(  ) );
        model.put( MARK_LASTNAME, appointment.getLastName(  ) );
        model.put( MARK_EMAIL, appointment.getEmail(  ) );
        model.put( MARK_REFERENCE, AppointmentService.getService(  ).computeRefAppointment( appointment ) );
        model.put( MARK_DATE_APPOINTMENT, appointment.getDateAppointment(  ) );

        String strStartingTime = AppointmentService.getService(  )
                                                   .getFormatedStringTime( appointmentSlot.getStartingHour(  ),
                appointmentSlot.getStartingMinute(  ) );
        model.put( MARK_TIME_APPOINTMENT, strStartingTime );
        model.put( MARK_MESSAGE, notifyAppointmentDTO.getMessage(  ) );

        List<Response> listResponse = AppointmentHome.findListResponse( appointment.getIdAppointment(  ) );
        model.put( MARK_LIST_RESPONSE, listResponse );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_NOTIFY_APPOINTMENT_RECAP, locale, model );
        model.put( MARK_RECAP, template.getHtml(  ) );

        return model;
    }

    private ICalService getICalService(  )
    {
        if ( _iCalService == null )
        {
            _iCalService = ICalService.getService(  );
        }

        return _iCalService;
    }
}
