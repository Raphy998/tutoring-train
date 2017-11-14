create or replace PROCEDURE PRO_TSESSION AS 
BEGIN
  delete from tsession where EXPIRYDATE <= sysdate;
  commit;
END PRO_TSESSION;
