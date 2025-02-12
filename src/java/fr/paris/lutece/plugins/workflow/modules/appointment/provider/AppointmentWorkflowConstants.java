/*
 * Copyright (c) 2002-2024, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.appointment.provider;

/**
 * Class providing constant values, used throughout the module
 */
public final class AppointmentWorkflowConstants
{
    /**
     * Private constructor
     */
    private AppointmentWorkflowConstants( )
    {
    }

    // CONSTANT MARKS
    public static final String MARK_MESSAGE = "message";
    public static final String MARK_LIST_RESPONSE = "listResponse";
    public static final String MARK_FIRSTNAME = "firstName";
    public static final String MARK_LASTNAME = "lastName";
    public static final String MARK_EMAIL = "email";
    public static final String MARK_DATE_APPOINTMENT = "date_appointment";
    public static final String MARK_TIME_APPOINTMENT = "time_appointment";
    public static final String MARK_END_TIME_APPOINTMENT = "end_time_appointment";
    public static final String MARK_REFERENCE = "reference";
    public static final String MARK_URL_REPORT = "url_report";
    public static final String MARK_URL_CANCEL = "url_cancel";
    public static final String MARK_CANCEL_MOTIF = "cancelMotif";
    public static final String MARK_URL_VALIDATE = "url_validate";
    public static final String MARK_RECAP = "recap";
    public static final String MARK_ENTRY_RESPONSE_PREFIX = "response_";

    // CONSTANT MARK DESCRIPTIONS
    public static final String DESCRIPTION_MARK_FIRSTNAME = "module.workflow.appointment.task_notify_appointment_config.label_firstname";
    public static final String DESCRIPTION_MARK_LASTNAME = "module.workflow.appointment.task_notify_appointment_config.label_lastname";
    public static final String DESCRIPTION_MARK_EMAIL = "module.workflow.appointment.task_notify_appointment_config.label_email";
    public static final String DESCRIPTION_MARK_DATE_APPOINTMENT = "module.workflow.appointment.task_notify_appointment_config.label_date_appointment";
    public static final String DESCRIPTION_MARK_TIME_APPOINTMENT = "module.workflow.appointment.task_notify_appointment_config.label_time_appointment";
    public static final String DESCRIPTION_MARK_END_TIME_APPOINTMENT = "module.workflow.appointment.task_notify_appointment_config.label_end_time_appointment";
    public static final String DESCRIPTION_MARK_REFERENCE = "module.workflow.appointment.task_notify_appointment_config.label_reference";
    public static final String DESCRIPTION_MARK_URL_REPORT = "module.workflow.appointment.task_notify_appointment_config.label_url_report";
    public static final String DESCRIPTION_MARK_URL_CANCEL = "module.workflow.appointment.task_notify_appointment_config.label_url_cancel";
    public static final String DESCRIPTION_MARK_CANCEL_MOTIF = "module.workflow.appointment.task_notify_appointment_config.label_cancelmotif";
    public static final String DESCRIPTION_MARK_URL_VALIDATE = "module.workflow.appointment.task_notify_appointment_config.label_url_validate";
    public static final String DESCRIPTION_MARK_RECAP = "module.workflow.appointment.task_notify_appointment_config.label_recap";
}
