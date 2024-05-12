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


   type AbstractTask is task interface;
   -- Task specifications for parallel processing
   
   type AbstractTask_Ref is access AbstractTask;
   

   task type T(prev: AbstractTask_Ref) is new AbstractTask with
      entry MX_B(newMX: in Matrix; newB: in Vector);
      --  entry Z_D_C_MR(newMA: in Matrix);
      --  entry ai(newA: in Integer);
      --  entry bi(newB: in Integer);
      --  entry a_b(newA: in Integer; newB: in Integer);
      --  entry Ah(newAh: in Vector);
   end T;
   
   

   task body T is
      MA: Matrix(1..N, 1..N);
      V: Vector(1..N);
   begin
      Put_line("Process 1 is started");

      --  Put_Line(Item => Integer'Image(prev));
      
      --  accept MX_B(newMX: in Matrix; newB: in Vector) do
      --     Put_line("Process 1 is started");
      --  end MX_B;
       
      
      MA := createMatrix;
      V := createVector;
      Put_line("Process 1 is ended");

      outputMatrix(MA);
      outputVector(V);

      Put(Item => Integer'Image(vectorByVector(V,V)) & " ");
      
   end T;
   
   T1: T := new T(null);
   T2: T(T1);
   
begin
   null;
end Main;
