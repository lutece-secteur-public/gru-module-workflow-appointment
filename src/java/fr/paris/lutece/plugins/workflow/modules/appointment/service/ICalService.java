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

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.StringTokenizer;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;

/**
 * Service to send iCal appointments by email
 */
@Dependent
@Named( "workflow-appointment.iCalService" )
public class ICalService
{
    // properties
    private static final String PROPERTY_MAIL_LIST_SEPARATOR = "mail.list.separator";
    private static final String PROPERTY_ICAL_PRODID = "workflow-appointment.ical.prodid";
    private static final String PROPERTY_DEFAULT_TIME_ZONE = "workflow-appointment.server.timezone.id";
    private static final String PROPERTY_RELATIVE_PATH_TO_TIME_ZONE_FILE = "workflow-appointment.server.timezone.fileRelativePath";

    // constants
    private static final String CONSTANT_MAILTO = "MAILTO:";

    // messages
    private static final String MSG_TIMEZONE_FILE_NOT_FOUND = "iCal default Time zone file not found";
    private static final String MSG_TIMEZONE_FILE_INCORRECT = "iCal default Time zone file format problem";

    /**
     * Send an appointment to a user by email.
     * 
     * @param strEmailAttendee
     *            Comma separated list of users that will attend the appointment
     * @param strEmailOptionnal
     *            Comma separated list of users that will be invited to the appointment, but who are not required.
     * @param strSubject
     *            The subject of the appointment.
     * @param strBodyContent
     *            The body content that describes the appointment
     * @param strLocation
     *            The location of the appointment
     * @param strSenderName
     *            The name of the sender
     * @param strSenderEmail
     *            The email of the sender
     * @param appointment
     *            The appointment
     * @param bCreate
     *            True to notify the creation of the appointment, false to notify its removal
     */
    public void sendAppointment( String strEmailAttendee, String strEmailOptionnal, String strSubject, String strBodyContent, String strLocation,
            String strSenderName, String strSenderEmail, AppointmentDTO appointment, boolean bCreate )
    {

        CalendarBuilder builder = new CalendarBuilder( );
        Calendar iCalendar;
        try
        {
            String strRelativeWebPath = AppPropertiesService.getProperty( PROPERTY_RELATIVE_PATH_TO_TIME_ZONE_FILE );
            String absoluteTimeZoneFilePath = AppPathService.getAbsolutePathFromRelativePath( strRelativeWebPath );
            iCalendar = builder.build( new FileInputStream( absoluteTimeZoneFilePath ) );
        }
        catch( FileNotFoundException ex )
        {
            AppLogService.error( MSG_TIMEZONE_FILE_NOT_FOUND, ex );
            return;
        }
        catch( IOException | ParserException ex )
        {
            AppLogService.error( MSG_TIMEZONE_FILE_INCORRECT, ex );
            return;
        }

        TimeZoneRegistry registry = builder.getRegistry( );
        ZoneId zoneId = registry.getZoneId( AppPropertiesService.getProperty( PROPERTY_DEFAULT_TIME_ZONE ) );

        // Same instant as before: the local date-time is read in the system zone, then
        // rendered in the configured zone. ical4j 4 carries the zone in the temporal
        // value itself, so DtStart/DtEnd no longer need a separate setTimeZone call.
        ZonedDateTime beginningDateTime = toCalendarDateTime( appointment.getStartingDateTime( ), zoneId );
        ZonedDateTime endingDateTime = toCalendarDateTime( appointment.getEndingDateTime( ), zoneId );

        VEvent event = new VEvent( );
        event = event.add( new DtStart<>( beginningDateTime ) );
        event = event.add( new DtEnd<>( endingDateTime ) );
        event = event.add( new Summary( ( strSubject != null ) ? strSubject : StringUtils.EMPTY ) );

        // Format the description that goes in the ICalendar
        String formatedIcalendarDescription = formatICalendarDescription( strBodyContent );

        // ical4j 4 builds Organizer from a plain String without throwing URISyntaxException,
        // so the try/catch that wrapped this block is gone.
        {
            event = event.add( new Uid( Appointment.APPOINTMENT_RESOURCE_TYPE + appointment.getIdAppointment( ) ) );
            String strEmailSeparator = AppPropertiesService.getProperty( PROPERTY_MAIL_LIST_SEPARATOR, ";" );
            if ( StringUtils.isNotEmpty( strEmailAttendee ) )
            {
                StringTokenizer st = new StringTokenizer( strEmailAttendee, strEmailSeparator );
                while ( st.hasMoreTokens( ) )
                {
                    event = addAttendee( event, st.nextToken( ), true );
                }
            }
            if ( StringUtils.isNotEmpty( strEmailOptionnal ) )
            {
                StringTokenizer st = new StringTokenizer( strEmailOptionnal, strEmailSeparator );
                while ( st.hasMoreTokens( ) )
                {
                    event = addAttendee( event, st.nextToken( ), false );
                }
            }
            Organizer organizer = new Organizer( strSenderEmail );
            organizer = organizer.add( new Cn( strSenderName ) );
            event = event.add( organizer );
            event = event.add( new Location( strLocation ) );
            event = event.add( new Description( formatedIcalendarDescription ) );
            // Add an alternative description to properly render HTML content
            event = addAlternativeHtmlDescription( event, formatedIcalendarDescription );
        }

        // ical4j 4 dropped the Method.REQUEST / Version.VERSION_2_0 / CalScale.GREGORIAN
        // singletons; the properties are now built from their VALUE_* string constants.
        iCalendar = iCalendar.add( new Method( bCreate ? Method.VALUE_REQUEST : Method.VALUE_CANCEL ) );
        iCalendar = iCalendar.add( new ProdId( AppPropertiesService.getProperty( PROPERTY_ICAL_PRODID ) ) );
        iCalendar = iCalendar.add( new Version( new ParameterList( ), Version.VALUE_2_0 ) );
        iCalendar = iCalendar.add( new CalScale( CalScale.VALUE_GREGORIAN ) );
        iCalendar = iCalendar.add( event );

        MailService.sendMailCalendar( strEmailAttendee, strEmailOptionnal, null, strSenderName, strSenderEmail,
                ( strSubject != null ) ? strSubject : StringUtils.EMPTY, strBodyContent, iCalendar.toString( ), bCreate );
    }

