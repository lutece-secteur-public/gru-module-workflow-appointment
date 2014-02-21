/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;


/**
 * DTO to send email. This class extends TaskConfig to allow task config to use
 * this class (because Java does not allow several extends)
 */
public class NotifyAppointmentDTO extends TaskConfig
{
    private String _strSubject;
    private String _strMessage;
    private String _strSenderName;
    private String _strRecipientsCc;
    private String _strRecipientsBcc;
    private boolean _bSendICalNotif;
    private String _strLocation;

    /**
     * Get the subject
     * @return the subject of the message
     */
    public String getSubject(  )
    {
        return _strSubject;
    }

    /**
     * Set the subject of the message
     * @param subject the subject of the message
     */
    public void setSubject( String subject )
    {
        _strSubject = subject;
    }

    /**
     * Get the message
     * @return the message of the notification
     */
    public String getMessage(  )
    {
        return _strMessage;
    }

    /**
     * Set the message of the notification
     * @param message the message of the notification
     */
    public void setMessage( String message )
    {
        _strMessage = message;
    }

    /**
     * Get the sender name
     * @return the sender name
     */
    public String getSenderName(  )
    {
        return _strSenderName;
    }

    /**
     * Set the sender name
     * @param senderName the sender name
     */
    public void setSenderName( String senderName )
    {
        _strSenderName = senderName;
    }

    /**
     * Returns the Recipient
     * @return The Recipient
     */
    public String getRecipientsCc(  )
    {
        return _strRecipientsCc;
    }

    /**
     * Sets the Recipient
     * @param strRecipient The Recipient
     */
    public void setRecipientsCc( String strRecipient )
    {
        _strRecipientsCc = strRecipient;
    }

    /**
     * Returns the Recipient
     * @return The Recipient
     */
    public String getRecipientsBcc(  )
    {
        return _strRecipientsBcc;
    }

    /**
     * Sets the Recipient
     * @param strRecipient The Recipient
     */
    public void setRecipientsBcc( String strRecipient )
    {
        _strRecipientsBcc = strRecipient;
    }

    /**
     * Check if the notification should include an iCal event
     * @return True if the notification should include an iCal event, false
     *         otherwise
     */
    public boolean getSendICalNotif( )
    {
        return _bSendICalNotif;
    }

    /**
     * Set whether this notification should include an iCal event
     * @param bSendICalNotif True if this notification should include an
     *            iCal event, false otherwise
     */
    public void setSendICalNotif( boolean bSendICalNotif )
    {
        this._bSendICalNotif = bSendICalNotif;
    }

    /**
     * Get the localization of the appointment
     * @return The localization of the appointment
     */
    public String getLocation( )
    {
        return _strLocation;
    }

    /**
     * Set the localization of the appointment
     * @param strLocation The localization of the appointment
     */
    public void setLocation( String strLocation )
    {
        this._strLocation = strLocation;
    }
}
