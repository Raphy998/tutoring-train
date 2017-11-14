CREATE OR REPLACE PROCEDURE PRO_DEACTIVATE AS 
BEGIN
  FOR entry_rec IN (
        SELECT id
          FROM ENTRY
         WHERE DUEDATE <= sysdate)
   LOOP
   UPDATE entry SET ISACTIVE = '0' WHERE ID = entry_rec.id;
   END LOOP;
END PRO_DEACTIVATE;