    /**
     * Convert an appointment date-time to the zone the calendar is rendered in. The local date-time is read in the system zone, then expressed in the target
     * zone, so the instant is preserved and only its representation changes.
     * 
     * @param dateTime
     *            The local date-time of the appointment
     * @param zoneId
     *            The zone the calendar is rendered in
     * @return the same instant, expressed in the target zone
     */
    static ZonedDateTime toCalendarDateTime( LocalDateTime dateTime, ZoneId zoneId )
    {
        return dateTime.atZone( ZoneId.systemDefault( ) ).toInstant( ).atZone( zoneId );
    }

    /**
     * Add an attendee to an event
     * 
     * @param event
     *            The event to add the attendee to
     * @param strEmail
     *            The email of the user
     * @param bRequired
     *            True if the presence of the user is mandatory, false if it is optional
     * @return the event carrying the new attendee
     */
    private VEvent addAttendee( VEvent event, String strEmail, boolean bRequired )
    {
        Attendee attendee = new Attendee( URI.create( CONSTANT_MAILTO + strEmail ) );
        attendee = attendee.add( bRequired ? Role.REQ_PARTICIPANT : Role.OPT_PARTICIPANT );
        attendee = attendee.add( PartStat.NEEDS_ACTION );
        attendee = attendee.add( Rsvp.FALSE );
        return event.add( attendee );
    }

    /**
     * Format the String containing the description of a calendar invite to respect the ICalendar specifications: 75 characters per line, CRLF + white-space at
     * the start of new lines... ( c.f. <a href="https://datatracker.ietf.org/doc/html/rfc5545#section-3.1">iCalendar RFC5545</a> )
     * 
     * @param strDescription
     *            The calendar's invite description to format
     * @return the formated description, or the value of the original description if the formatting was not necessary
     */
    public static String formatICalendarDescription( String strDescription )
    {
        // ICalendar specific properties. The folding symbol is a return to next line + a white-space
        String foldingSymbol = "\r ";
        int lineMaxLength = 75;

        int descriptionLength = strDescription.length( );

        if ( descriptionLength <= lineMaxLength )
        {
            return strDescription;
        }

        strDescription = strDescription.replaceAll( "\n", foldingSymbol );

        StringBuilder formatedDesctiption = new StringBuilder( strDescription.substring( 0, lineMaxLength ) );

        // Use ICalendar's folding method ( CRLF + white-space ) after every 75 characters
        for ( int i = lineMaxLength; i < descriptionLength; i += lineMaxLength )
        {
            if ( i + lineMaxLength < descriptionLength )
            {
                // Add the folding symbol followed by the next 75 characters of the description
                formatedDesctiption.append( foldingSymbol ).append( strDescription.substring( i, i + lineMaxLength ) );
            }
            else
            {
                // Add the remaining characters and stop the process
                formatedDesctiption.append( foldingSymbol ).append( strDescription.substring( i ) );
                break;
            }
        }
        return formatedDesctiption.toString( );
    }

    /**
     * Add an alternative description to process and render HTML content properly. This should only be used if there is HTML in the description
     * 
     * @param event
     *            The Calendar Event being created
     * @param description
     *            The description of the Event
     * @return the event, carrying the alternative description when the content is HTML
     */
    public VEvent addAlternativeHtmlDescription( VEvent event, String description )
    {
        // HTML regex pattern
        String patternHtml = "[\\S\\s]*\\<\\D+[\\S\\s]*\\>[\\S\\s]*\\<\\/\\D+[\\S\\s]*\\>[\\S\\s]*";

        // Check if the description contains HTML elements
        if ( description.matches( patternHtml ) )
        {
            // Create the alternative calendar description with the "X-ALT-DESC" property
            XParameter fmtTypeParameter = new XParameter( "FMTTYPE", "text/html" );
            ParameterList htmlParameters = new ParameterList( Collections.singletonList( fmtTypeParameter ) );
            XProperty htmlProp = new XProperty( "X-ALT-DESC", htmlParameters, description );

            // Add the alternative description to the event
            return event.add( htmlProp );
        }
        return event;
    }
}
