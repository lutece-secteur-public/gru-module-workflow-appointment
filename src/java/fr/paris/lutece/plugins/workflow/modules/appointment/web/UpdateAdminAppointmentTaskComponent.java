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
package fr.paris.lutece.plugins.workflow.modules.appointment.web;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.localization.Localization;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.LocalizationService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.UserService;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.UpdateAdminAppointmentHistory;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.UpdateAdminAppointmentHistoryHome;
import fr.paris.lutece.plugins.workflow.modules.appointment.service.ICalService;
import fr.paris.lutece.plugins.workflow.web.task.NoConfigTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * UpdateAdminAppointmentTaskComponent
 */
public class UpdateAdminAppointmentTaskComponent extends NoConfigTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_UPDATE_ADMIN_APPOINTMENT = "admin/plugins/workflow/modules/appointment/task_update_admin_appointment_config.html";

    // MESSAGES
    private static final String MESSAGE_MANDATORY_FIELD = "portal.util.message.mandatoryFields";
    private static final String MESSAGE_ADMIN_USER_ASSOCIATED_TO_APPOINTMENT = "module.workflow.appointment.task_update_admin_appointment_config.adminUserAssociatedToAppointment";

    private static final String DATE_OF_APPOINTMENT = "module.workflow.appointment.task_notify_appointment_config.label_date_appointment";
    private static final String TIME_OF_APPOINTMENT = "module.workflow.appointment.task_notify_appointment_config.label_time_appointment";
    private static final String LAST_NAME_OF_USER = "module.workflow.appointment.task_notify_appointment_config.label_lastname";
    private static final String FIRST_NAME_OF_USER = "module.workflow.appointment.task_notify_appointment_config.label_firstname";
    private static final String APPOINTMENT_WITH = "module.workflow.appointment.task_notify_appointment_config.appointment_with";
    // MARKS
    private static final String MARK_LIST_ADMIN_USERS = "list_admin_users";

    // PARAMETERS
    private static final String PARAMETER_ID_ADMIN_USER = "id_admin_user";
    private static final String PARAMETER_IS_CAL_NOTIF = "is_cal_notif";
    // CONSTANTS
    private static final String CONSTANT_SPACE = " ";
    private static final String COLON = ":";

    public static final String FORMAT_DATE = "dd/MM/yyyy";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<String, Object>( );

        Collection<AdminUser> listAdminUser = AdminUserHome.findUserList( );
        ReferenceList refListAdmins = new ReferenceList( );

        for ( AdminUser adminUser : listAdminUser )
        {
            refListAdmins.addItem( adminUser.getUserId( ), adminUser.getFirstName( ) + CONSTANT_SPACE + adminUser.getLastName( ) );
        }

        model.put( MARK_LIST_ADMIN_USERS, refListAdmins );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_UPDATE_ADMIN_APPOINTMENT, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        String strIdAdminUser = request.getParameter( PARAMETER_ID_ADMIN_USER );

        if ( StringUtils.isBlank( strIdAdminUser ) || !StringUtils.isNumeric( strIdAdminUser ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, AdminMessage.TYPE_STOP );
        }

        int nIdAdminUser = Integer.parseInt( strIdAdminUser );
        AdminUser adminUser = AdminUserHome.findByPrimaryKey( nIdAdminUser );

        if ( adminUser == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, AdminMessage.TYPE_STOP );
        }

        String strIsCalNotif = request.getParameter( PARAMETER_IS_CAL_NOTIF );
        if ( Boolean.parseBoolean( strIsCalNotif ) )
        {
            sendICal( nIdResource, adminUser, locale );
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        List<UpdateAdminAppointmentHistory> listHistory = UpdateAdminAppointmentHistoryHome.findByIdHistory( nIdHistory );
        StringBuilder sbHistory = new StringBuilder( );

        for ( UpdateAdminAppointmentHistory history : listHistory )
        {
            AdminUser adminUser = AdminUserHome.findByPrimaryKey( history.getIdAdminUser( ) );

            if ( adminUser != null )
            {
                Object [ ] args = {
                    adminUser.getFirstName( ) + CONSTANT_SPACE + adminUser.getLastName( )
                };
                sbHistory.append( I18nService.getLocalizedString( MESSAGE_ADMIN_USER_ASSOCIATED_TO_APPOINTMENT, args, locale ) );
            }
        }

        return sbHistory.toString( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    /**
     * Send an email with an event attached to
     * 
     * @param nIdResource
     *            the id of the appointment
     * @param adminUser
     *            the associated user
     * @param locale
     *            the locale
     */
    private void sendICal( int nIdResource, AdminUser adminUser, Locale locale )
    {
        Appointment appointment = AppointmentService.findAppointmentById( nIdResource );
        User user = UserService.findUserById( appointment.getIdUser( ) );
        Slot slot = SlotService.findSlotById( appointment.getIdSlot( ) );
        Form form = FormService.findFormLightByPrimaryKey( slot.getIdForm( ) );
        Localization localization = LocalizationService.findLocalizationWithFormId( form.getIdForm( ) );
        String location = StringUtils.EMPTY;
        if ( localization != null && StringUtils.isNotEmpty( localization.getAddress( ) ) )
        {
            location = localization.getAddress( );
        }
        String content = new StringJoiner( StringUtils.EMPTY ).add( I18nService.getLocalizedString( DATE_OF_APPOINTMENT, locale ) ).add( CONSTANT_SPACE )
                .add( COLON ).add( CONSTANT_SPACE ).add( DateTimeFormatter.ofPattern( FORMAT_DATE ).format( slot.getDate( ) ) ).add( StringUtils.LF )
                .add( I18nService.getLocalizedString( TIME_OF_APPOINTMENT, locale ) ).add( CONSTANT_SPACE ).add( COLON ).add( CONSTANT_SPACE )
                .add( slot.getStartingTime( ).toString( ) ).add( StringUtils.LF ).add( I18nService.getLocalizedString( FIRST_NAME_OF_USER, locale ) )
                .add( CONSTANT_SPACE ).add( COLON ).add( CONSTANT_SPACE ).add( user.getFirstName( ) ).add( StringUtils.LF )
                .add( I18nService.getLocalizedString( LAST_NAME_OF_USER, locale ) ).add( CONSTANT_SPACE ).add( COLON ).add( CONSTANT_SPACE )
                .add( user.getLastName( ) ).add( StringUtils.LF ).toString( );
        String subject = new StringJoiner( StringUtils.EMPTY ).add( APPOINTMENT_WITH ).add( CONSTANT_SPACE ).add( COLON ).add( CONSTANT_SPACE )
                .add( user.getFirstName( ) ).add( CONSTANT_SPACE ).add( user.getLastName( ) ).toString( );
        // Send an email with a cal event to this admin user
        ICalService.getService( ).sendAppointment( adminUser.getEmail( ), StringUtils.EMPTY, subject, content, location, MailService.getNoReplyEmail( ),
                MailService.getNoReplyEmail( ), appointment, Boolean.TRUE );
    }
}
