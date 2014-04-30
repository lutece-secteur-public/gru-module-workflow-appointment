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
import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskNotifyAdminAppointmentConfig;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskNotifyAppointmentConfig;
import fr.paris.lutece.plugins.workflow.modules.appointment.web.ExecuteWorkflowAction;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.util.AppPathService;

import org.apache.commons.lang.StringUtils;

import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;


/**
 * Workflow task to notify an admin user associated to an appointment. <br />
 * The admin user is the admin user specified in the configuration of the task,
 * or the admin user associated with the appointment if no admin user is
 * associated to the configuration.
 */
public class TaskNotifyAdminAppointment extends AbstractTaskNotifyAppointment<TaskNotifyAdminAppointmentConfig>
{
    /**
     * Name of the bean of the config service of this task
     */
    public static final String CONFIG_SERVICE_BEAN_NAME = "workflow-appointment.taskNotifyAdminAppointmentConfigService";

    // TEMPLATES
    private static final String MARK_URL_CANCEL = "url_cancel";
    private static final String MARK_URL_VALIDATE = "url_validate";

    // SERVICES
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    @Named( CONFIG_SERVICE_BEAN_NAME )
    private ITaskConfigService _taskNotifyAppointmentAdminConfigService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        TaskNotifyAdminAppointmentConfig config = _taskNotifyAppointmentAdminConfigService.findByPrimaryKey( this
                .getId( ) );

        if ( config != null )
        {
            Appointment appointment = AppointmentHome.findByPrimaryKey( resourceHistory.getIdResource( ) );
            AdminUser adminUser = AdminUserHome.findByPrimaryKey( ( config.getIdAdminUser( ) > 0 ) ? config
                    .getIdAdminUser( ) : appointment.getIdAdminUser( ) );

            if ( adminUser != null )
            {
                this.sendEmail( appointment, resourceHistory, request, locale, config, adminUser.getEmail( ) );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig( )
    {
        _taskNotifyAppointmentAdminConfigService.remove( this.getId( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale locale )
    {
        TaskNotifyAppointmentConfig config = _taskNotifyAppointmentAdminConfigService.findByPrimaryKey( this.getId( ) );

        if ( config != null )
        {
            return config.getSubject( );
        }

        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> fillModel( HttpServletRequest request,
            TaskNotifyAdminAppointmentConfig notifyAppointmentDTO, Appointment appointment,
            AppointmentSlot appointmentSlot, Locale locale )
    {
        Map<String, Object> model = super.fillModel( request, notifyAppointmentDTO, appointment, appointmentSlot,
                locale );
        model.put( MARK_URL_CANCEL, ExecuteWorkflowAction.getExecuteWorkflowActionUrl(
                AppPathService.getBaseUrl( request ), notifyAppointmentDTO.getIdActionCancel( ),
                notifyAppointmentDTO.getIdAdminUser( ), appointment.getIdAppointment( ) ) );
        model.put( MARK_URL_VALIDATE, ExecuteWorkflowAction.getExecuteWorkflowActionUrl(
                AppPathService.getBaseUrl( request ), notifyAppointmentDTO.getIdActionValidate( ),
                notifyAppointmentDTO.getIdAdminUser( ), appointment.getIdAppointment( ) ) );

        return model;
    }
}
