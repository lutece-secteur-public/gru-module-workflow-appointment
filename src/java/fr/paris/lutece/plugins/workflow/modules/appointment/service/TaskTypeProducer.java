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
package fr.paris.lutece.plugins.workflow.modules.appointment.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import fr.paris.lutece.plugins.workflowcore.business.task.TaskType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class TaskTypeProducer
{
    @Produces
    @ApplicationScoped
    @Named( "workflow-appointment.taskTypeNotifyAppointment" )
    public ITaskType produceTaskTypeNotifyAppointment(
            @ConfigProperty( name = "workflow-appointment.taskTypeNotifyAppointment.key" ) String key,
            @ConfigProperty( name = "workflow-appointment.taskTypeNotifyAppointment.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow-appointment.taskTypeNotifyAppointment.beanName" ) String beanName,
            @ConfigProperty( name = "workflow-appointment.taskTypeNotifyAppointment.configBeanName" ) String configBeanName,
            @ConfigProperty( name = "workflow-appointment.taskTypeNotifyAppointment.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow-appointment.taskTypeNotifyAppointment.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow-appointment.taskTypeNotifyAppointment.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow-appointment.taskTypeChangeAppointmentStatus" )
    public ITaskType produceTaskTypeChangeAppointmentStatus(
            @ConfigProperty( name = "workflow-appointment.taskTypeChangeAppointmentStatus.key" ) String key,
            @ConfigProperty( name = "workflow-appointment.taskTypeChangeAppointmentStatus.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow-appointment.taskTypeChangeAppointmentStatus.beanName" ) String beanName,
            @ConfigProperty( name = "workflow-appointment.taskTypeChangeAppointmentStatus.configBeanName" ) String configBeanName,
            @ConfigProperty( name = "workflow-appointment.taskTypeChangeAppointmentStatus.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow-appointment.taskTypeChangeAppointmentStatus.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow-appointment.taskTypeChangeAppointmentStatus.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow-appointment.taskTypeManualAppointmentNotification" )
    public ITaskType produceTaskTypeManualAppointmentNotification(
            @ConfigProperty( name = "workflow-appointment.taskTypeManualAppointmentNotification.key" ) String key,
            @ConfigProperty( name = "workflow-appointment.taskTypeManualAppointmentNotification.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow-appointment.taskTypeManualAppointmentNotification.beanName" ) String beanName,
            @ConfigProperty( name = "workflow-appointment.taskTypeManualAppointmentNotification.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow-appointment.taskTypeManualAppointmentNotification.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow-appointment.taskTypeManualAppointmentNotification.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return buildTaskType( key, titleI18nKey, beanName, null, configRequired, formTaskRequired, taskForAutomaticAction );
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow-appointment.taskTypeNotifyAdminAppointment" )
    public ITaskType produceTaskTypeNotifyAdminAppointment(
            @ConfigProperty( name = "workflow-appointment.taskTypeNotifyAdminAppointment.key" ) String key,
            @ConfigProperty( name = "workflow-appointment.taskTypeNotifyAdminAppointment.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow-appointment.taskTypeNotifyAdminAppointment.beanName" ) String beanName,
            @ConfigProperty( name = "workflow-appointment.taskTypeNotifyAdminAppointment.configBeanName" ) String configBeanName,
            @ConfigProperty( name = "workflow-appointment.taskTypeNotifyAdminAppointment.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow-appointment.taskTypeNotifyAdminAppointment.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow-appointment.taskTypeNotifyAdminAppointment.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow-appointment.taskTypeUpdateAppointmentCancelAction" )
    public ITaskType produceTaskTypeUpdateAppointmentCancelAction(
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAppointmentCancelAction.key" ) String key,
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAppointmentCancelAction.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAppointmentCancelAction.beanName" ) String beanName,
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAppointmentCancelAction.configBeanName" ) String configBeanName,
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAppointmentCancelAction.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAppointmentCancelAction.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAppointmentCancelAction.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow-appointment.taskTypeUpdateAppointment" )
    public ITaskType produceTaskTypeUpdateAppointment(
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAppointment.key" ) String key,
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAppointment.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAppointment.beanName" ) String beanName,
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAppointment.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAppointment.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAppointment.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return buildTaskType( key, titleI18nKey, beanName, null, configRequired, formTaskRequired, taskForAutomaticAction );
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow-appointment.taskTypeReportAppointment" )
    public ITaskType produceTaskTypeReportAppointment(
            @ConfigProperty( name = "workflow-appointment.taskTypeReportAppointment.key" ) String key,
            @ConfigProperty( name = "workflow-appointment.taskTypeReportAppointment.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow-appointment.taskTypeReportAppointment.beanName" ) String beanName,
            @ConfigProperty( name = "workflow-appointment.taskTypeReportAppointment.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow-appointment.taskTypeReportAppointment.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow-appointment.taskTypeReportAppointment.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return buildTaskType( key, titleI18nKey, beanName, null, configRequired, formTaskRequired, taskForAutomaticAction );
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow-appointment.taskTypeUpdateAdminAppointment" )
    public ITaskType produceTaskTypeUpdateAdminAppointment(
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAdminAppointment.key" ) String key,
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAdminAppointment.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAdminAppointment.beanName" ) String beanName,
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAdminAppointment.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAdminAppointment.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow-appointment.taskTypeUpdateAdminAppointment.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return buildTaskType( key, titleI18nKey, beanName, null, configRequired, formTaskRequired, taskForAutomaticAction );
    }

    private ITaskType buildTaskType( String strKey, String strTitleI18nKey, String strBeanName, String strConfigBeanName,
            boolean bIsConfigRequired, boolean bIsFormTaskRequired, boolean bIsTaskForAutomaticAction )
    {
        TaskType taskType = new TaskType( );
        taskType.setKey( strKey );
        taskType.setTitleI18nKey( strTitleI18nKey );
        taskType.setBeanName( strBeanName );
        taskType.setConfigBeanName( strConfigBeanName );
        taskType.setConfigRequired( bIsConfigRequired );
        taskType.setFormTaskRequired( bIsFormTaskRequired );
        taskType.setTaskForAutomaticAction( bIsTaskForAutomaticAction );
        return taskType;
    }
}
