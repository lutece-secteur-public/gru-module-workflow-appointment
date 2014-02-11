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
package fr.paris.lutece.plugins.workflow.modules.appointment.web;

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskChangeAppointmentStatusConfig;
import fr.paris.lutece.plugins.workflow.modules.appointment.service.TaskChangeAppointmentStatus;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import javax.servlet.http.HttpServletRequest;


/**
 * ChangeAppointmentStatusTaskComponent
 */
public class ChangeAppointmentStatusTaskComponent extends NoFormTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_CHANGE_APPOINTMENT_STATUS_CONFIG = "admin/plugins/workflow/modules/appointment/task_change_appointment_status_config.html";

    // FIELDS
    private static final String FIELD_APPOINTMENT_STATUS = "module.workflow.appointment.task_change_appointment_status.fieldAppointmentStatus";

    // MESSAGES
    private static final String MESSAGE_MANDATORY_FIELD = "module.workflow.appointment.message.mandatory.field";
    private static final String MESSAGE_APPOINTMENT_VALIDATED = "module.workflow.appointment.message.appointmentValidated";
    private static final String MESSAGE_APPOINTMENT_CANCELED = "module.workflow.appointment.message.appointmentCanceled";
    private static final String MESSAGE_LABEL_STATUS_VALIDATED = "appointment.message.labelStatusValidated";
    private static final String MESSAGE_LABEL_STATUS_REJECTED = "appointment.message.labelStatusRejected";

    // MARKS
    private static final String MARK_CONFIG = "config";
    private static final String MARK_REF_LIST_STATUS = "refListStatus";

    // PARAMETERS
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_APPOINTMENT_STATUS = "status";

    // SERVICES
    @Inject
    @Named( TaskChangeAppointmentStatus.CONFIG_SERVICE_BEAN_NAME )
    private ITaskConfigService _taskChangeAppointmentStatusConfigService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        String strAppointmentStatus = request.getParameter( PARAMETER_APPOINTMENT_STATUS );
        String strApply = request.getParameter( PARAMETER_APPLY );
        String strError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strApply ) )
        {
            if ( StringUtils.isBlank( strAppointmentStatus ) )
            {
                strError = FIELD_APPOINTMENT_STATUS;
            }
        }

        int nAppointmentStatus = AppointmentService.getService(  ).parseInt( strAppointmentStatus );

        if ( !strError.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        TaskChangeAppointmentStatusConfig config = _taskChangeAppointmentStatusConfigService.findByPrimaryKey( task.getId(  ) );
        Boolean bCreate = false;

        if ( config == null )
        {
            config = new TaskChangeAppointmentStatusConfig(  );
            config.setIdTask( task.getId(  ) );
            bCreate = true;
        }

        config.setAppointmentStatus( nAppointmentStatus );

        if ( bCreate )
        {
            _taskChangeAppointmentStatusConfigService.create( config );
        }
        else
        {
            _taskChangeAppointmentStatusConfigService.update( config );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        TaskChangeAppointmentStatusConfig config = _taskChangeAppointmentStatusConfigService.findByPrimaryKey( task.getId(  ) );

        ReferenceList refListStatus = new ReferenceList(  );
        refListStatus.addItem( StringUtils.EMPTY, StringUtils.EMPTY );
        refListStatus.addItem( Appointment.STATUS_VALIDATED,
            I18nService.getLocalizedString( MESSAGE_LABEL_STATUS_VALIDATED, locale ) );
        refListStatus.addItem( Appointment.STATUS_REJECTED,
            I18nService.getLocalizedString( MESSAGE_LABEL_STATUS_REJECTED, locale ) );

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_CONFIG, config );
        model.put( MARK_REF_LIST_STATUS, refListStatus );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_CHANGE_APPOINTMENT_STATUS_CONFIG, locale,
                model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        TaskChangeAppointmentStatusConfig config = _taskChangeAppointmentStatusConfigService.findByPrimaryKey( task.getId(  ) );

        return I18nService.getLocalizedString( ( config.getAppointmentStatus(  ) > 0 ) ? MESSAGE_APPOINTMENT_VALIDATED
                                                                                       : MESSAGE_APPOINTMENT_CANCELED,
            locale );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }
}
