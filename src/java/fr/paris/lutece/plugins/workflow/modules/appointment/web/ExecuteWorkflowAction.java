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
package fr.paris.lutece.plugins.workflow.modules.appointment.web;

import fr.paris.lutece.plugins.appointment.web.AppointmentJspBean;
import fr.paris.lutece.plugins.workflow.modules.appointment.service.WorkflowAppointmentPlugin;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.util.CryptoService;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Do execute a workflow action
 */
public class ExecuteWorkflowAction
{
    // Parameters
    private static final String PARAMETER_ID_ACTION = "id_action";
    private static final String PARAMETER_ID_ADMIN_USER = "id_admin_user";
    private static final String PARAMETER_ID_RESOURCE = "id_resource";
    private static final String PARAMETER_TIMESTAMP = "timestamp";
    private static final String PARAMETER_KEY = "key";

    // Properties
    private static final String PROPERTY_LINKS_LIMIT_VALIDITY = "workflow-appointment.executeWorkflowAction.links_limit_validity";

    // JSP URL
    private static final String JSP_URL_EXECUTE_WORKFLOW_ACTION = "jsp/site/plugins/workflow/modules/appointment/DoExecuteWorkflowAction.jsp";

    // Messages
    private static final String MESSAGE_ACTION_PERFORMED = "module.workflow.appointment.message.actionPerformed";

    // Error
    private static final String ERROR_MESSAGE_ACCESS_DENIED = "portal.site.message.pageAccessDenied";

    // Constants
    private static final String DEFAULT_ENCRYPTION_ALGO = "SHA-256";
    private static final int DEFAULT_LIMIT_TIME_VALIDITY = 30;

    /**
     * Do check if the given key is valid. If it is valid, then the user is
     * redirected to a page to execute a workflow action.
     * @param request The request
     * @param response The response
     * @return The next URL to redirect to
     * @throws SiteMessageException If a site message needs to be displayed
     */
    public String doExecuteWorkflowAction( HttpServletRequest request, HttpServletResponse response )
        throws SiteMessageException
    {
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        String strIdAdminUser = request.getParameter( PARAMETER_ID_ADMIN_USER );
        String strIdResource = request.getParameter( PARAMETER_ID_RESOURCE );
        String strTimestamp = request.getParameter( PARAMETER_TIMESTAMP );
        String strKey = request.getParameter( PARAMETER_KEY );

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) &&
                StringUtils.isNotEmpty( strTimestamp ) && StringUtils.isNumeric( strTimestamp ) &&
                StringUtils.isNotEmpty( strIdResource ) && StringUtils.isNumeric( strIdResource ) &&
                StringUtils.isNotEmpty( strKey ) && StringUtils.isNotEmpty( strIdAdminUser ) &&
                StringUtils.isNumeric( strIdAdminUser ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );

            long lTimestamp = Long.parseLong( strTimestamp );
            int nIdResource = Integer.parseInt( strIdResource );

            AdminUser user = null;
            int nIdAdminUser = Integer.parseInt( strIdAdminUser );

            if ( nIdAdminUser > 0 )
            {
                user = AdminUserHome.findByPrimaryKey( nIdAdminUser );
            }

            int nLinkLimitValidity = AppPropertiesService.getPropertyInt( PROPERTY_LINKS_LIMIT_VALIDITY,
                    DEFAULT_LIMIT_TIME_VALIDITY );

            if ( nLinkLimitValidity > 0 )
            {
                Calendar calendar = GregorianCalendar.getInstance( WorkflowAppointmentPlugin.getPluginLocale( 
                            Locale.getDefault(  ) ) );
                calendar.add( Calendar.DAY_OF_WEEK, -1 * nLinkLimitValidity );

                if ( calendar.getTimeInMillis(  ) > lTimestamp )
                {
                    SiteMessageService.setMessage( request, ERROR_MESSAGE_ACCESS_DENIED, SiteMessage.TYPE_ERROR );

                    return null;
                }
            }

            String strComputedKey = computeAuthenticationKey( nIdAction, nIdAdminUser, lTimestamp, nIdResource );

            if ( ( user == null ) || !StringUtils.equals( strComputedKey, strKey ) )
            {
                SiteMessageService.setMessage( request, ERROR_MESSAGE_ACCESS_DENIED, SiteMessage.TYPE_ERROR );

                return null;
            }

            try
            {
                AdminAuthenticationService.getInstance(  ).registerUser( request, user );
            }
            catch ( AccessDeniedException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            catch ( UserNotSignedException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }

            return AppointmentJspBean.getUrlExecuteWorkflowAction( request, strIdResource, strIdAction );
        }

        SiteMessageService.setMessage( request, ERROR_MESSAGE_ACCESS_DENIED, SiteMessage.TYPE_ERROR );

        return null;
    }

    /**
     * Get the URL to execute a workflow action on an appointment
     * @param strBaseURL The base URL to use
     * @param nIdWorkflowAction The id of the workflow action to execute
     * @param nIdAdminUser The id of the action user that execute the action
     * @param nIdResource The id of the appointment to execute the workflow
     *            action on
     * @return The URL
     */
    public static String getExecuteWorkflowActionUrl( String strBaseURL, int nIdWorkflowAction, int nIdAdminUser,
        int nIdResource )
    {
        long lTimestamp = System.currentTimeMillis(  );
        UrlItem urlItem = new UrlItem( strBaseURL + JSP_URL_EXECUTE_WORKFLOW_ACTION );
        urlItem.addParameter( PARAMETER_ID_ACTION, nIdWorkflowAction );
        urlItem.addParameter( PARAMETER_ID_RESOURCE, nIdResource );
        urlItem.addParameter( PARAMETER_ID_ADMIN_USER, nIdAdminUser );
        urlItem.addParameter( PARAMETER_TIMESTAMP, Long.toString( lTimestamp ) );
        urlItem.addParameter( PARAMETER_KEY,
            computeAuthenticationKey( nIdWorkflowAction, nIdAdminUser, lTimestamp, nIdResource ) );

        return urlItem.getUrl(  );
    }

    /**
     * Compute the authentication key to execute a workflow action
     * @param nIdAction The id of the action to execute
     * @param nIdAdminUser The id of the admin user that will execute the action
     * @param nTimestamp The timestamp used when the link was created
     * @param nIdResource The id of the workflow resource
     * @return The authentication key
     */
    private static String computeAuthenticationKey( int nIdAction, int nIdAdminUser, long nTimestamp, int nIdResource )
    {
        String strPrivateKey = CryptoService.getCryptoKey(  );

        return CryptoService.encrypt( nIdAction + nIdAdminUser + nTimestamp + nIdResource + strPrivateKey,
            DEFAULT_ENCRYPTION_ALGO );
    }
}
