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
package fr.paris.lutece.plugins.workflow.modules.appointment.business;

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

public class TaskNotifyCrmConfig extends TaskConfig
{

    private String _strDemandeType;
    private String _strData;
    private String _strIdStatusCRM;
    private String _strStatusText;
    private String _strObject;
    private String _strMessage;
    private String _strSender;

    public String getDemandeType( )
    {
        return _strDemandeType;
    }

    public void setDemandeType( String strDemandeType )
    {
        _strDemandeType = strDemandeType;
    }

    public String getData( )
    {
        return _strData;
    }

    public void setData( String strData )
    {
        _strData = strData;
    }

    public String getIdStatusCRM( )
    {
        return _strIdStatusCRM;
    }

    public void setIdStatusCRM( String strIdStatusCRM )
    {
        _strIdStatusCRM = strIdStatusCRM;
    }

    public String getStatusText( )
    {
        return _strStatusText;
    }

    public void setStatusText( String strStatusText )
    {
        _strStatusText = strStatusText;
    }

    public String getObject( )
    {
        return _strObject;
    }

    public void setObject( String strObject )
    {
        _strObject = strObject;
    }

    public String getMessage( )
    {
        return _strMessage;
    }

    public void setMessage( String strMessage )
    {
        _strMessage = strMessage;
    }

    public String getSender( )
    {
        return _strSender;
    }

    public void setSender( String strSender )
    {
        _strSender = strSender;
    }

}
