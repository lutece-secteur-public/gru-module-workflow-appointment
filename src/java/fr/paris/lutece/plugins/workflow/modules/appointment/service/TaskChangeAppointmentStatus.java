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
package fr.paris.lutece.plugins.workflow.modules.appointment.service;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskChangeAppointmentStatusConfig;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * Workflow task to change the status of an appointment
 */
public class TaskChangeAppointmentStatus extends SimpleTask
{
    /**
     * Name of the bean of the config service of this task
     */
    public static final String CONFIG_SERVICE_BEAN_NAME = "workflow-appointment.taskChangeAppointmentStatusConfigService";

    // Messages
    private static final String MESSAGE_ACTIVATE_APPOINTMENT = "module.workflow.appointment.task_change_appointment_status.labelEnableAppointment";
    private static final String MESSAGE_DEACTIVATE_APPOINTMENT = "module.workflow.appointment.task_change_appointment_status.labelDisableAppointment";

    // SERVICES
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    @Named( CONFIG_SERVICE_BEAN_NAME )
    private ITaskConfigService _taskChangeAppointmentStatusConfigService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        TaskChangeAppointmentStatusConfig config = _taskChangeAppointmentStatusConfigService.findByPrimaryKey( this.getId( ) );

        if ( ( config != null ) && ( resourceHistory != null ) && Appointment.APPOINTMENT_RESOURCE_TYPE.equals( resourceHistory.getResourceType( ) ) )
        {
            // We get the appointment to update
            Appointment appointment = AppointmentService.findAppointmentById( resourceHistory.getIdResource( ) );
            if ( appointment != null )
            {
                appointment.setIsCancelled( config.getAppointmentStatus( ) == 0 );
                AppointmentService.updateAppointment( appointment );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig( )
    {
        _taskChangeAppointmentStatusConfigService.remove( this.getId( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale locale )
    {
        TaskChangeAppointmentStatusConfig config = _taskChangeAppointmentStatusConfigService.findByPrimaryKey( this.getId( ) );

        if ( config != null )
        {
            return I18nService.getLocalizedString( ( config.getAppointmentStatus( ) == 1 ) ? MESSAGE_ACTIVATE_APPOINTMENT : MESSAGE_DEACTIVATE_APPOINTMENT,
                    locale );
        }

        return StringUtils.EMPTY;
    }
}
