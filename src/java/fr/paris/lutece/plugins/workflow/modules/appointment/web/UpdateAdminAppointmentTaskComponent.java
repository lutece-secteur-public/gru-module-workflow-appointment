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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.UpdateAdminAppointmentHistory;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.UpdateAdminAppointmentHistoryHome;
import fr.paris.lutece.plugins.workflow.web.task.NoConfigTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
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

    // MARKS
    private static final String MARK_LIST_ADMIN_USERS = "list_admin_users";

    // PARAMETERS
    private static final String PARAMETER_ID_ADMIN_USER = "id_admin_user";
    // CONSTANTS
    private static final String CONSTANT_SPACE = " ";

    public static final String FORMAT_DATE = "dd/MM/yyyy";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<>( );

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
        Appointment appointment = AppointmentService.findAppointmentById( nIdResource );
        appointment.setIdAdminUser( nIdAdminUser );
        AppointmentService.updateAppointment( appointment );
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
}
