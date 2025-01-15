with Ada.Text_IO; use Ada.Text_IO;
with Ada.Characters.Latin_1; use Ada.Characters.Latin_1;

procedure activate is
   
   -- The number of elements
   -- 508 ok
   -- 509 long execution + -1073741819 exit code
   -- 510 failed to activate (during task startup)
   -- 2 matrices 510 x 510 and 510 x 510
   N: Integer := 510;

   -- Fill value for the matrices and vectors
   Cnst: Integer := 2;

   type Matrix is array(Integer range <>, Integer range <>) of Integer;


   -- Create a Matrix filled by the fill value
   function createMatrix return Matrix is
      matrix_buf: Matrix(1..N, 1..N);
   begin
         return matrix_buf;
      end createMatrix;
   
      task type T0 is end T0;
   
   
   type T0Ref is access T0;
   
     task body T0 is
   
      MX: Matrix(1..N, 1..N);
      MR: Matrix(1..N, 1..N);
   
   
   begin
      Put_line("t0");
   
      MR := createMatrix;
      MX := createMatrix;
   end T0;
   
   T0_HEAD: T0Ref;
   
begin
   Put_Line("hello1"&Integer'Image(N) );
   Put_Line("hello2"&Integer'Image(N) );
   T0_HEAD := new T0;
end activate;
