with Ada.Text_IO; use Ada.Text_IO;
with Ada.Characters.Latin_1; use Ada.Characters.Latin_1;

procedure main is
   N: Integer := 16;
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
   function chunkMatrix(MO: Matrix; chunkNum: Integer) return Matrix is
      M: Matrix(1..N, 1..H);
   begin
      for i in 1 .. N loop
         for j in 1 .. H loop
            M(i, j) := MO(i, H * chunkNum + j);
         end loop;
      end loop;
      return M;
   end chunkMatrix;

   function chunkVector(VO: Vector; chunkNum: Integer) return Vector is
         V: Vector(1..H);
      begin
         for i in 1 .. H loop
            V(i) := VO(H * chunkNum + i);
         end loop;
         return V;
      end chunkVector;

   procedure insertChunk(VO: out Vector; V: Vector; chunkNum: Integer) is
      begin
         for i in 1 .. H loop
            VO(H * chunkNum + i) := V(i);
         end loop;
      end insertChunk;

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

   function computeZh(X: Vector; MA: Matrix; MSh: Matrix; m0: Integer; Fh: Vector) return Vector is
      MA_MSh: Matrix(1..N, 1..H);
      X_MA_MSh: Vector(1..H);
      m_Fh: Vector(1..H);
   begin
      Put_Line("Computing Zh");
      MA_MSh := matrixByMatrix(MA, Msh);    -- (NxN) x (NxH) -> (NxH)
      X_MA_MSh := vectorByMatrix(X, transpose(MA_MSh));     -- transposed(NxH)-> (H x N) x (N x 1) -> (H x 1)
      m_Fh := scalarByVector(m0, Fh);

      return vectorPlusVector(X_MA_MSh, m_Fh); -- (Hx1) + (Hx1) = (Hx1)
   end computeZh;

-- Task specifications for parallel processing

   task T1 is
      entry out_MA (newMA: out Matrix);
   end T1;

   task T2 is
      entry out_X_MA_MS_F_in_chunk(newX: out Vector; newMA: out Matrix; MSh: out Matrix; Fh: out Vector;
                                   chunkNum: in Integer);
      entry in_m_Z_chunk(new_m: in Integer; newZ: in Vector; chunkNum: in Integer);
   end T2;
   --
   task T3 is
      entry out_MS(newMS: out Matrix);
   end T3;

   task T4 is

   end T4;

   task T5 is

   end T5;

   task T6 is

   end T6;

   task T7 is
   end T7;

   task T8 is
      entry out_F(newF: out Vector);
   end T8;

   --TASK 1--

   task body T1 is
      MA: Matrix(1..N, 1..N);
      X: Vector(1..N);
      MS1: Matrix(1..N, 1..H);
      F1: Vector(1..H);
      m1: Integer;
      Z1: Vector(1..H);
   begin
      Put_line("Process 1 is started");

      MA := createMatrix;

      accept out_MA (newMA: out Matrix) do
         newMA := MA;

      end out_MA;


      T2.out_X_MA_MS_F_in_chunk(X, MA, MS1, F1, 0);
      m1 := min(chunkVector(X, 0));
      Z1 := computeZh(X, MA, MS1, m1, F1);
      T2.in_m_Z_chunk(m1, Z1, 0);

      Put_line("Process 1 is ended");

   end T1;

    --TASK 2--
   task body T2 is
      X: Vector(1..N);
      m2: Integer;

      MA: Matrix(1..N, 1..N);
      MS: Matrix(1..N, 1..N);
      F: Vector(1..N);
      m: Integer;
      Z2: Vector(1..H);
      Z: Vector(1..N);
   begin
      Put_line("Process 2 is started");
   -- Initialize X vector
      X := createVector;

      m2 := min(chunkVector(X, 1));
