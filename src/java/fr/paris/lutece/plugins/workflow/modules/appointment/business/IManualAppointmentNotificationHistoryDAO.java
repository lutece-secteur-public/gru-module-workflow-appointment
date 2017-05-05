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

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Interface for manual appointment notification history
 */
public interface IManualAppointmentNotificationHistoryDAO {
	/**
	 * Create a new manual appointment notification history
	 * 
	 * @param history
	 *            The history to insert into the database
	 * @param plugin
	 *            The plugin
	 */
	void create(ManualAppointmentNotificationHistory history, Plugin plugin);

	/**
	 * Get a manual appointment notification history from its primary key
	 * 
	 * @param nIdNotif
	 *            The id of the history to get
	 * @param plugin
	 *            The plugin
	 * @return The history
	 */
	ManualAppointmentNotificationHistory findByPrimaryKey(int nIdNotif, Plugin plugin);

	/**
	 * Get the list of history associated with a given history id
	 * 
	 * @param nIdHistory
	 *            The history id
	 * @param plugin
	 *            The plugin
	 * @return the list of history, or an empty list if none was found
	 */
	List<ManualAppointmentNotificationHistory> findByIdHistory(int nIdHistory, Plugin plugin);

	/**
	 * Remove an history from its primary key
	 * 
	 * @param nIdNotif
	 *            The id of the history
	 * @param plugin
	 *            the plugin
	 */
	void delete(int nIdNotif, Plugin plugin);

	/**
	 * Remove every history associated with a given appointment
	 * 
	 * @param nIdAppointment
	 *            The id of the appointment
	 * @param plugin
	 *            the plugin
	 */
	void deleteByIdAppointment(int nIdAppointment, Plugin plugin);
}
