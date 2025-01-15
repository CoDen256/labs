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

begin
   --  Insert code here.
   null;
end Main;
