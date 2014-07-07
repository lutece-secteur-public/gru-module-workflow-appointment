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

import fr.paris.lutece.plugins.workflow.modules.appointment.service.WorkflowAppointmentPlugin;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 *
 * TaskNotifyAppointmentConfigDAO
 *
 */
public class TaskNotifyAppointmentConfigDAO implements ITaskConfigDAO<TaskNotifyAppointmentConfig>
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_task,sender_name,sender_email,subject,message,recipients_cc,recipients_bcc,id_action_cancel,ical_notification,create_notif,location,is_sms " +
        "FROM workflow_task_notify_appointment_cf WHERE id_task=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_notify_appointment_cf( " +
        "id_task,sender_name,sender_email,subject,message,recipients_cc,recipients_bcc,id_action_cancel,ical_notification,create_notif,location,is_sms)" +
        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_notify_appointment_cf " +
        " SET sender_name = ?, sender_email = ?, subject = ?, message = ?, recipients_cc = ?, recipients_bcc = ?, id_action_cancel = ?, ical_notification = ?, create_notif = ?, location = ?, is_sms = ?" +
        " WHERE id_task = ? ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_notify_appointment_cf WHERE id_task = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( TaskNotifyAppointmentConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, WorkflowAppointmentPlugin.getPlugin(  ) );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, config.getIdTask(  ) );
        daoUtil.setString( nIndex++, config.getSenderName(  ) );
        daoUtil.setString( nIndex++, config.getSenderEmail(  ) );
        daoUtil.setString( nIndex++, config.getSubject(  ) );
        daoUtil.setString( nIndex++, config.getMessage(  ) );
        daoUtil.setString( nIndex++, config.getRecipientsCc(  ) );
        daoUtil.setString( nIndex++, config.getRecipientsBcc(  ) );
        daoUtil.setInt( nIndex++, config.getIdActionCancel(  ) );
        daoUtil.setBoolean( nIndex++, config.getSendICalNotif(  ) );
        daoUtil.setBoolean( nIndex++, config.getCreateNotif(  ) );
        daoUtil.setString( nIndex++, config.getLocation(  ) );
        daoUtil.setBoolean( nIndex, config.getIsSms(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( TaskNotifyAppointmentConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowAppointmentPlugin.getPlugin(  ) );

        int nIndex = 1;

        daoUtil.setString( nIndex++, config.getSenderName(  ) );
        daoUtil.setString( nIndex++, config.getSenderEmail(  ) );
        daoUtil.setString( nIndex++, config.getSubject(  ) );
        daoUtil.setString( nIndex++, config.getMessage(  ) );
        daoUtil.setString( nIndex++, config.getRecipientsCc(  ) );
        daoUtil.setString( nIndex++, config.getRecipientsBcc(  ) );
        daoUtil.setInt( nIndex++, config.getIdActionCancel(  ) );
        daoUtil.setBoolean( nIndex++, config.getSendICalNotif(  ) );
        daoUtil.setBoolean( nIndex++, config.getCreateNotif(  ) );
        daoUtil.setString( nIndex++, config.getLocation(  ) );
        daoUtil.setBoolean( nIndex++, config.getIsSms(  ) );

        daoUtil.setInt( nIndex, config.getIdTask(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskNotifyAppointmentConfig load( int nIdTask )
    {
        TaskNotifyAppointmentConfig config = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, WorkflowAppointmentPlugin.getPlugin(  ) );

        daoUtil.setInt( 1, nIdTask );

        daoUtil.executeQuery(  );

        int nIndex = 1;

        if ( daoUtil.next(  ) )
        {
            config = new TaskNotifyAppointmentConfig(  );
            config.setIdTask( daoUtil.getInt( nIndex++ ) );
            config.setSenderName( daoUtil.getString( nIndex++ ) );
            config.setSenderEmail( daoUtil.getString( nIndex++ ) );
            config.setSubject( daoUtil.getString( nIndex++ ) );
            config.setMessage( daoUtil.getString( nIndex++ ) );
            config.setRecipientsCc( daoUtil.getString( nIndex++ ) );
            config.setRecipientsBcc( daoUtil.getString( nIndex++ ) );
            config.setIdActionCancel( daoUtil.getInt( nIndex++ ) );
            config.setSendICalNotif( daoUtil.getBoolean( nIndex++ ) );
            config.setCreateNotif( daoUtil.getBoolean( nIndex++ ) );
            config.setLocation( daoUtil.getString( nIndex++ ) );
            config.setIsSms( daoUtil.getBoolean( nIndex ) );
        }

        daoUtil.free(  );

        return config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdTask )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowAppointmentPlugin.getPlugin(  ) );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
