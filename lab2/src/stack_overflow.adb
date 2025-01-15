with Ada.Text_IO; use Ada.Text_IO;
with Ada.Characters.Latin_1; use Ada.Characters.Latin_1;

procedure activate is
   
   -- The number of elements
   -- 508 ok
   -- 509 long execution + -1073741819;
   -- 510 raised STORAGE_ERROR : EXCEPTION_STACK_OVERFLOW
   -- 2 matrices 510 x 510 and 510 x 510 = 260100*2 = 520200
   
   
   -- The number of elements
   -- 719 ok
   -- 720 long execution + -1073741819;
   -- 721 raised STORAGE_ERROR : EXCEPTION_STACK_OVERFLOW
   -- 1 matrices 721 x 721 = 519841
   
   
   -- The number of elements
   -- 510000 ok
   -- 519679 long execution + -1073741819;
   -- 519680 raised STORAGE_ERROR : EXCEPTION_STACK_OVERFLOW
   -- 1 vector 519680
   N: Integer := 519679;


   type Vector is array(Integer range <>) of Integer;
   
   task type T0 is end T0;
   type T0Ref is access T0;
   
     task body T0 is
   begin
      Put_line("s");
   end T0;
   

   MX: Vector(1..N);
   

begin
   Put_Line("first"&Integer'Image(1));
end activate;
