create or replace TRIGGER TR_TUSER
BEFORE INSERT OR UPDATE ON TUSER 
FOR EACH ROW
BEGIN
   IF NOT (:new.role = 'A' OR :new.role = 'M' OR :new.role = 'U') THEN 
        RAISE_APPLICATION_ERROR(-20010, 'Users role is invalid');
   END IF;
   IF NOT (:new.gender = 'M' OR :new.gender = 'F' OR :new.gender = 'N') THEN 
        RAISE_APPLICATION_ERROR(-20011, 'Users gender is invalid');
   END IF;
END;