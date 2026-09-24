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
package fr.paris.lutece.plugins.workflow.modules.appointment.web;

import fr.paris.lutece.test.LuteceTestCase;

import org.junit.jupiter.api.Test;

/**
 * Covers the key signing the workflow action links sent by mail: it must bind each value, not their sum.
 */
public class ExecuteWorkflowActionTest extends LuteceTestCase
{
    private static final long TIMESTAMP = 1_790_000_000_000L;

    /**
     * The same values give the same key, so a link sent by mail is accepted back.
     */
    @Test
    public void testSameValuesSameKey( )
    {
        assertEquals( ExecuteWorkflowAction.computeAuthenticationKey( 12, 5, TIMESTAMP, 9001 ),
                ExecuteWorkflowAction.computeAuthenticationKey( 12, 5, TIMESTAMP, 9001 ) );
    }

    /**
     * Another admin user with a timestamp shifted by the difference keeps the sum of the values: the key must still
     * change, or a received link would be turned into one that signs in as the administrator.
     */
    @Test
    public void testValuesWithTheSameSumGiveAnotherKey( )
    {
        String strReceived = ExecuteWorkflowAction.computeAuthenticationKey( 12, 5, TIMESTAMP, 9001 );
        String strForged = ExecuteWorkflowAction.computeAuthenticationKey( 12, 1, TIMESTAMP + 4, 9001 );

        assertFalse( strReceived.equals( strForged ) );
    }

    /**
     * Moving a value from one field to another keeps the concatenation of the digits: the separator must keep them apart.
     */
    @Test
    public void testShiftedDigitsGiveAnotherKey( )
    {
        assertFalse( ExecuteWorkflowAction.computeAuthenticationKey( 12, 5, TIMESTAMP, 9001 )
                .equals( ExecuteWorkflowAction.computeAuthenticationKey( 1, 25, TIMESTAMP, 9001 ) ) );
    }
}
