ALTER TABLE workflow_task_update_appointment_cancel_cf ADD CONSTRAINT fk_wf_task_up_app_cancel_cf FOREIGN KEY (id_action_cancel)
      REFERENCES workflow_action (id_action) ON DELETE RESTRICT ON UPDATE RESTRICT ;
