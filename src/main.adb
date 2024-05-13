with Ada.Text_IO; use Ada.Text_IO;
with Ada.Characters.Latin_1; use Ada.Characters.Latin_1;

procedure Main is
   -- The number of elements
   N: Integer := 16;
   -- The number of tasks
   P: Integer := 16;
   -- The size of a chunk to process for each task
   H: Integer := N/P;
   -- Fill value for the matrices and vectors
   Cnst: Integer := 16;

   ---- Helper procedures for calculations ----

   -- Define type for the Vector and Matrix, used in computations
   type Vector is array(Integer range <>) of Integer;
   type Matrix is array(Integer range <>, Integer range <>) of Integer;

   -- Print out the given vector
   procedure outputVector(V : in Vector) is
   begin
      Put ("[");
      for i in V'Range(1) loop
         Put(Item => Integer'Image(V(i)) & " ");
      end loop;
      Put_Line("]");
   end outputVector;


   -- Create a Vector filled by the fill value
   function createVector return Vector is
      vector_buf: Vector(1..N);
      begin
         for j in 1 .. N loop
            vector_buf(j) := Cnst;
         end loop;
         return vector_buf;
      end createVector;

   -- Create a Matrix filled by the fill value
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
   -- Get a chunk (of size N x H) of a matrix based on the given chunk num. 
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

   -- (N x 1) -> (H x 1)
   -- Get a chunk (of size H) of a vector based on the given chunk num
   function getVectorChunk(VO: Vector; chunkNum: Integer) return Vector is
         V: Vector(1..H);
      begin
         for i in 1 .. H loop
            V(i) := VO(H * chunkNum + i);
         end loop;
         return V;
      end getVectorChunk;

   -- Insert the given chunk(V) into the given Vector (VO) based on the given chunk num
   procedure insertVectorChunk(VO: out Vector; V: Vector; chunkNum: Integer) is
      begin
         for i in 1 .. H loop
            VO(H * chunkNum + i) := V(i);
         end loop;
      end insertVectorChunk;

   -- Calculate the minimum of the vector
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
   -- Multiple given matrix by a vector
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

   -- (N x 1) x (N x 1) -> (N x 1)
   -- Multiply given vectors
   function vectorByVector(Vec0: Vector; Vec1: Vector) return Integer is
         sum: Integer;
      begin
         sum := 0;
         for i in Vec0'Range(1) loop
            sum := sum + Vec0(i) * Vec1(i);
         end loop;
         return sum;
      end vectorByVector;

   -- Multiple given matrices
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

   -- (N x H) -> (H x N)
   -- Transpose the given matrix
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

   -- Sum the vector values
   function vectorPlusVector (Vec1, Vec2: Vector) return Vector is
      Result : Vector(Vec1'Range(1));
   begin
      for i in Vec1'Range(1) loop
         Result(i) := Vec1(i) + Vec2(i);
      end loop;
      return Result;
   end vectorPlusVector;


   -- Multiple given vector by a scalar
   function scalarByVector (Scalar: Integer; Vec: Vector) return Vector is
      Result : Vector(Vec'Range(1));
   begin
      for i in Vec'Range(1) loop
         Result(i) := Vec(i)*Scalar;
      end loop;
      return Result;
   end scalarByVector;


   ---- Helper procedures/functions used by tasks to compute a given formula ----

   --- a_i = min(Dh)
   function computeAi(D: Vector; chunkNum: Integer) return Integer is
   begin
      return min(getVectorChunk(D, chunkNum));
   end computeAi;

   --- b_i = Bh x Ch
   function computeBi(B: Vector; C: Vector; chunkNum: Integer) return Integer is
   begin
      return vectorByVector(
         getVectorChunk(B, chunkNum),
         getVectorChunk(C, chunkNum)
      );
   end computeBi;

   --- MAh = (MX x MRh)^T
   --- The result is additionally transposed
   function computeMAh(MX: Matrix; MR: Matrix; chunkNum: Integer) return Matrix is
      MRh: Matrix(1..N, 1..H);
      MXxMRh: Matrix(1..N, 1..H);
      Transposed: Matrix(1..H, 1..N);
   begin
      MRh := getMatrixChunk(MR, chunkNum);
      MXxMRh := matrixByMatrix(MX, MRh);
      Transposed := transpose(MXxMRh);
      return Transposed;
   end computeMAh;

   -- Ah = (b_i x Zh) + (D x MAh x a_i)
   function computeAh(bi: Integer; Z: Vector; D: Vector; MAh: Matrix; ai: Integer; chunkNum: Integer) return Vector is
      Zh: Vector(1..H);
      left: Vector(1..H);
      DxMAh: Vector(1..H);
      right: Vector(1..H);
   begin
      Zh := getVectorChunk(Z, chunkNum);
      left := scalarByVector(bi, Zh);
      
      DxMAh := vectorByMatrix(D, MAh);    -- (H x N) * (N x 1)
      right := scalarByVector(ai, DxMAh);

      return vectorPlusVector(left, right);
   end computeAh;

   ---- Task definitions ----

   ---- There are 3 different task types used to implement the linear model
   ---- 1. T0 is the head of the task chain  (Creating MX, B + Outputing the result A)
   ---- 2. TP is the tail of the task chain  (Creating Z,D,C,MR)
   ---- 3. T is an inner node within the task chain 

   ---- Communication diagram 
   ---- (For example, 8 tasks. T0 - head, TP - tail. T1 - next after the head. T6 the task before the last TP)

   ---- 1. [T0] ->     MX,B     -> [T1] ->     MX,B     -> ... -> [T6] ->     MX,B     -> [TP]   # T0 creats MX, B and propagates chainwise until TP     [-> = submit_MX_B]
   ---- 2. [T0] <-   Z,D,C,MR   <- [T1] <-   Z,D,C,MR   <- ... <- [T6] <-   Z,D,C,MR   <- [TP]   # TP creates Z,D,C,MR and propagates chainwise until T0 [<- = submit_Z_D_C_MR]
   ---- 3. [T0] ->      a0      -> [T1] ->  min(a0,a1)  -> ... -> [T6] ->  min(a5,a6)  -> [TP]   # T0 computes a0 and propagates. Each next task computes ai, receives a(i-1) and propagates min(ai, a(i-1)). TP has min(a0, a1...aP) in the end [-> = submit_ai]
   ---- 4. [T0] ->      b0      -> [T1] ->    b0 + b1   -> ... -> [T6] ->    b0 + b1   -> [TP]   # T0 computes b0 and propagates. Each next task computes bi, receives b(i-1) and propagates bi + b(i-1). TP has (b0+b1...bP) in the end. [-> = submit_bi]
   ---- 5. [T0] <-     a, b     <- [T1] <-     a, b     <- ... <- [T6] <-     a, b     <- [TP]   # TP has both min(a0, a1...aP) and (b0+b1...bP). TP Propagates both values chainwise to the downstream until T0 [<- = submit_a_b]
   ---- 6. [T0] <-      Ah      <- [T1] <-      Ah      <- ... <- [T6] <-      Ah      <- [TP]   # TP computes Ah, inserts it to the empty matrix (A) and propagates. Each next task computes Ah, inserts it to the matrix (A) received from the previous. T0 has whole matrix A in the end. [<- = submit_A]
 

   -- [T] The type of the inner nodes in the task chain (i.e. T1..T(P-1)). The actual definition follows later. 
   type T;

   -- The type of the reference to T (needed for definition to reference itself)
   type TRef is access T;

   -- The type for an array of references to the inner nodes in the task chain (array of T kinda...). It will contain all the inner nodes
   type TaskStack is array(Integer range <>) of TRef;
   
   
   ---[T0] The type for the head task of the chain.
   task type T0 is
      -- Helper entry to provide this task with a reference to the chain of tasks. Not included in the logic of parallel processing.
      entry init(newTasks: TaskStack);

      -- Actual entries for message passing
      entry submit_Z_D_C_MR(newZ, newD, newC: in Vector; newMR: in Matrix);
      entry submit_a_b(newA: in Integer; newB: in Integer);
      entry submit_A(newA: in Vector);
   end T0;
   
   --- The type of the reference to T0
   type T0Ref is access T0;


   
   ---[TP] The type for the tail task of the chain.
   task type TP is
      -- Helper entry to provide this task with a reference to the chain of tasks. Not included in the logic of parallel processing.
      entry init(newTasks: TaskStack);

      -- Actual entries for message passing
      entry submit_MX_B(newMX: in Matrix; newB: in Vector);
      entry submit_ai(newA: in Integer);
      entry submit_bi(newB: in Integer);
   end TP;
   type TPRef is access TP;
   

   -- [T] (Again) Actual definition of the inner node in the task chain
   task type T is 
      -- Not needed in the message logic. Just a way to initialize a task. We prove a new task with a 1) unique number 2) the inner chain of tasks. 3) The head of the chain. 4) The tail of the chain
      entry init(newNum: in Integer;  newTasks: in TaskStack; head : in T0Ref; tail: in TPRef);

      -- Actual messages to pass between Tasks.
      entry submit_MX_B(newMX: in Matrix; newB: in Vector);
      entry submit_Z_D_C_MR(newZ, newD, newC: in Vector; newMR: in Matrix);
      entry submit_ai(newA: in Integer);
      entry submit_bi(newB: in Integer);
      entry submit_a_b(newA: in Integer; newB: in Integer);
      entry submit_A(newA: in Vector);
   end T;
   

   --- Implementation of an inner chain Task
   task body T is
      num: Integer; -- the num of the task
      tasks: TaskStack(1..P-2);  -- the reference to the task chain (task list)
                       -- P-2 because T0 and TP are not in the chain (not in the task list)

      first: T0Ref;  -- the reference to the head of the chain (T0)
      last: TPRef;   -- the reference to the tail of the chain (TP)

   
      -- Buffer variables for temporary use
      ai: Integer;
      aiSmaller: Integer; -- contains min(ai, a(i-1))
      
      bi: Integer;   
      biPlus: Integer;   -- contains bi + b(i-1)
      
      minA: Integer;    -- contains min(a0,a1...aP)
      sumB: Integer;    -- contains b0 + b1 + ... + bP
      
      MX: Matrix(1..N, 1..N);
      MR: Matrix(1..N, 1..N);
      A: Vector(1..N);
      Ah: Vector(1..H);
      MAh: Matrix(1..H, 1..N);
      
      Z: Vector(1..N);
      D: Vector(1..N);
      C: Vector(1..N);
      B: Vector(1..N);

   begin
      -- Just for initializing, no logic in a parallel programming sense, no message passing
      accept init(newNum: in Integer;  newTasks: in TaskStack; head : in T0Ref; tail: in TPRef) do
         num := newNum;
         tasks := newTasks;
         first := head;
         last := tail;
      end init;
      
      --- Waiting for MX and B to be passed from this task's predecessor
      accept submit_MX_B(newMX: in Matrix; newB: in Vector) do
         MX := newMx;
         B := newB;
      end submit_MX_B;
      
      -- Got MX and B, lets propagate it further into the chain to this task's successor
      if (num = P - 2) then
         -- It means there are no tasks in the chain left this is the last one, pass it to the tail of the chain.
         last.submit_MX_B(MX, B);
      else 
         -- Pass MX,B to our successor
         Tasks(num+1).submit_MX_B(MX,B);
      end if;
      
      
      --- Waiting for Z,D,C,MR to be passed from this taks's successor (successor because in the opposite direction: from TP to T0)
      accept submit_Z_D_C_MR(newZ, newD, newC: in Vector; newMR: in Matrix) do
             Z := newZ;
             D := newD;
             C := newC;
             MR := newMR;
      end submit_Z_D_C_MR;
      
      -- Got Z,D,C,MR lets propagate it further to our predecessor
      if (num = 1) then
         -- This task is first in the chain, thus no more tasks left. pass it to the head of the chain
         first.submit_Z_D_C_MR(Z,D,C,MR);
      else 
         -- Pass it to the predecessor
         Tasks(num-1).submit_Z_D_C_MR(Z,D,C,MR);
      end if;
      
      --- 
      -- Computing ai
      ai := computeAi(D, num);
      
      -- Waiting for a(i-1) from our predecessor
      accept submit_ai(newA: in Integer) do
         -- compute the smallest of both (ai and a(i-1))
         aiSmaller := Integer'Min(ai, newA);
      end submit_ai;
      
      -- Propagate the smallest of both ai to our successor
      if (num = P - 2) then
         -- We are the last one, pass to the tail ('last' is the  tail task)
         last.submit_ai(aiSmaller);
      else 
         -- Pass to the next task in the chain
         Tasks(num+1).submit_ai(aiSmaller);
      end if;
      
      
      ---
      -- Computing bi
      bi := computeBi(B, C, num);
      
      -- Waiting for bi from our predecessor
      accept submit_bi(newB: in Integer) do
         -- Summing our bi with the b(i-1) of the predecessor
         biPlus := bi + newB;
      end submit_bi;
      
      -- Pass the sum further
      if (num = P - 2) then
         -- This is  the last one task in the chain, give it to the tail of the task chain
         last.submit_bi(biPlus);
      else 
         -- Or Pass the sum to the next task
         Tasks(num+1).submit_bi(biPlus);
      end if;

      
      --
      -- Wait for a,b to be propagated to us by the successor
      accept submit_a_b(newA: in Integer; newB: in Integer) do
         minA:=newA;
         sumB:=newB;
      end submit_a_b;
      
      -- Propagate further to predecessor or the head of the chain
       if (num = 1) then
         first.submit_a_b(minA,sumB);
      else 
         Tasks(num-1).submit_a_b(minA,sumB);
      end if;
      
      ---
      -- Computing Ah
      MAh := computeMAh(MX, MR, num);
      Ah := computeAh(sumB, Z, D, MAh, minA, num);
      
      -- Waiting for the aggregated matrix of our successors. They've build up the matrix chunk by chunk and passed it to us
      accept submit_A(newA: in Vector) do
         A := newA;
      end submit_A;
        
      -- Now its our turn to place our chunk in the matrix and pass it down to the predecessors(or the head)
      insertVectorChunk(A, Ah, num);
       if (num = 1) then
         first.submit_A(A);
      else 
         Tasks(num-1).submit_A(A);
      end if;
      
      ---      
   end T;
   
  -- Implementation of the head task 
  task body T0 is
      -- The number of the head task is always 0
      num: Integer:=0; 
      -- The list of the other tasks in chain
      tasks: TaskStack(1..P-2);
      
      -- Buffer variables for computations
      ai: Integer;
      bi: Integer;
      
      minA: Integer;
      sumB: Integer;
      MAh: Matrix(1..H, 1..N);
      MX: Matrix(1..N, 1..N);
      MR: Matrix(1..N, 1..N);
      A: Vector(1..N);
      Ah: Vector(1..H);
      Z: Vector(1..N);
      D: Vector(1..N);
      C: Vector(1..N);
      B: Vector(1..N);

   begin
      -- Just for getting the reference to the task chain
      accept init(newTasks: TaskStack) do
         tasks := newTasks;
      end init;
      
      ---
      -- Creating MX, B 
      MX := createMatrix;
      B := createVector;
      -- Passing the matrices upstream to the next task in the chain. He, in turn, will pass it to its successor and so on.
      Tasks(num+1).submit_MX_B(MX, B); -- Actually its always the Task with num=1, since we are the head.
      
      ---
      -- Wait for Z,D,C,MR to be passed down to us from TP. We are the head of the chain, no tasks further.
      accept submit_Z_D_C_MR(newZ, newD, newC: in Vector; newMR: in Matrix) do
             Z := newZ;
             D := newD;
             C := newC;
             MR := newMR;
      end submit_Z_D_C_MR;
      
      ---
      -- Compute ai
      ai := computeAi(D, num);
      
      -- Pass ai to the next task. Each next task will compute ai and compare it with the a(i-1) of its predecessor and so on and pass the minimum to its successor.
      Tasks(num+1).submit_ai(ai);
      
      
      ---
      --- Compute bi and pass it upstream to the next task (same as ai)
      bi := computeBi(B, C, num);

      Tasks(num+1).submit_bi(bi);

      
      --
      -- Wait for the min(a0...aP) and b0+...bP to come down to us from TP.
      accept submit_a_b(newA: in Integer; newB: in Integer) do
         minA:=newA;
         sumB:=newB;
      end submit_a_b;
      
      ---
      -- Compute MAh and Ah
      MAh := computeMAh(MX, MR, num);
      Ah := computeAh(sumB, Z, D, MAh, minA, num);
      
      -- Wait for the built up matrix A. This matrix will contain the matrix filled with all the chunks of all tasks from T1..TP. Only one chunk left to insert: our chunk.
      accept submit_A(newA: in Vector) do
         A := newA;
      end submit_A;
      
      -- Insert our last chunk and complete the matrix fully.
      insertVectorChunk(A, Ah, num);
      
      -- We have completed matrix A, print it out
      outputVector(A);
        
   end T0;
   
   -- The implementation of the tail task
   task body TP is
      -- the number of the tail is always P - 1
      num: Integer := P - 1;

      -- the reference to the inner chain of the tasks
      tasks: TaskStack(1..P-2);
      
      -- Helper variables for computations
      ai: Integer;
      bi: Integer;
      
      minA: Integer;
      sumB: Integer;
      
      MX: Matrix(1..N, 1..N);
      MR: Matrix(1..N, 1..N);
      MAh: Matrix(1..H, 1..N);
      A: Vector(1..N);
      Ah: Vector(1..H);
      Z: Vector(1..N);
      D: Vector(1..N);
      C: Vector(1..N);
      B: Vector(1..N);

   begin
      
      -- Helper entry to get reference to the task list
      accept init(newTasks: TaskStack) do
         tasks := newTasks;
      end init;
      
     
      ---
      -- Waiting for the MX, B to come up to us from the T0(head)
     accept submit_MX_B(newMX: in Matrix; newB: in Vector) do
         MX := newMx;
         B := newB;
      end submit_MX_B;
      
      ---
      -- Create Z,D,C,MR
      Z := createVector;
      D := createVector;
      C := createVector;
      MR := createMatrix;
      
      -- Pass them downstream to the predecessor. Each task will pass it to its predecessor and so on until the head T0.
      Tasks(num-1).submit_Z_D_C_MR(Z, D, C, MR);
      
      
      ---
      -- Compute our ai
      ai := computeAi(D, num);
      
      -- Wait for the a(i-1) of the previous task. This a(i-1) willl contain the minimum of all ai's from the previous task except our ai
      accept submit_ai(newA: in Integer) do
         minA := Integer'Min(ai, newA); -- this basically is min(a0....aP), because newA=min(a(P-1), a(P-2)) and so on...
      end submit_ai;
      
      
      
      ---
      bi := computeBi(B, C, num);
      
      -- Wait for b(i-1) of the previos task, which is the sum of all bi's up to bP.
      accept submit_bi(newB: in Integer) do
         sumB := bi + newB; -- this is basically the sum of b0 + b1 + b2 + ... bP
      end submit_bi;
      

      -- Since we (the tail of the chain) have the minimum of all a's and the sum of all b's pass them down the chain to propagate.
      Tasks(num-1).submit_a_b(minA, sumB);

      ---
      MAh := computeMAh(MX, MR, num);
      Ah := computeAh(sumB, Z, D, MAh, minA, num);
        
      -- Compute Ah, we are the first one to start building up the matrix (A). Insert our chunk in the empty matrix and pass it downstream to the T0, where it will be fully completed.
      insertVectorChunk(A, Ah, num);
      Tasks(num-1).submit_A(A);
      
      
      ---
      
   end TP;
   

   -- Declare the chain of the task (but not filling out yet)
   Tasks : TaskStack(1..P-2);
   -- The actual created instances of the head and tail tasks
   T0_HEAD: T0Ref := new T0;
   TP_TAIL: TPRef := new TP;
   
begin
   
   -- Filling the list(chain) of the tasks with actual instances.
   for i in Tasks'Range(1) loop
      Tasks(i) := new T;
   end loop;
   
   -- Provide the head and the tail with the list of the inner tasks.
   T0_HEAD.init(Tasks);
   TP_TAIL.init(Tasks);


   -- Provide for each inner chain task its number, the list of other tasks, the head and the tail of the chain.
   for i in Tasks'Range(1) loop
      Tasks(i).init(i, Tasks, T0_HEAD, TP_TAIL);
   end loop;
end Main;
