with Ada.Text_IO; use Ada.Text_IO;
with Ada.Characters.Latin_1; use Ada.Characters.Latin_1;

procedure Main is
   N: Integer := 4;
   P: Integer := 8;
   H: Integer := N/P;
   Cnst: Integer := 2;

   type Vector is array(Integer range <>) of Integer;
   type Matrix is array(Integer range <>, Integer range <>) of Integer;

   procedure outputVector(V : in Vector) is
   begin
      Put ("[");
      for i in V'Range(1) loop
         Put(Item => Integer'Image(V(i)) & " ");
      end loop;
      Put_Line("]");
   end outputVector;


   procedure outputMatrix(M : in Matrix) is
   begin
      for i in M'Range(1) loop
         Put ("    [");
         for j in M'Range(2) loop
            Put(Item => Integer'Image(M(i, j)) & " ");
         end loop;
         Put_Line("]");
      end loop;
   end outputMatrix;


   function createVector return Vector is
      vector_buf: Vector(1..N);
      begin
         for j in 1 .. N loop
            vector_buf(j) := Cnst;
         end loop;
         return vector_buf;
      end createVector;

   function createMatrix return Matrix is
      matrix_buf: Matrix(1..N, 1..N);
      begin
            for i in 1 .. N loop
               for j in 1 .. N loop
                  matrix_buf(i, j) := Cnst;
               end loop;
            end loop;
         return matrix_buf;
      end createMatrix;

   --  (N x N) -> (N x H)
   function getMatrixChunk(MO: Matrix; chunkNum: Integer) return Matrix is
      M: Matrix(1..N, 1..H);
   begin
      for i in 1 .. N loop
         for j in 1 .. H loop
            M(i, j) := MO(i, H * chunkNum + j);
         end loop;
      end loop;
      return M;
   end getMatrixChunk;

   function getVectorChunk(VO: Vector; chunkNum: Integer) return Vector is
         V: Vector(1..H);
      begin
         for i in 1 .. H loop
            V(i) := VO(H * chunkNum + i);
         end loop;
         return V;
      end getVectorChunk;

   procedure insertVectorChunk(VO: out Vector; V: Vector; chunkNum: Integer) is
      begin
         for i in 1 .. H loop
            VO(H * chunkNum + i) := V(i);
         end loop;
      end insertVectorChunk;

   function min(Vec: Vector) return Integer is
      Result : Integer;
   begin
      Result := Vec(1);
      for i in Vec'Range(1) loop
         if Vec(i) < Result then
            Result := Vec(i);
         end if;
      end loop;
      return Result;
   end min;

   -- (H x N) x (N x 1) -> (H x 1)
   function vectorByMatrix(Vec: Vector; Mat: Matrix) return Vector is
         V: Vector(Mat'Range(1));
         sum: Integer;
      begin
         for i in Mat'Range(1) loop
            sum := 0;
            for j in Vec'Range(1) loop
               sum := sum + Vec(j) * Mat(i, j);
            end loop;
            V(i) := sum;
         end loop;
         return V;
      end vectorByMatrix;

   function vectorByVector(Vec0: Vector; Vec1: Vector) return Integer is
         sum: Integer;
      begin
         sum := 0;
         for i in Vec0'Range(1) loop
            sum := sum + Vec0(i) * Vec1(i);
         end loop;
         return sum;
      end vectorByVector;

   function matrixByMatrix(Mat1, Mat2: Matrix) return Matrix is
      Result : Matrix(Mat1'Range(1), Mat2'Range(2));
   begin
      for i in Mat1'Range(1) loop
         for j in Mat2'Range(2) loop
            Result(i, j) := 0;
            for k in Mat1'Range(2) loop
               Result(i, j) := Result(i, j) + Mat1(j, k) * Mat2(k, j);
            end loop;
         end loop;
      end loop;
      return Result;
   end matrixByMatrix;

   function transpose(Mat: Matrix) return Matrix is
      Result : Matrix(Mat'Range(2), Mat'Range(1));
   begin
      for i in Mat'Range(1) loop
         for j in Mat'Range(2) loop
            Result(j, i) := Mat(i, j);
         end loop;
      end loop;
      return Result;
   end transpose;

   function vectorPlusVector (Vec1, Vec2: Vector) return Vector is
      Result : Vector(Vec1'Range(1));
   begin
      for i in Vec1'Range(1) loop
         Result(i) := Vec1(i) + Vec2(i);
      end loop;
      return Result;
   end vectorPlusVector;

   function scalarByVector (Scalar: Integer; Vec: Vector) return Vector is
      Result : Vector(Vec'Range(1));
   begin
      for i in Vec'Range(1) loop
         Result(i) := Vec(i)*Scalar;
      end loop;
      return Result;
   end scalarByVector;

   function computeAi(D: Vector; chunkNum: Integer) return Integer is
   begin
      return min(getVectorChunk(D, chunkNum));
   end computeAi;

   function computeBi(B: Vector; C: Vector; chunkNum: Integer) return Integer is
   begin
      return vectorByVector(
         getVectorChunk(B, chunkNum),
         getVectorChunk(C, chunkNum)
      );
   end computeBi;

   function computeMAh(MX: Matrix; MR: Matrix; chunkNum: Integer) return Matrix is
      MRh: Matrix(1..N, 1..H);
      MXxMRh: Matrix(1..N, 1..H);
   begin
      MRh := getMatrixChunk(MR, chunkNum);
      MXxMRh := matrixByMatrix(MX, MRh);
      return transpose(MXxMRh);
   end computeMAh;

   function computeAh(bi: Integer; Z: Vector; D: Vector; MAh: Matrix; ai: Integer; chunkNum: Integer) return Vector is
      Zh: Vector(1..H);
      left: Vector(1..H);
      DxMAh: Vector(1..H);
      right: Vector(1..H);
   begin
      Zh := getVectorChunk(Z, chunkNum);
      left := scalarByVector(bi, Z);
      DxMAh := vectorByMatrix(D, MAh);
      right := scalarByVector(ai, DxMAh);

      return vectorPlusVector(left, right);
   end computeAh;

   
   type T;
   
   type TRef is access T;
   
   type TaskStack is array(Integer range <>) of TRef;
   
   task type T1 is
      entry init(newTasks: TaskStack);
      entry submit_Z_D_C_MR(newZ, newD, newC: in Vector; newMR: in Matrix);
      entry submit_a_b(newA: in Integer; newB: in Integer);
      entry submit_A(newA: in Vector);
   end T1;
   
   type T1Ref is access T1;
   
   task type TP is
      entry init(newTasks: TaskStack);
      entry submit_MX_B(newMX: in Matrix; newB: in Vector);
      entry submit_ai(newA: in Integer);
      entry submit_bi(newB: in Integer);
   end TP;
   type TPRef is access TP;
   
   task type T is 
      entry init(newNum: in Integer;  newTasks: in TaskStack; f : in T1Ref; l: in TPRef);
      entry submit_MX_B(newMX: in Matrix; newB: in Vector);
      entry submit_Z_D_C_MR(newZ, newD, newC: in Vector; newMR: in Matrix);
      entry submit_ai(newA: in Integer);
      entry submit_bi(newB: in Integer);
      entry submit_a_b(newA: in Integer; newB: in Integer);
      entry submit_A(newA: in Vector);
   end T;
   

   
   task body T is
      num: Integer;
      tasks: TaskStack(1..P-2);
      first: T1Ref;
      last: TPRef;

   
      ai: Integer;
      aiSmaller: Integer;
      
      bi: Integer;
      biPlus: Integer;
      
      minA: Integer;
      sumB: Integer;
      
      MX: Matrix(1..N, 1..N);
      MR: Matrix(1..N, 1..N);
      A: Vector(1..N);
      Ah: Vector(1..H);
      MAh: Matrix(1..N, 1..H);
      
      Z: Vector(1..N);
      D: Vector(1..N);
      C: Vector(1..N);
      B: Vector(1..N);

   begin
      
      accept init(newNum: in Integer;  newTasks: in TaskStack; f : in T1Ref; l: in TPRef) do
         num := newNum;
         tasks := newTasks;
         first := f;
         last := l;
      end init;
      
      Put_Line("Launched  T" & Integer'Image(num));
      ---
      accept submit_MX_B(newMX: in Matrix; newB: in Vector) do
         Put_Line("Got MX_B:" & Integer'Image(num));
         MX := newMx;
         B := newB;
      end submit_MX_B;
      
      if (num = P - 2) then
         Put_Line("submitting MX,B to the last one");
         last.submit_MX_B(MX, B);
      else 
         Put_Line("submitting MB,B from" & Integer'Image(num) & " to " & Integer'Image(num+1));
         Tasks(num+1).submit_MX_B(MX,B);
      end if;
      
      
      ---
      accept submit_Z_D_C_MR(newZ, newD, newC: in Vector; newMR: in Matrix) do
             Put_Line("Got Z_D_C_MR: "& Integer'Image(num));
             Z := newZ;
             D := newD;
             C := newC;
             MR := newMR;
      end submit_Z_D_C_MR;
      
      if (num = 1) then
         Put_Line("submitting Z,D,C,MR to the first one");
         first.submit_Z_D_C_MR(Z,D,C,MR);
      else 
         Put_Line("submitting Z,D,C,MR from" & Integer'Image(num) & " to " & Integer'Image(num-1));
         Tasks(num-1).submit_Z_D_C_MR(Z,D,C,MR);
      end if;
      
      ---
      ai := computeAi(D, num);
      Put_Line("Calculated a"&Integer'Image(num) & " = " & Integer'Image(ai));
      
      accept submit_ai(newA: in Integer) do
         Put_Line("Got 'a' from previous"&newA);
         aiSmaller := Integer'Min(ai, newA);
      end submit_ai;
      
      Put_Line("Submitting min" & aiSmaller);
      if (num = P - 2) then
         Put_Line("submitting ai to the last one");
         last.submit_ai(aiSmaller);
      else 
         Put_Line("submitting ai from" & Integer'Image(num) & " to " & Integer'Image(num+1));
         Tasks(num+1).submit_ai(aiSmaller);
      end if;
      
      
      ---
      bi := computeBi(B, C, num);
      Put_Line("Calculated b"&Integer'Image(num) & " = " & Integer'Image(bi));
      
      accept submit_bi(newB: in Integer) do
         Put_Line("Got 'b' from previous"&newB);
         biPlus := bi + newB;
      end submit_bi;
      
      Put_Line("Submitting sum" & biPlus);
      if (num = P - 2) then
         Put_Line("submitting bi to the last one");
         last.submit_bi(biPlus);
      else 
         Put_Line("submitting bi from" & Integer'Image(num) & " to " & Integer'Image(num+1));
         Tasks(num+1).submit_bi(biPlus);
      end if;

      
      --
      accept submit_a_b(newA: in Integer; newB: in Integer) do
         Put_Line("Got a and b: "& Integer'Image(num));
         minA:=newA;
         sumB:=newB;
      end submit_a_b;
      
      Put_Line("Submitting a and b" & a & b);
       if (num = 1) then
         Put_Line("submitting a&b to the first one");
         first.submit_a_b(a,b);
      else 
         Put_Line("submitting a&b from" & Integer'Image(num) & " to " & Integer'Image(num-1));
         Tasks(num-1).submit_a_b(a,b);
      end if;
      
      ---
      MAh := computeMAh(MX, MR, num);
      Ah := computeAh(sumB, Z, D, MAh, minA, num);
     
      
      accept submit_A(newA: in Vector) do
         Put_Line("Got Ah: "& Integer'Image(num));
         A := newA;
      end submit_A;
        
      insertVectorChunk(A, Ah, num);
      
       if (num = 1) then
         Put_Line("submitting Ah to the first one");
         first.submit_A(A);
      else 
         Put_Line("submitting Ah from" & Integer'Image(num) & " to " & Integer'Image(num-1));
         Tasks(num-1).submit_A(A);
      end if;
      
      ---
      Put_Line("Done" & Integer'Image(num));
      
   end T;
   
   
  task body T1 is
      num: Integer:=0; 
      tasks: TaskStack(1..P-2);
      
      ai: Integer;
      bi: Integer;
      
      minA: Integer;
      sumB: Integer;
      MAh: Matrix(1..N, 1..H);
      MX: Matrix(1..N, 1..N);
      MR: Matrix(1..N, 1..N);
      A: Vector(1..N);
      Ah: Vector(1..H);
      Z: Vector(1..N);
      D: Vector(1..N);
      C: Vector(1..N);
      B: Vector(1..N);

   begin
      
      accept init(newTasks: TaskStack) do
         tasks := newTasks;
      end init;
      
      Put_Line("Launched  T0");
      
      ---
      MX := createMatrix;
      B := createVector;
      Tasks(num+1).submit_MX_B(MX, B);
      
      ---
      accept submit_Z_D_C_MR(newZ, newD, newC: in Vector; newMR: in Matrix) do
             Put_Line("Got Z_D_C_MR: "& Integer'Image(num));
             Z := newZ;
             D := newD;
             C := newC;
             MR := newMR;
      end submit_Z_D_C_MR;
      
      
      ---
      ai := computeAi(D, num);
      Put_Line("Calculated a"&Integer'Image(num) & " = " & Integer'Image(ai));
      
      Put_Line("submitting ai from" & Integer'Image(num) & " to " & Integer'Image(num+1));
      Tasks(num+1).submit_ai(ai);
      
      
      ---
      bi := computeBi(B, C, num);
      Put_Line("Calculated b"&Integer'Image(num) & " = " & Integer'Image(bi));

      Put_Line("submitting bi from" & Integer'Image(num) & " to " & Integer'Image(num+1));
      Tasks(num+1).submit_bi(bi);

      
      --
      accept submit_a_b(newA: in Integer; newB: in Integer) do
         Put_Line("Got a and b: "& Integer'Image(num));
         minA:=newA;
         sumB:=newB;
      end submit_a_b;
      
      ---
      MAh := computeMAh(MX, MR, num);
      Ah := computeAh(sumB, Z, D, MAh, minA, num);
     
      
      accept submit_A(newA: in Vector) do
         Put_Line("Got Ah: "& Integer'Image(num));
         A := newA;
      end submit_A;
        
      insertVectorChunk(A, Ah, num);
      
      outputMatrix(A);
      
      Put_Line("Done" & Integer'Image(num));
  
   end T1;
   
     task body TP is
      num: Integer := P - 1;
      tasks: TaskStack(1..P-2);
      
      ai: Integer;
      bi: Integer;
      
      minA: Integer;
      sumB: Integer;
      
      MX: Matrix(1..N, 1..N);
      MR: Matrix(1..N, 1..N);
      MAh: Matrix(1..N, 1..H);
      A: Vector(1..N);
      Ah: Vector(1..H);
      Z: Vector(1..N);
      D: Vector(1..N);
      C: Vector(1..N);
      B: Vector(1..N);

   begin
      
      accept init(newTasks: TaskStack) do
         tasks := newTasks;
      end init;
      
      Put_Line("Launched  T" & Integer'Image(num));
     
      ---
     accept submit_MX_B(newMX: in Matrix; newB: in Vector) do
     Put_Line("Got MX_B:" & Integer'Image(num));
         MX := newMx;
         B := newB;
      end submit_MX_B;
      
      ---
      Z := createVector;
      D := createVector;
      C := createVector;
      MR := createMatrix;
      
     Put_Line("Submitting Z,D,C,MR to penultimate task");
      Tasks(num-1).submit_Z_D_C_MR(Z, D, C, MR);
      
      
      ---
      ai := computeAi(D, num);
      Put_Line("Calculated a"&Integer'Image(num) & " = " & Integer'Image(ai));
      
      accept submit_ai(newA: in Integer) do
         Put_Line("Got 'a' from previous"&newA);
         minA := Integer'Min(ai, newA);
      end submit_ai;
      
      
      
      ---
      bi := computeBi(B, C, num);
      Put_Line("Calculated b"&Integer'Image(num) & " = " & Integer'Image(bi));
      
      accept submit_bi(newB: in Integer) do
         Put_Line("Got 'b' from previous"&newB);
         sumB := bi + newB;
      end submit_bi;
      

      Put_Line("Submitting a&b" & minA & " " & sumB);
      Tasks(num-1).submit_a_b(minA, sumB);

      ---
      MAh := computeMAh(MX, MR, num);
      Ah := computeAh(sumB, Z, D, MAh, minA, num);
        
      insertVectorChunk(A, Ah, num);
      
  
      Put_Line("submitting Ah from" & Integer'Image(num) & " to " & Integer'Image(num-1));
      Tasks(num-1).submit_A(A);
      
      
      ---
      Put_Line("Done T" & Integer'Image(num));
      
   end TP;
   

      
   Tasks : TaskStack(1..P-2);
   Task1: T1Ref := new T1;
   TaskP: TPRef := new TP;
   
begin
   
   for i in Tasks'Range(1) loop
      Tasks(i) := new T;
   end loop;
   
   Task1.init(Tasks);
   TaskP.init(Tasks);

   for i in Tasks'Range(1) loop
      Tasks(i).init(i, Tasks, Task1, TaskP);
   end loop;
end Main;
