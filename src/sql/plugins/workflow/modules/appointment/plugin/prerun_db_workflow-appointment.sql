-- liquibase formatted sql
-- changeset workflow-appointment:prerun_db_workflow-appointment.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM core_datastore WHERE entity_key = 'core.plugins.status.workflow-appointment.version'
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.table_constraints WHERE constraint_schema = database() AND constraint_name = 'fk_wf_task_up_app_cancel_cf'
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM workflow_task_update_appointment_cancel_cf c LEFT JOIN workflow_action a ON a.id_action = c.id_action_cancel WHERE c.id_action_cancel IS NOT NULL AND a.id_action IS NULL
-- comment Adds the cancel action foreign key on a site where the module is installed without it
ALTER TABLE workflow_task_update_appointment_cancel_cf ADD CONSTRAINT fk_wf_task_up_app_cancel_cf FOREIGN KEY (id_action_cancel)
      REFERENCES workflow_action (id_action) ON DELETE RESTRICT ON UPDATE RESTRICT ;
