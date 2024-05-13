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
   
   task type T is
      entry init(newPrev: in TRef; newNext: in TRef; newNum: in Integer);
      entry submit_MX_B(newMX: in Matrix; newB: in Vector);
      entry submit_Z_D_C_MR(newMA: in Matrix);
      entry submit_ai(newA: in Integer);
      entry submit_bi(newB: in Integer);
      entry submit_a_b(newA: in Integer; newB: in Integer);
      entry submit_Ah(newAh: in Vector);
   end T;

   
   task body T is
      
      prev: TRef;
      next: TRef;
      num: Integer;
   
      ai: Integer;
      bi: Integer;
      
      minA: Integer;
      sumB: Integer;
      
      MX: Matrix(1..N, 1..N);
      DC: Matrix(1..N, 1..N);
      MR: Matrix(1..N, 1..N);
      A: Vector(1..N);
      Ah: Vector(1..H);
      Z: Vector(1..N);
      Zh: Vector(1..H);
      D: Vector(1..N);
      Dh: Vector(1..H);
      C: Vector(1..N);
      Ch: Vector(1..H);
      B: Vector(1..N);
      Bh: Vector(1..H);

   begin
      
      accept init(newPrev: in TRef; newNext: in TRef; newNum: in Integer) do
         prev := newPrev;
         next := newNext;
         num := newNum;
      end init;
      Put_Line("Launched  T" & Integer'Image(num));
      
      
      -- If T1 create, else receive
      if prev = null then
         MX := createMatrix;
         B := createVector;
      else -- T2..TP
        accept submit_MX_B(newMX: in Matrix; newB: in Vector) do
            Put_Line("Got MX_B:" & Integer'Image(num));
            MX := newMx;
            B := newB;
         end submit_MX_B;
      end if;

      -- IF not T1..TP-1
      if next /= null then
         next.submit_MX_B(MX, B);
      end if;

      
      --  accept submit_Z_D_C_MR(newMA: in Matrix) do
      --  Put_Line("");
      --  end submit_Z_D_C_MR;
      --  
      --  accept submit_ai(newA: in Integer) do
      --  Put_Line("");
      --  end submit_ai;
      --  
      --  accept submit_bi(newB: in Integer) do
      --  Put_Line("");
      --  end submit_bi;
      --  
      --  accept submit_a_b(newA: in Integer; newB: in Integer) do
      --  Put_Line("");
      --  end submit_a_b;
      --  
      --  accept submit_Ah(newAh: in Vector) do
      --     Put_Line("");
      --  end submit_Ah;
      --  
   end T;
   
   T1: TRef := new T;
   T2: TRef := new T;
   TP: TRef := new T;
   
begin
   
   T1.init(null, T2, 0);
   T2.init(T1, TP, 1);
   TP.init(T2, null, 2);

end Main;
