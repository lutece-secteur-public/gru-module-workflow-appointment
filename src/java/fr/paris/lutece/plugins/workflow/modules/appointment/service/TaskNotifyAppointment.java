/*
 * Copyright (c) 2002-2021, City of Paris
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
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskNotifyAppointmentConfig;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * Workflow task to notify a user of an appointment
 */
public class TaskNotifyAppointment extends AbstractTaskNotifyAppointment<TaskNotifyAppointmentConfig>
{
    /**
     * Name of the bean of the config service of this task
     */
    public static final String CONFIG_SERVICE_BEAN_NAME = "workflow-appointment.taskNotifyAppointmentConfigService";

    // TEMPLATES
    private static final String MARK_URL_CANCEL = "url_cancel";
    private static final String MARK_MOTIF_CANCEL = "comment_value_";

    private static final String PROPERTY_MAIL_LANG_SERVER = "workflow-appointment.server.mail.lang";

    // PARAMETERS
    private static final String ERROR_CANCEL_APPOINTMENT_MSG = "<p>Nous sommes désolés, l'annulation de votre rendez-vous ne peut être prise en compte. Vous pouvez contacter le service aux coordonnées fournies dans l'e-mail de confirmation initial</p>";
    // SERVICES
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    @Named( CONFIG_SERVICE_BEAN_NAME )
    private ITaskConfigService _taskNotifyAppointmentConfigService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String sServerMailLang = AppPropertiesService.getProperty( PROPERTY_MAIL_LANG_SERVER );

        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        TaskNotifyAppointmentConfig config = _taskNotifyAppointmentConfigService.findByPrimaryKey( this.getId( ) );
        if ( config == null )
        {
            AppLogService.error( "Error: No config for Task " + this.getId( ) );
            return;
        }
        AppointmentDTO appointment = AppointmentService.buildAppointmentDTOFromIdAppointment( resourceHistory.getIdResource( ) );
        User user = appointment.getUser( );
        if ( request != null )
        {
            Map<String, String [ ]> parameters = request.getParameterMap( );
            String strCancelMotif = null;
            for ( Map.Entry<String, String [ ]> entry : parameters.entrySet( ) )
            {
                if ( entry.getKey( ).startsWith( MARK_MOTIF_CANCEL ) )
                {
                    String [ ] tabAllParamsStartedWithCommentValue = entry.getValue( );
                    strCancelMotif = tabAllParamsStartedWithCommentValue [0];
                    config.setCancelMotif( strCancelMotif );
                    break;
                }
            }
        }

        String strEmail;

        if ( config.getIsSms( ) )
        {
            strEmail = getEmailForSmsFromAppointment( appointment );
        }
        else
        {
            strEmail = user.getEmail( );
        }

        if ( StringUtils.isBlank( strEmail ) )
        {
            return;
        }

        Locale lEmailLocale = locale;
        if ( !sServerMailLang.isEmpty( ) )
        {
            lEmailLocale = new Locale( sServerMailLang.split( "_" ) [0], sServerMailLang.split( "_" ) [1] );
        }

        if ( appointment.getIsCancelled( ) && StringUtils.isNotEmpty( config.getCancelMotif( ) ) )
        {
            config.setMessage( ERROR_CANCEL_APPOINTMENT_MSG );
        }

        if ( this.sendEmail( appointment, resourceHistory, request, lEmailLocale, config, strEmail ) != null && config.getIdActionCancel( ) > 0
                && config.getIdActionCancel( ) != appointment.getIdActionCancelled( ) )
        {
            appointment.setIdActionCancelled( config.getIdActionCancel( ) );
            AppointmentService.updateAppointment( appointment );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig( )
    {
        _taskNotifyAppointmentConfigService.remove( this.getId( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale locale )
    {
        TaskNotifyAppointmentConfig config = _taskNotifyAppointmentConfigService.findByPrimaryKey( this.getId( ) );

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
    public Map<String, Object> fillModel( HttpServletRequest request, TaskNotifyAppointmentConfig notifyAppointmentDTO, AppointmentDTO appointment,
            Locale locale )
    {
        Map<String, Object> model = super.fillModel( request, notifyAppointmentDTO, appointment, locale );
        model.put( MARK_URL_CANCEL, AppointmentApp.getCancelAppointmentUrl( request, appointment ) );

        return model;
    }
}
