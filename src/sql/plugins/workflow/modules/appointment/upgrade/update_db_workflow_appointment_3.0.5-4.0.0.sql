-- liquibase formatted sql
-- changeset workflow-appointment:update_db_workflow_appointment_3.0.5-4.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN

DROP TABLE IF EXISTS workflow_task_notify_appointment_crm;