-- Retrieve MA, MS, and F matrices/vectors from Tasks 1, 3, and 8
      T1.out_MA(MA);
      T3.out_MS(MS);
      T8.out_F(F);

        -- Share X, MA, MS, and F with other tasks

      for J in 1..P-1 loop
         accept out_X_MA_MS_F_in_chunk(newX: out Vector; newMA: out Matrix; MSh: out Matrix; Fh: out Vector;
                                    chunkNum: in Integer) do

            newMA := MA;
            newX := X;
            MSh := chunkMatrix(MS, chunkNum);
            Fh := chunkVector(F, chunkNum);

         end out_X_MA_MS_F_in_chunk;
      end loop;
  -- Receive m values and Zh vectors from other tasks
      m := m2;
      for J in 1..P-1 loop
         accept in_m_Z_chunk(new_m: in Integer; newZ: in Vector; chunkNum: in Integer) do

            if new_m < m then
               m := new_m;
            end if;
            insertChunk(Z, newZ, chunkNum);
         end in_m_Z_chunk;
      end loop;
 -- Compute Zh for the local chunk and share the result with Task 1
      Z2 := computeZh(X, MA, chunkMatrix(MS, 1), m2, chunkVector(F, 1));
      insertChunk(Z, Z2, 1);

      outputVector(Z);
      Put_line("Process 2 has finished");
   end T2;

   --TASK 3--

   task body T3 is
      MS: Matrix(1..N, 1..N);
      MA: Matrix(1..N, 1..N);
      X: Vector(1..N);
      MS3: Matrix(1..N, 1..H);
      F3: Vector(1..H);
      m3: Integer;
      Z3: Vector(1..H);
   begin

      Put_line("Process 3 is started");
      MS := createMatrix;

      accept out_MS(newMS: out Matrix) do
         newMS:= MS;

      end out_MS;

      T2.out_X_MA_MS_F_in_chunk(X, MA, MS3, F3, 2);
      m3 := min(chunkVector(X, 2));
      Z3 := computeZh(X, MA, MS3, m3, F3);
      T2.in_m_Z_chunk(m3, Z3, 2);

      Put_line("Process 4 has finished");
   end T3;

   task body T4 is
      MA: Matrix(1..N, 1..N);
      X: Vector(1..N);
      MS4: Matrix(1..N, 1..H);
      F4: Vector(1..H);
      m4: Integer;
      Z4: Vector(1..H);
   begin
      Put_line("Process 4 is started");

      T2.out_X_MA_MS_F_in_chunk(X, MA, MS4, F4, 3);
      m4 := min(chunkVector(X, 3));
      Z4 := computeZh(X, MA, MS4, m4, F4);
      T2.in_m_Z_chunk(m4, Z4, 3);

      Put_line("Process 4 has finished");
   end T4;


   task body T5 is
      MA: Matrix(1..N, 1..N);
      X: Vector(1..N);
      MS5: Matrix(1..N, 1..H);
      F5: Vector(1..H);
      m5: Integer;
      Z5: Vector(1..H);
   begin
      Put_line("Process 5 is started");

      T2.out_X_MA_MS_F_in_chunk(X, MA, MS5, F5, 4);
      m5 := min(chunkVector(X, 5));
      Z5 := computeZh(X, MA, MS5, m5, F5);
      T2.in_m_Z_chunk(m5, Z5, 4);

      Put_line("Process 5 has finished");
   end T5;

   task body T6 is
      MA: Matrix(1..N, 1..N);
      X: Vector(1..N);
      MS6: Matrix(1..N, 1..H);
      F6: Vector(1..H);
      m6: Integer;
      Z6: Vector(1..H);
   begin
      Put_line("Process 6 is started");

      T2.out_X_MA_MS_F_in_chunk(X, MA, MS6, F6, 5);
      m6 := min(chunkVector(X, 5));
      Z6 := computeZh(X, MA, MS6, m6, F6);
      T2.in_m_Z_chunk(m6, Z6, 5);

      Put_line("Process 6 has finished");
   end T6;

   task body T7 is
      MA: Matrix(1..N, 1..N);
      X: Vector(1..N);
      MS7: Matrix(1..N, 1..H);
      F7: Vector(1..H);
      m7: Integer;
      Z7: Vector(1..H);
   begin
      Put_line("Process 7 is started");

      T2.out_X_MA_MS_F_in_chunk(X, MA, MS7, F7, 6);
      m7 := min(chunkVector(X, 6));
      Z7 := computeZh(X, MA, MS7, m7, F7);
      T2.in_m_Z_chunk(m7, Z7, 6);

      Put_line("Process 7 has finished");
   end T7;

   task body T8 is
      F: Vector(1..N);
      MA: Matrix(1..N, 1..N);
      X: Vector(1..N);
      MS8: Matrix(1..N, 1..H);
      F8: Vector(1..H);
      m8: Integer;
      Z8: Vector(1..H);
   begin

      Put_line("Process 8 is started");
      F := createVector;

      accept out_F(newF: out Vector) do
         newF := F;

      end out_F;

      T2.out_X_MA_MS_F_in_chunk(X, MA, MS8, F8, 7);
      m8 := min(chunkVector(X, 7));
      Z8 := computeZh(X, MA, MS8, m8, F8);
      T2.in_m_Z_chunk(m8, Z8, 7);

      Put_line("Process 8 has finished");

   end T8;


begin
   null;
end main;
