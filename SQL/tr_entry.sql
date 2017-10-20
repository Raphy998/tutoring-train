create or replace TRIGGER TR_ENTRY
BEFORE INSERT OR UPDATE ON ENTRY 
FOR EACH ROW
BEGIN
   IF NOT (:new.isActive = '1' OR :new.isActive = '0') THEN 
        RAISE_APPLICATION_ERROR(-20012, 'Entry isActive only 1 or 0 allowed');
   END IF;
   IF NOT (:new.flag = 'R' OR :new.flag = 'O') THEN 
        RAISE_APPLICATION_ERROR(-20013, 'Entry flag is only O or R is allowed');
   END IF;
END;