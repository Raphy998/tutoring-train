create or replace TRIGGER TR_SESSION 
BEFORE INSERT ON TSESSION
FOR EACH ROW
DECLARE
    v_count number;
BEGIN
    select count(*) into v_count from tsession where username = :new.username;
    IF ( v_count = 5) then
        delete from tsession where username = :new.username and EXPIRYDATE = (select min(EXPIRYDATE) from TSESSION where username = :new.username);
    end if;
END;
