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

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.crmclient.service.ICRMClientService;
import fr.paris.lutece.plugins.crmclient.util.CRMException;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskNotifyCrmConfig;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.util.AppLogService;

public class TaskNotifyAppointementCrm extends SimpleTask
{

    /**
     * Name of the bean of the config service of this task
     */
    public static final String CONFIG_SERVICE_BEAN_NAME = "workflow-appointment.taskNotifyCrmConfigService";

    private static final String MARK_FIRSTNAME = "${firstName}";
    private static final String MARK_LASTNAME = "${lastName}";
    private static final String MARK_REFERENCE = "${reference}";
    private static final String MARK_DATE_APPOINTMENT = "${date_appointment}";

    // SERVICES
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    private ICRMClientService _crmClientService;

    @Inject
    @Named( CONFIG_SERVICE_BEAN_NAME )
    private ITaskConfigService _taskNotifyAppointmentCrmConfigService;

    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {

        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        TaskNotifyCrmConfig config = _taskNotifyAppointmentCrmConfigService.findByPrimaryKey( this.getId( ) );
        AppointmentDTO appointment = AppointmentService.buildAppointmentDTOFromIdAppointment( resourceHistory.getIdResource( ) );
        User user = appointment.getUser( );
        String strIdDemand = null;

        if ( config != null )
        {

            try
            {
                strIdDemand = _crmClientService.sendCreateDemandByUserGuid( config.getDemandeType( ), Integer.toString( appointment.getIdUser( ) ),
                        config.getIdStatusCRM( ), config.getStatusText( ), config.getData( ) );
            }
            catch( CRMException e )
            {
                AppLogService.error( e );
            }
        }
        if ( strIdDemand != null )
        {

            String mesg = getMessageAppointment( config.getMessage( ), appointment, user );
            _crmClientService.notify( strIdDemand, config.getObject( ), mesg, config.getSender( ) );

        }

    }

    @Override
    public String getTitle( Locale locale )
    {

        TaskNotifyCrmConfig config = _taskNotifyAppointmentCrmConfigService.findByPrimaryKey( this.getId( ) );

        if ( config != null )
        {
            return config.getDemandeType( );
        }

        return StringUtils.EMPTY;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig( )
    {
        _taskNotifyAppointmentCrmConfigService.remove( this.getId( ) );
    }

    private String getMessageAppointment( String msg, AppointmentDTO appointment, User user )
    {

        String message = ( msg.replace( MARK_FIRSTNAME, user.getFirstName( ) ) ).replace( MARK_LASTNAME, user.getLastName( ) );
        String m = message.replace( MARK_REFERENCE, appointment.getReference( ) );
        return m.replace( MARK_DATE_APPOINTMENT, appointment.getDateOfTheAppointment( ) );
    }

}
