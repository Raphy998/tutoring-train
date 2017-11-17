CREATE OR REPLACE PROCEDURE PRO_DEACTIVATE AS 
BEGIN
  Update entry set isactive = '0' where duedate <= sysdate;
END PRO_DEACTIVATE;
