/*
 * Copyright (c) 2002-2020, City of Paris
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentResponseService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.AppointmentUtilities;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * TaskUpddateAppointment
 */
public class TaskUpdateAppointment extends SimpleTask
{

    private static final String PARAMETER_EMAIL = "email";
    private static final String PARAMETER_EMAIL_CONFIRMATION = "emailConfirm";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_LAST_NAME = "lastname";
    private static final String PARAMETER_FIRST_NAME = "firstname";

    // MESSAGES
    private static final String MESSAGE_UPDATE_APPOINTMENT = "module.workflow.appointment.task_update_appointment_config.title";

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
        Appointment appointment = AppointmentService.findAppointmentById( resourceHistory.getIdResource( ) );
        AppointmentDTO appointmentDTO = AppointmentService.buildAppointmentDTOFromIdAppointment( appointment.getIdAppointment( ) );

        String strEmail = request.getParameter( PARAMETER_EMAIL );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        AppointmentFormDTO form = FormService.buildAppointmentForm( Integer.parseInt( strIdForm ), 0, 0 );

        List<GenericAttributeError> listFormErrors = new ArrayList<>( );
        AppointmentUtilities.checkEmail( strEmail, request.getParameter( PARAMETER_EMAIL_CONFIRMATION ), form, locale, listFormErrors );
        AppointmentUtilities.fillAppointmentDTO( appointmentDTO, appointmentDTO.getNbBookedSeats( ), strEmail, request.getParameter( PARAMETER_FIRST_NAME ),
                request.getParameter( PARAMETER_LAST_NAME ) );

        AppointmentUtilities.validateFormAndEntries( appointmentDTO, request, listFormErrors, AdminUserService.getAdminUser( request ) != null );
        AppointmentUtilities.fillInListResponseWithMapResponse( appointmentDTO );

        if ( CollectionUtils.isEmpty( listFormErrors ) )
        {
            User user = appointmentDTO.getUser( );
            user.setEmail( appointmentDTO.getEmail( ) );
            user.setFirstName( appointmentDTO.getFirstName( ) );
            user.setLastName( appointmentDTO.getLastName( ) );
            user.setPhoneNumber( appointmentDTO.getPhoneNumber( ) );
            UserHome.update( user );
            List<Integer> entryIdToDelete = appointmentDTO.getListResponse( ).stream( ).map( Response::getEntry ).map( Entry::getIdEntry )
                    .collect( Collectors.toList( ) );

            AppointmentResponseService.removeResponsesByIdAppointmentAndListEntryId( appointment.getIdAppointment( ), entryIdToDelete );
            if ( CollectionUtils.isNotEmpty( appointmentDTO.getListResponse( ) ) )
            {
                for ( Response response : appointmentDTO.getListResponse( ) )
                {
                    ResponseHome.create( response );
                    AppointmentResponseService.insertAppointmentResponse( appointment.getIdAppointment( ), response.getIdResponse( ) );
                }
            }

            AppointmentHome.update( appointment );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_UPDATE_APPOINTMENT, locale );
    }
}
