-- liquibase formatted sql
-- changeset workflow-appointment:update_db_workflow_appointment_2.0.8-3.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE workflow_task_update_appointment_cancel_cf  ADD id_action_report INT DEFAULT NULL;