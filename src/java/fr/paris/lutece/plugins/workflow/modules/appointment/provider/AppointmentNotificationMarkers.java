/*
 * Copyright (c) 2002-2024, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.appointment.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.service.AppointmentResponseService;
import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.NotifyAppointmentDTO;
import fr.paris.lutece.plugins.workflowcore.service.provider.InfoMarker;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * Class containing InfoMarker related methods
 */
public class AppointmentNotificationMarkers
{
    /**
     * The appointment
     */
    private AppointmentDTO _appointment;

    /**
     * The Notification Task's configuration
     */
    private NotifyAppointmentDTO _notifyAppointmentDTO;

    /**
     * Constructor
     * 
     * @param appointment
     *            The appointment to get data from
     * @param notifyAppointmentDTO
     *            The configuration of the task
     */
    public AppointmentNotificationMarkers( AppointmentDTO appointment, NotifyAppointmentDTO notifyAppointmentDTO )
    {
        _appointment = appointment;
        _notifyAppointmentDTO = notifyAppointmentDTO;
    }

    /**
     * Get a Collection of all available markers and their description
     * 
     * @param isAdminNotification
     *            Whether the markers provided are used for an admin notification or not
     * @return a Collection of InfoMarker Objects
     */
    public static Collection<InfoMarker> getMarkerDescriptions( boolean isAdminNotification )
    {
        Collection<InfoMarker> collectionNotifyMarkers = new ArrayList<>( );

        // Create the markers available to fill an appointment's notification
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_FIRSTNAME, AppointmentWorkflowConstants.DESCRIPTION_MARK_FIRSTNAME ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_LASTNAME, AppointmentWorkflowConstants.DESCRIPTION_MARK_LASTNAME ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_EMAIL, AppointmentWorkflowConstants.DESCRIPTION_MARK_EMAIL ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_DATE_APPOINTMENT, AppointmentWorkflowConstants.DESCRIPTION_MARK_DATE_APPOINTMENT ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_TIME_APPOINTMENT, AppointmentWorkflowConstants.DESCRIPTION_MARK_TIME_APPOINTMENT ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_END_TIME_APPOINTMENT, AppointmentWorkflowConstants.DESCRIPTION_MARK_END_TIME_APPOINTMENT ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_REFERENCE, AppointmentWorkflowConstants.DESCRIPTION_MARK_REFERENCE ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_URL_CANCEL, AppointmentWorkflowConstants.DESCRIPTION_MARK_URL_CANCEL ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_URL_REPORT, AppointmentWorkflowConstants.DESCRIPTION_MARK_URL_REPORT ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_CANCEL_MOTIF, AppointmentWorkflowConstants.DESCRIPTION_MARK_CANCEL_MOTIF ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_RECAP, AppointmentWorkflowConstants.DESCRIPTION_MARK_RECAP ) );
        // Add additional markers when the notification is sent to an admin
        if ( isAdminNotification )
        {
            addAdminMarkers( collectionNotifyMarkers );
        }
        return collectionNotifyMarkers;
    }

    /**
     * Get a Collection of all available markers and their description, additionally creates markers for the specified Appointment Form's Entries
     * 
     * @param idAppointmentForm
     *            ID of the Appointment Form used to retrieve its specific Entry fields
     * @param isAdminNotification
     *            Whether the markers provided are used for an admin notification or not
     * @return a Collection of InfoMarker Objects
     */
    public static Collection<InfoMarker> getMarkerDescriptions( int idAppointmentForm, boolean isAdminNotification )
    {
        Collection<InfoMarker> collectionNotifyMarkers = new ArrayList<>( );

        // Create the markers available to fill an appointment's notification
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_FIRSTNAME, AppointmentWorkflowConstants.DESCRIPTION_MARK_FIRSTNAME ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_LASTNAME, AppointmentWorkflowConstants.DESCRIPTION_MARK_LASTNAME ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_EMAIL, AppointmentWorkflowConstants.DESCRIPTION_MARK_EMAIL ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_DATE_APPOINTMENT, AppointmentWorkflowConstants.DESCRIPTION_MARK_DATE_APPOINTMENT ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_TIME_APPOINTMENT, AppointmentWorkflowConstants.DESCRIPTION_MARK_TIME_APPOINTMENT ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_END_TIME_APPOINTMENT, AppointmentWorkflowConstants.DESCRIPTION_MARK_END_TIME_APPOINTMENT ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_REFERENCE, AppointmentWorkflowConstants.DESCRIPTION_MARK_REFERENCE ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_URL_CANCEL, AppointmentWorkflowConstants.DESCRIPTION_MARK_URL_CANCEL ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_URL_REPORT, AppointmentWorkflowConstants.DESCRIPTION_MARK_URL_REPORT ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_CANCEL_MOTIF, AppointmentWorkflowConstants.DESCRIPTION_MARK_CANCEL_MOTIF ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_RECAP, AppointmentWorkflowConstants.DESCRIPTION_MARK_RECAP ) );
        // Add additional markers when the notification is sent to an admin
        if ( isAdminNotification )
        {
            addAdminMarkers( collectionNotifyMarkers );
        }
        // Add Markers for the form's Entries
        addEntriesMarkers( idAppointmentForm, collectionNotifyMarkers );

        return collectionNotifyMarkers;
    }

    /**
     * Get a Collection of the markers and their specific values. This method is used to replace the markers by the actual values of the resource, before the
     * notification is sent
     * 
     * @return a Collection of InfoMarker, made of their names and proper values
     */
    public Collection<InfoMarker> getMarkerValues( )
    {
        Collection<InfoMarker> collectionNotifyMarkers = new ArrayList<>( );

        collectionNotifyMarkers.add( createMarkerValues( AppointmentWorkflowConstants.MARK_FIRSTNAME, _appointment.getFirstName( ) ) );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentWorkflowConstants.MARK_LASTNAME, _appointment.getLastName( ) ) );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentWorkflowConstants.MARK_EMAIL, _appointment.getEmail( ) ) );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentWorkflowConstants.MARK_DATE_APPOINTMENT, _appointment.getDateOfTheAppointment( ) ) );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentWorkflowConstants.MARK_TIME_APPOINTMENT, _appointment.getStartingTime( ).toString( ) ) );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentWorkflowConstants.MARK_END_TIME_APPOINTMENT, _appointment.getEndingTime( ).toString( ) ) );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentWorkflowConstants.MARK_REFERENCE, _appointment.getReference( ) ) );
        // Retrieve the URLs to cancel, report... an appointment
        String strUrlReport = AppointmentApp.getReportAppointmentUrl( _appointment );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentWorkflowConstants.MARK_URL_REPORT, strUrlReport.replaceAll( "&", "&amp;" ) ) );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentWorkflowConstants.MARK_CANCEL_MOTIF, _notifyAppointmentDTO.getCancelMotif( ) ) );
        // Add the Entry Responses' values
        addEntriesResponsesValues( _appointment.getIdAppointment( ), collectionNotifyMarkers );
        // Add the notification's main message body, where the markers will be replaced by their values
        collectionNotifyMarkers.add( createMarkerValues( AppointmentWorkflowConstants.MARK_MESSAGE, _notifyAppointmentDTO.getMessage( ) ) );

        return collectionNotifyMarkers;
    }

    /**
     * Construct an InfoMarker with the given name and description
     * 
     * @param strMarkerName
     *            The marker's name
     * @param strMarkerDescription
     *            The marker's description
     * @return the InfoMarker Object
     */
    private static InfoMarker createMarkerDescriptions( String strMarkerName, String strMarkerDescription )
    {
        InfoMarker infoMarker = new InfoMarker( strMarkerName );

        // Get the description's value depending on the local language
        String localizedDescription = I18nService.getLocalizedString( strMarkerDescription, I18nService.getDefaultLocale( ) );
        // If no local description is found, use the original description value given
        infoMarker.setDescription( StringUtils.isEmpty( localizedDescription ) ? strMarkerDescription : localizedDescription );

        return infoMarker;
    }

    /**
     * Construct an InfoMarker with the given name and value
     * 
     * @param strMarkerName
     *            The marker's name
     * @param strMarkerValue
     *            The marker's value
     * @return the InfoMarker Object
     */
    private static InfoMarker createMarkerValues( String strMarkerName, String strMarkerValue )
    {
        InfoMarker infoMarker = new InfoMarker( strMarkerName );
        infoMarker.setValue( strMarkerValue );

        return infoMarker;
    }

    /**
     * Add an appointment's responses summary (recap) to a Collection of markers
     * 
     * @param collectionNotifyMarkers
     *            The Collection of markers where the summary data will be added
     * @param appointmentRecap
     *            The content of the summary
     */
    public static void addAppointmentRecap( Collection<InfoMarker> collectionNotifyMarkers, String appointmentRecap )
    {
        collectionNotifyMarkers.add( createMarkerValues( AppointmentWorkflowConstants.MARK_RECAP, appointmentRecap ) );
    }

    /**
     * Add additional markers, specific to admin notifications
     * 
     * @param collectionNotifyMarkers
     */
    private static void addAdminMarkers( Collection<InfoMarker> collectionNotifyMarkers )
    {
        collectionNotifyMarkers
                .add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_URL_VALIDATE, AppointmentWorkflowConstants.DESCRIPTION_MARK_URL_VALIDATE ) );
    }

    /**
     * Add markers for the appointment form's entries and their responses
     * 
     * @param idAppointmentForm
     *            ID of the appointment form used
     * @param collectionInfoMarkers
     *            Collection of InfoMarker objects, where the entry markers will be added
     */
    private static void addEntriesMarkers( int idAppointmentForm, Collection<InfoMarker> collectionInfoMarkers )
    {
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( idAppointmentForm );
        entryFilter.setResourceType( AppointmentFormDTO.RESOURCE_TYPE );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        List<Entry> listEntry = EntryHome.getEntryList( entryFilter );
        for ( Entry entry : listEntry )
        {
            if ( !StringUtils.isEmpty( entry.getTitle( ) ) )
            {
                collectionInfoMarkers
                        .add( createMarkerDescriptions( AppointmentWorkflowConstants.MARK_ENTRY_RESPONSE_PREFIX + entry.getIdEntry( ), entry.getTitle( ) ) );
            }
        }
    }

    /**
     * Retrieve the values of an appointment's responses and add them as new markers in the given InfoMarker Collection
     * 
     * @param idAppointment
     *            ID of the appointment from which the responses will be retrieved
     * @param collectionInfoMarkers
     *            Collection of InfoMarkers that will be completed with the retrieved responses
     */
    private static void addEntriesResponsesValues( int idAppointment, Collection<InfoMarker> collectionInfoMarkers )
    {
        // Get the list of Reponses tied to this appointment
        List<Response> listResponses = AppointmentResponseService.findListResponse( idAppointment );
        // For each Response, create a new InfoMarker with its value
        for ( Response response : listResponses )
        {
            Entry entry = EntryHome.findByPrimaryKey( response.getEntry( ).getIdEntry( ) );
            collectionInfoMarkers
                    .add( createMarkerValues( AppointmentWorkflowConstants.MARK_ENTRY_RESPONSE_PREFIX + entry.getIdEntry( ), response.getResponseValue( ) ) );
        }
    }
}
