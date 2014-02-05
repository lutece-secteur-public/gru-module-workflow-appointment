/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * ManualAppointmentNotificationHistoryDAO
 */
public class ManualAppointmentNotificationHistoryDAO implements IManualAppointmentNotificationHistoryDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_history,id_appointment,email,subject,message "
            + "FROM workflow_task_manual_app_notify WHERE id_notif=?";
    private static final String SQL_QUERY_FIND_BY_ID_HISTORY = "SELECT id_notif,id_history,id_appointment,email,subject,message "
            + "FROM workflow_task_manual_app_notify WHERE id_history=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_manual_app_notify( "
            + "id_notif,id_history,id_appointment,email,subject,message)" + "VALUES (?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_manual_app_notify WHERE id_notif = ? ";
    private static final String SQL_QUERY_DELETE_BY_ID_APPOINTMENT = "DELETE FROM workflow_task_manual_app_notify WHERE id_appointment = ? ";
    private static final String SQL_QUEERY_NEW_PRIMARY_KEY = "SELECT MAX(id_notif) FROM workflow_task_manual_app_notify";
    
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUEERY_NEW_PRIMARY_KEY, plugin );
        daoUtil.executeQuery( );
        int nRes = 1;
        if ( daoUtil.next( ) )
        {
            nRes = daoUtil.getInt( 1 ) + 1;
        }
        daoUtil.free( );
        return nRes;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void create( ManualAppointmentNotificationHistory history, Plugin plugin )
    {
        history.setIdManualNotif( newPrimaryKey( plugin ) );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        int nIndex = 1;
        daoUtil.setInt( nIndex++, history.getIdManualNotif( ) );
        daoUtil.setInt( nIndex++, history.getIdHistory( ) );
        daoUtil.setInt( nIndex++, history.getIdAppointment( ) );
        daoUtil.setString( nIndex++, history.getEmailTo( ) );
        daoUtil.setString( nIndex++, history.getEmailSubject( ) );
        daoUtil.setString( nIndex, history.getEmailMessage( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ManualAppointmentNotificationHistory findByPrimaryKey( int nIdNotif, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nIdNotif );
        daoUtil.executeQuery( );
        ManualAppointmentNotificationHistory history;
        if ( daoUtil.next( ) )
        {
            int nIndex = 1;
            history = new ManualAppointmentNotificationHistory( );
            history.setIdManualNotif( nIdNotif );
            history.setIdHistory( daoUtil.getInt( nIndex++ ) );
            history.setIdAppointment( daoUtil.getInt( nIndex++ ) );
            history.setEmailTo( daoUtil.getString( nIndex++ ) );
            history.setEmailSubject( daoUtil.getString( nIndex++ ) );
            history.setEmailMessage( daoUtil.getString( nIndex ) );
        }
        else
        {
            history = null;
        }
        daoUtil.free( );
        return history;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdNotif, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdNotif );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ManualAppointmentNotificationHistory> findByIdHistory( int nIdHistory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_ID_HISTORY, plugin );
        daoUtil.setInt( 1, nIdHistory );
        daoUtil.executeQuery( );
        List<ManualAppointmentNotificationHistory> listHistory = new ArrayList<ManualAppointmentNotificationHistory>( );
        if ( daoUtil.next( ) )
        {
            int nIndex = 1;
            ManualAppointmentNotificationHistory history = new ManualAppointmentNotificationHistory( );
            history.setIdManualNotif( daoUtil.getInt( nIndex++ ) );
            history.setIdHistory( daoUtil.getInt( nIndex++ ) );
            history.setIdAppointment( daoUtil.getInt( nIndex++ ) );
            history.setEmailTo( daoUtil.getString( nIndex++ ) );
            history.setEmailSubject( daoUtil.getString( nIndex++ ) );
            history.setEmailMessage( daoUtil.getString( nIndex ) );
            listHistory.add( history );
        }
        daoUtil.free( );
        return listHistory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByIdAppointment( int nIdAppointment, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_APPOINTMENT, plugin );
        daoUtil.setInt( 1, nIdAppointment );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

}
