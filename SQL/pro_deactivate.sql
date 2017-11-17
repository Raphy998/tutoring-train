create or replace PROCEDURE PRO_DEACTIVATE AS 
BEGIN
  Update entry set isactive = '0' where duedate <= sysdate;
  commit;
END PRO_DEACTIVATE;
