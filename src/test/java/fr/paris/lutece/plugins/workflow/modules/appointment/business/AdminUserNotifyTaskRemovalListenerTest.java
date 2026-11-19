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
package fr.paris.lutece.plugins.workflow.modules.appointment.business;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.workflow.modules.appointment.service.listeners.AdminUserNotifyTaskRemovalListener;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The deletion of a back office user is refused while a task "Notify an administrator" is configured to notify the user
 */
public class AdminUserNotifyTaskRemovalListenerTest extends LuteceTestCase
{
    private static final int ID_TASK = 765432;
    private static final int ID_ADMIN_USER = 765431;

    @Test
    public void testNotifiedUserRefusesTheDeletion( )
    {
        TaskNotifyAdminAppointmentConfigDAO dao = CDI.current( ).select( TaskNotifyAdminAppointmentConfigDAO.class ).get( );
        AdminUserNotifyTaskRemovalListener listener = new AdminUserNotifyTaskRemovalListener( );
        String strIdAdminUser = Integer.toString( ID_ADMIN_USER );
        assertTrue( listener.canBeRemoved( strIdAdminUser ) );

        TaskNotifyAdminAppointmentConfig config = new TaskNotifyAdminAppointmentConfig( );
        config.setIdTask( ID_TASK );
        config.setIdAdminUser( ID_ADMIN_USER );
        config.setSenderName( "sender" );
        config.setSubject( "subject" );
        config.setMessage( "message" );
        config.setRecipientsCc( "" );
        config.setRecipientsBcc( "" );
        config.setLocation( "" );
        dao.insert( config );
        try
        {
            assertFalse( listener.canBeRemoved( strIdAdminUser ) );
            String strMessage = listener.getRemovalRefusedMessage( strIdAdminUser, Locale.ENGLISH );
            assertTrue( strMessage.contains( "1 workflow task" ), strMessage );
            assertTrue( listener.canBeRemoved( "abc" ) );
        }
        finally
        {
            dao.delete( ID_TASK );
        }
        assertTrue( listener.canBeRemoved( strIdAdminUser ) );
    }
}
