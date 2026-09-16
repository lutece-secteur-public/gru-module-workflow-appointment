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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import fr.paris.lutece.test.LuteceTestCase;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;

/**
 * Covers the parts of {@link ICalService} that the ical4j 4 migration rewrote: the timezone conversion feeding DtStart/DtEnd, the alternative HTML description,
 * and the RFC 5545 line folding.
 *
 * sendAppointment itself is not exercised here: it ends on MailService, which needs mail infrastructure this test cannot assume.
 */
public class ICalServiceTest extends LuteceTestCase
{
    private static final ZoneId ZONE_PARIS = ZoneId.of( "Europe/Paris" );
    private static final ZoneId ZONE_TOKYO = ZoneId.of( "Asia/Tokyo" );

    /**
     * The conversion must preserve the instant and only change how it is represented. Reading the same local date-time against two zones has to yield two
     * ZonedDateTime pointing at the same moment.
     */
    public void testToCalendarDateTimePreservesInstant( )
    {
        LocalDateTime localDateTime = LocalDateTime.of( 2026, 3, 17, 14, 30 );

        ZonedDateTime inParis = ICalService.toCalendarDateTime( localDateTime, ZONE_PARIS );
        ZonedDateTime inTokyo = ICalService.toCalendarDateTime( localDateTime, ZONE_TOKYO );

        assertEquals( inParis.toInstant( ), inTokyo.toInstant( ) );
        assertEquals( ZONE_PARIS, inParis.getZone( ) );
        assertEquals( ZONE_TOKYO, inTokyo.getZone( ) );
    }

    /**
     * The instant must be the one the local date-time designates in the system zone, which is what the 3.x code produced through epoch milliseconds.
     */
    public void testToCalendarDateTimeUsesSystemZoneAsSource( )
    {
        LocalDateTime localDateTime = LocalDateTime.of( 2026, 7, 1, 9, 0 );

        ZonedDateTime converted = ICalService.toCalendarDateTime( localDateTime, ZONE_TOKYO );

        assertEquals( localDateTime.atZone( ZoneId.systemDefault( ) ).toInstant( ), converted.toInstant( ) );
    }

    /**
     * DtStart and DtEnd must carry the zone, since ical4j 4 dropped the separate setTimeZone call and reads it from the temporal value.
     */
    public void testEventCarriesZonedStartAndEnd( )
    {
        ZonedDateTime start = ICalService.toCalendarDateTime( LocalDateTime.of( 2026, 3, 17, 14, 30 ), ZONE_PARIS );
        ZonedDateTime end = ICalService.toCalendarDateTime( LocalDateTime.of( 2026, 3, 17, 15, 0 ), ZONE_PARIS );

        VEvent event = new VEvent( );
        event = event.add( new DtStart<>( start ) );
        event = event.add( new DtEnd<>( end ) );
        event = event.add( new Summary( "Rendez-vous" ) );

        DtStart<ZonedDateTime> dtStart = (DtStart<ZonedDateTime>) singleProperty( event, Property.DTSTART );
        DtEnd<ZonedDateTime> dtEnd = (DtEnd<ZonedDateTime>) singleProperty( event, Property.DTEND );

        assertEquals( start.toInstant( ), dtStart.getDate( ).toInstant( ) );
        assertEquals( end.toInstant( ), dtEnd.getDate( ).toInstant( ) );
        assertEquals( ZONE_PARIS, dtStart.getDate( ).getZone( ) );
        assertTrue( event.toString( ).contains( "Europe/Paris" ) );
    }

    /**
     * An HTML description must add the X-ALT-DESC property, carrying FMTTYPE=text/html.
     */
    public void testAlternativeHtmlDescriptionAddedForHtmlContent( )
    {
        VEvent event = new VEvent( );

        event = new ICalService( ).addAlternativeHtmlDescription( event, "<p>Votre <b>rendez-vous</b> est confirme</p>" );

        Property htmlProperty = singleProperty( event, "X-ALT-DESC" );
        assertNotNull( htmlProperty );

        List<Parameter> fmtType = htmlProperty.getParameters( "FMTTYPE" );
        assertEquals( 1, fmtType.size( ) );
        assertEquals( "text/html", fmtType.get( 0 ).getValue( ) );
    }

    /**
     * Plain text must be left alone: no X-ALT-DESC, and the event comes back unchanged.
     */
    public void testAlternativeHtmlDescriptionSkippedForPlainText( )
    {
        VEvent event = new VEvent( );

        VEvent result = new ICalService( ).addAlternativeHtmlDescription( event, "Votre rendez-vous est confirme" );

        assertNull( singleProperty( result, "X-ALT-DESC" ) );
    }

    /**
     * A description below the 75 character limit must be returned untouched.
     */
    public void testShortDescriptionIsNotFolded( )
    {
        String shortDescription = "Rendez-vous confirme";

        assertEquals( shortDescription, ICalService.formatICalendarDescription( shortDescription ) );
    }

    /**
     * Beyond 75 characters the description must be folded, and no resulting line may exceed the limit.
     */
    public void testLongDescriptionIsFolded( )
    {
        String longDescription = "a".repeat( 200 );

        String folded = ICalService.formatICalendarDescription( longDescription );

        assertTrue( folded.contains( "\r " ) );
        for ( String line : folded.split( "\r " ) )
        {
            assertTrue( "folded line longer than 75 characters: " + line.length( ), line.length( ) <= 75 );
        }
        assertEquals( longDescription, folded.replace( "\r ", "" ) );
    }

    private Property singleProperty( VEvent event, String name )
    {
        List<Property> properties = event.getProperties( );
        return properties.stream( ).filter( p -> name.equals( p.getName( ) ) ).findFirst( ).orElse( null );
    }
}
