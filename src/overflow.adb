with Ada.Text_IO; use Ada.Text_IO;
with Ada.Characters.Latin_1; use Ada.Characters.Latin_1;


procedure overflow is
   
   
      -- The number of elements
   -- 510000 ok
   -- 519680 long execution + -1073741819;
   -- 519681 raised STORAGE_ERROR : EXCEPTION_STACK_OVERFLOW
      -- 1 array of integer 519679 = 519 841
      -- plus task
      
   
   
       -- The number of elements
   -- 510000 ok
   -- 519680 long execution + -1073741819;
   -- 519681000 raised STORAGE_ERROR : EXCEPTION_STACK_OVERFLOW
      -- 1 array of integer 519679 = 519 841
       -- plus task
       
   type Vector is array(Integer range <>) of Integer;
   type VectorA is access Vector;
   N: Integer := 519681000;
   
   --  task type T0 is end T0;
   --  
   --    task body T0 is
   --  begin
   --     Put_line("s");
   --  end T0;
   

   MX: VectorA(1..N) := new Vector(1..N);

begin
   Put_Line("first"&Integer'Image(1));
end overflow;
