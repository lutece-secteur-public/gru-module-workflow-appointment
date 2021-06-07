/*
 * Copyright (c) 2002-2021, City of Paris
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskNotifyCrmConfig;
import fr.paris.lutece.plugins.workflow.modules.appointment.service.TaskNotifyAppointementCrm;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

public class NotifyCrmTaskComponent extends NoFormTaskComponent
{

    // TEMPLATES
    private static final String TEMPLATE_NOTIFY_CRM = "admin/plugins/workflow/modules/appointment/task_notify_appointement_crm.html";

    // MARKS
    private static final String MARK_CONFIG = "config";

    // PARAMETERS
    private static final String PARAMETER_ID_DEMANDE_TYPE = "demandeType";
    private static final String PARAMETER_DATA = "data";
    private static final String PARAMETER_STATUS_TEXT = "statusText";
    private static final String PARAMETER_ID_STATUS_CRM = "idStatusCRM";
    private static final String PARAMETER_OBJECT = "object";
    private static final String PARAMETER_MESSAGE = "message";
    private static final String PARAMETER_SENDER = "sender";

    // SERVICES
    @Inject
    @Named( TaskNotifyAppointementCrm.CONFIG_SERVICE_BEAN_NAME )
    private ITaskConfigService _taskNotifyAppointmentCrmConfigService;

    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<>( );

        TaskNotifyCrmConfig config = _taskNotifyAppointmentCrmConfigService.findByPrimaryKey( task.getId( ) );

        model.put( MARK_CONFIG, config );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_NOTIFY_CRM, locale, model );
        return template.getHtml( );

    }

    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {

        return I18nService.getLocalizedString( "module.workflow.appointment.taskNotifyAppointmentCrm.title", locale );
    }

    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        String strDemandeType = request.getParameter( PARAMETER_ID_DEMANDE_TYPE );
        String strData = request.getParameter( PARAMETER_DATA );
        String strStatusText = request.getParameter( PARAMETER_STATUS_TEXT );
        String strIdStatusCRM = request.getParameter( PARAMETER_ID_STATUS_CRM );
        String strObject = request.getParameter( PARAMETER_OBJECT );
        String strMessage = request.getParameter( PARAMETER_MESSAGE );
        String strSender = request.getParameter( PARAMETER_SENDER );

        TaskNotifyCrmConfig config = _taskNotifyAppointmentCrmConfigService.findByPrimaryKey( task.getId( ) );
        boolean bCreate = false;

        if ( config == null )
        {

            config = new TaskNotifyCrmConfig( );
            config.setIdTask( task.getId( ) );
            bCreate = true;
        }

        config.setDemandeType( strDemandeType );
        config.setData( strData );
        config.setIdStatusCRM( strIdStatusCRM );
        config.setStatusText( strStatusText );
        config.setObject( strObject );
        config.setMessage( strMessage );
        config.setSender( strSender );

        if ( bCreate )
        {
            _taskNotifyAppointmentCrmConfigService.create( config );
        }
        else
        {
            _taskNotifyAppointmentCrmConfigService.update( config );
        }

        return null;
    }

}
