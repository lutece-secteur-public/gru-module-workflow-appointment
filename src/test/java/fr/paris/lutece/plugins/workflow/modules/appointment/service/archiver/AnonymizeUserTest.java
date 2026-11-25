/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.appointment.service.archiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.workflow.modules.appointment.service.archiver.anonymization.IAnonymizationService;

/**
 * The anonymisation of an appointment leaves no personal data of the user
 */
public class AnonymizeUserTest
{
    @Test
    public void testEveryPersonalDataIsReplaced( )
    {
        User user = new User( );
        user.setFirstName( "Camille" );
        user.setLastName( "Martin" );
        user.setEmail( "camille.martin@example.org" );
        user.setPhoneNumber( "0612345678" );
        user.setGuid( "guid-of-the-account" );

        WorkflowAppointmentAnonymizeArchiveProcessingService.anonymizeUser( user, new Form( ), new IAnonymizationService( )
        {
            @Override
            public String getAnonymisedValue( Form form )
            {
                return "anonymous";
            }

            @Override
            public String getBeanName( )
            {
                return "test";
            }

            @Override
            public String getPattern( )
            {
                return "%t";
            }
        } );

        assertEquals( "anonymous", user.getFirstName( ) );
        assertEquals( "anonymous", user.getLastName( ) );
        assertEquals( "anonymous", user.getEmail( ) );
        assertNull( user.getPhoneNumber( ) );
        assertNull( user.getGuid( ) );
    }
}
