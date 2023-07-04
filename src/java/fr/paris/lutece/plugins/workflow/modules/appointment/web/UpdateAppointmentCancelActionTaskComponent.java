/*
 * Copyright (c) 2002-2022, City of Paris
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.appointment.business.TaskUpdateAppointmentCancelActionConfig;
import fr.paris.lutece.plugins.workflow.modules.appointment.service.TaskUpdateAppointmentCancelReportAction;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 *
 * NotifyAppointmentTaskComponent
 *
 */
public class UpdateAppointmentCancelActionTaskComponent extends NoFormTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_UPDATE_APPOINTMENT_CANCEL_ACTION_CONFIG = "admin/plugins/workflow/modules/appointment/task_update_appointment_cancel_action_config.html";

    // MESSAGES
    private static final String MESSAGE_CANCEL_ACTION_UPDATED = "module.workflow.appointment.message.cancelActionUpdated";
    private static final String MESSAGE_MANDATORY_FIELD = "module.workflow.appointment.message.mandatory.field";
    private static final String FIELD_CANCEL_ACTION = "task_notify_appointment_config.label_action";
    private static final String FIELD_REPORT_ACTION = "task_notify_appointment_config.label_action_report";

    // MARKS
    private static final String MARK_LIST_ACTIONS = "list_actions";
    private static final String MARK_CONFIG = "config";

    // PARAMETERS
    private static final String PARAMETER_ID_ACTION_CANCEL = "id_action_cancel";
    private static final String PARAMETER_ID_ACTION_REPORT = "id_action_report";

    // SERVICES
    @Inject
    @Named( TaskUpdateAppointmentCancelReportAction.CONFIG_SERVICE_BEAN_NAME )
    private ITaskConfigService _taskUpdateAppointmentCancelActionConfigService;
    @Inject
    @Named( ActionService.BEAN_SERVICE )
    private ActionService _actionService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        TaskUpdateAppointmentCancelActionConfig config = _taskUpdateAppointmentCancelActionConfigService.findByPrimaryKey( task.getId( ) );

        ActionFilter filter = new ActionFilter( );
        filter.setAutomaticReflexiveAction( false );
        Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );
        filter.setIdStateBefore( action.getStateAfter( ).getId( ) );

        List<Action> listActions = _actionService.getListActionByFilter( filter );
        List<Integer> listIdStateBefore = action.getListIdStateBefore();
        int stateAfterId = action.getStateAfter().getId();
        int alternativeStateAfterId = action.getStateAfter().getId();

        if (listIdStateBefore.contains(stateAfterId) || listIdStateBefore.contains(alternativeStateAfterId))
        {
            for ( Action actionFound : listActions )
            {
                if ( actionFound.getId( ) == action.getId( ) )
                {
                    listActions.remove( actionFound );

                    break;
                }
            }
        }

        ReferenceList refListActions = new ReferenceList( listActions.size( ) + 1 );
        refListActions.addItem( 0, StringUtils.EMPTY );

        for ( Action actionFound : listActions )
        {
            refListActions.addItem( actionFound.getId( ), actionFound.getName( ) );
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_LIST_ACTIONS, refListActions );
        model.put( MARK_CONFIG, config );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_UPDATE_APPOINTMENT_CANCEL_ACTION_CONFIG, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        TaskUpdateAppointmentCancelActionConfig config = _taskUpdateAppointmentCancelActionConfigService.findByPrimaryKey( task.getId( ) );
        boolean bCreate = false;

        if ( config == null )
        {
            bCreate = true;
            config = new TaskUpdateAppointmentCancelActionConfig( );
            config.setIdTask( task.getId( ) );
        }

        String strIdActionCancel = request.getParameter( PARAMETER_ID_ACTION_CANCEL );
        String strIdActionReport = request.getParameter( PARAMETER_ID_ACTION_REPORT );

        int nIdActionCancel = 0;
        int nIdActionReport = 0;

        if ( StringUtils.isEmpty( strIdActionCancel ) || !StringUtils.isNumeric( strIdActionCancel ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( FIELD_CANCEL_ACTION, locale )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }
        if ( StringUtils.isEmpty( strIdActionReport ) || !StringUtils.isNumeric( strIdActionReport ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( FIELD_REPORT_ACTION, locale )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        nIdActionCancel = Integer.parseInt( strIdActionCancel );
        nIdActionReport = Integer.parseInt( strIdActionReport );
        config.setIdActionCancel( nIdActionCancel );
        config.setIdActionReport( nIdActionReport );

        if ( bCreate )
        {
            _taskUpdateAppointmentCancelActionConfigService.create( config );
        }
        else
        {
            _taskUpdateAppointmentCancelActionConfigService.update( config );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return I18nService.getLocalizedString( MESSAGE_CANCEL_ACTION_UPDATED, locale );
    }
}
