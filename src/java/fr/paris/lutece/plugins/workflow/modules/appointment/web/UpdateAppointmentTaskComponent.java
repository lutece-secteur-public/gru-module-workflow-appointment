/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.appointment.business.message.FormMessage;
import fr.paris.lutece.plugins.appointment.service.AppointmentResponseService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.AppointmentUtilities;
import fr.paris.lutece.plugins.appointment.service.EntryService;
import fr.paris.lutece.plugins.appointment.service.FormMessageService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.workflow.web.task.NoConfigTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 *
 * NotifyAppointmentTaskComponent
 *
 */
public class UpdateAppointmentTaskComponent extends NoConfigTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_FORM_BO = "admin/plugins/workflow/modules/appointment/task_update_appointment.html";
    private static final String TEMPLATE_TASK_FORM_FO = "skin/plugins/workflow/modules/appointment/task_update_appointment.html";

    // MESSAGES
    private static final String MESSAGE_ERROR = "module.workflow.appointment.error.task.update.appointment";

    // MARKS
   
    private static final String MARK_LOCALE = "locale";
    private static final String PARAMETER_DATE_OF_DISPLAY = "date_of_display";
    private static final String MARK_APPOINTMENT = "appointment";
    private static final String MARK_STR_ENTRY = "str_entry";
    private static final String MARK_FORM_MESSAGES = "formMessages";
    private static final String MARK_FORM = "form";



    // PARAMETERS

	private static final String PARAMETER_EMAIL = "email";
	private static final String PARAMETER_EMAIL_CONFIRMATION = "emailConfirm";
	private static final String PARAMETER_ID_FORM = "id_form";
	private static final String PARAMETER_ID_APPOINTMENT = "id_appointment";



    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap< >( );

       
        AppointmentDTO appointmentDTO = AppointmentService.buildAppointmentDTOFromIdAppointment( nIdResource );
        AppointmentFormDTO form = FormService.buildAppointmentForm( appointmentDTO.getIdForm( ), 0, 0 );
        FormMessage formMessages = FormMessageService.findFormMessageByIdForm( form.getIdForm( ) );

        appointmentDTO.setListResponse( AppointmentResponseService.findAndBuildListResponse( nIdResource, request ) );
        appointmentDTO.setMapResponsesByIdEntry( AppointmentResponseService.buildMapFromListResponse( appointmentDTO.getListResponse( ) ) );
        List<Entry> listEntryFirstLevel = EntryService.getFilter( form.getIdForm( ), true );
        StringBuilder strBuffer = new StringBuilder( );
        for ( Entry entry : listEntryFirstLevel )
        {
            EntryService.getHtmlEntry( model, entry.getIdEntry( ), strBuffer, locale, false, request );
        }
        model.put( MARK_STR_ENTRY, strBuffer.toString( ) );
        model.put( MARK_FORM, form );
        model.put( MARK_LOCALE, locale );
        model.put( MARK_FORM_MESSAGES, formMessages );
        model.put( MARK_APPOINTMENT, appointmentDTO );
        
        model.put( PARAMETER_DATE_OF_DISPLAY, appointmentDTO.getSlot( ).get( 0 ).getDate( ) );
        HtmlTemplate template = null;
    	User user = AdminUserService.getAdminUser( request );
    	if (user == null) {
    		try {
    		
    			template = AppTemplateService.getTemplate( TEMPLATE_TASK_FORM_FO, locale, model );
    			user = SecurityService.getInstance( ).getRemoteUser( request );
			
    		} catch (UserNotSignedException e) {
				
				AppLogService.error(e.getMessage( ), e);
				return null;
			}
    	} else {
    		
    		template = AppTemplateService.getTemplate( TEMPLATE_TASK_FORM_BO, locale, model );
    	}

        return template.getHtml( );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        List<GenericAttributeError> listFormErrors = new ArrayList< >( );

        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String strIdAppointment = request.getParameter( PARAMETER_ID_APPOINTMENT );

        AppointmentDTO appointmentDTO = AppointmentService.buildAppointmentDTOFromIdAppointment( Integer.parseInt( strIdAppointment ) );

        AppointmentFormDTO appointmentForm = FormService.buildAppointmentForm( Integer.parseInt(strIdForm ), 0, 0 );

        String strEmail = request.getParameter( PARAMETER_EMAIL );
        AppointmentUtilities.checkEmail( strEmail, request.getParameter( PARAMETER_EMAIL_CONFIRMATION ), appointmentForm, locale, listFormErrors );
        AppointmentUtilities.validateFormAndEntries( appointmentDTO, request, listFormErrors );

        if ( CollectionUtils.isNotEmpty( listFormErrors ) )
        {
        	if ( AdminUserService.getAdminUser( request ) != null )
            {
        		
               return buildErrorUrl(listFormErrors, request);
            }
            else
            {

                 return listFormErrors.get(0).getErrorMessage( );
            }

        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }
    
    /**
     * Builds the error URL
     * 
     * @param error
     *            the error
     * @param request
     *            the request
     * @return the error URL
     */
    private String buildErrorUrl( List<GenericAttributeError> listError, HttpServletRequest request )
    {
    	int i=0;
        Object [ ] listMessageParameters = new Object[listError.size()];
        for( GenericAttributeError error:listError ) {
        	
        	listMessageParameters[i]=error.getErrorMessage( );
        	i++;
        }
        return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR, listMessageParameters, AdminMessage.TYPE_STOP );
    }

}
